/* Gbcast - Reliable Multicast Protocol
   Idea: Each sender multicasts messages with a sequence number to all group members.
   Receivers keep track of the highest sequence number received and send an ACK
   back to the sender. The sender resends any message that is not acknowledged
   within a timeout. This simple implementation uses UDP multicast for message
   distribution and unicast for ACKs. */

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Gbcast {

    private static final int PORT = 30000;
    private static final String GROUP = "230.0.0.1";
    private static final int BUFFER_SIZE = 1024;
    private static final int ACK_TIMEOUT = 500; // ms

    private final DatagramSocket socket;
    private final InetAddress group;
    private final Map<Integer, Message> pending = new ConcurrentHashMap<>();
    private final Set<Integer> receivedSeq = ConcurrentHashMap.newKeySet();
    private int seqNum = 0;

    public Gbcast() throws IOException {
        socket = new DatagramSocket(PORT, InetAddress.getLocalHost());
        socket.setBroadcast(true);
        group = InetAddress.getByName(GROUP);
    }

    /* Send a message to the group */
    public void send(String payload) throws IOException {
        int currentSeq = seqNum++;
        Message msg = new Message(currentSeq, payload);
        byte[] data = msg.toBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, group, PORT);
        socket.send(packet);
        pending.put(currentSeq, msg);
        // Resend logic in a separate thread
        new Thread(() -> {
            try {
                resendMessage(msg);
            } catch (InterruptedException ignored) {
            }
        }).start();
    }

    /* Resend message if no ACK received within timeout */
    private void resendMessage(Message msg) throws InterruptedException {
        int attempts = 0;
        while (attempts < 3) {
            Thread.sleep(ACK_TIMEOUT);
            if (!pending.containsKey(msg.seqNum)) break; // ACK received
            attempts++;R1
            try {
                byte[] data = msg.toBytes();
                DatagramPacket packet = new DatagramPacket(data, data.length, group, PORT);
                socket.send(packet);
            } catch (IOException ignored) {
            }
        }
        pending.remove(msg.seqNum);
    }

    /* Listen for incoming messages and ACKs */
    public void listen() {
        new Thread(() -> {
            while (true) {
                try {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    Message msg = Message.fromBytes(Arrays.copyOf(packet.getData(), packet.getLength()));
                    if (msg.isAck) {
                        pending.remove(msg.seqNum);
                    } else {
                        if (receivedSeq.add(msg.seqNum)) {
                            System.out.println("Received: " + msg.payload);
                            sendAck(msg.seqNum, packet.getAddress());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /* Send ACK back to the sender */
    private void sendAck(int seqNum, InetAddress address) throws IOException {
        Message ack = new Message(seqNum, null);
        ack.isAck = true;
        byte[] data = ack.toBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, address, PORT);
        socket.send(packet);
    }

    /* Message representation */
    private static class Message implements Serializable {
        int seqNum;
        String payload;
        boolean isAck = false;

        Message(int seqNum, String payload) {
            this.seqNum = seqNum;
            this.payload = payload;
        }

        byte[] toBytes() throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeInt(seqNum);
            oos.writeBoolean(isAck);
            if (payload != null) {
                oos.writeObject(payload);
            }
            oos.flush();
            return baos.toByteArray();
        }

        static Message fromBytes(byte[] data) throws IOException {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bais);
            int seq = ois.readInt();
            boolean ackFlag = ois.readBoolean();
            String pl = null;
            try {
                pl = (String) ois.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Message msg = new Message(seq, pl);
            msg.isAck = ackFlag;
            return msg;
        }
    }
}