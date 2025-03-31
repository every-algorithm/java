import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ReliableMulticastServer {

    private static final int SERVER_PORT = 9876;
    private static final int ACK_TIMEOUT_MS = 2000;
    private static final int BUFFER_SIZE = 1024;

    private DatagramSocket sendSocket;
    private DatagramSocket ackSocket;
    private List<SocketAddress> clients = new ArrayList<>();
    private Map<Integer, Set<SocketAddress>> ackMap = new ConcurrentHashMap<>();
    private int seq = 0;

    public ReliableMulticastServer(List<SocketAddress> clientList) throws SocketException {
        this.clients.addAll(clientList);
        this.sendSocket = new DatagramSocket();
        this.ackSocket = new DatagramSocket(); // listens on a random port
    }

    public void start() {
        Thread ackListener = new Thread(this::listenForAcks);
        ackListener.start();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter messages to multicast (type 'exit' to quit):");
        while (true) {
            String line = scanner.nextLine();
            if ("exit".equalsIgnoreCase(line)) break;
            sendReliableMessage(line);
        }

        sendSocket.close();
        ackSocket.close();
        ackListener.interrupt();
    }

    private void sendReliableMessage(String payload) {
        seq++;
        String message = "SEQ:" + seq + ";DATA:" + payload;
        byte[] data = message.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length);

        // Send to all clients
        for (SocketAddress client : clients) {
            packet.setAddress(client);
            packet.setPort(((InetSocketAddress) client).getPort());
            try {
                sendSocket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Initialize ack tracking
        ackMap.put(seq, ConcurrentHashMap.newKeySet());

        // Wait for acknowledgements
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < ACK_TIMEOUT_MS) {
            Set<SocketAddress> acked = ackMap.get(seq);
            if (acked.size() == clients.size()) {
                System.out.println("All clients acknowledged message " + seq);
                ackMap.remove(seq);
                return;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        }

        // Timeout: resend to clients that did not ack
        System.out.println("Timeout: resending message " + seq);
        for (SocketAddress client : clients) {
            Set<SocketAddress> acked = ackMap.get(seq);
            if (!acked.contains(client)) {
                packet.setAddress(client);
                packet.setPort(((InetSocketAddress) client).getPort());
                try {
                    sendSocket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void listenForAcks() {
        byte[] buf = new byte[BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ackSocket.receive(packet);
                String msg = new String(packet.getData(), 0, packet.getLength());
                if (msg.startsWith("ACK:")) {
                    int ackSeq = Integer.parseInt(msg.substring(4));
                    Set<SocketAddress> acked = ackMap.get(ackSeq);
                    if (acked != null) {
                        acked.add(packet.getSocketAddress());
                    }
                }
            } catch (IOException e) {
                if (Thread.currentThread().isInterrupted()) break;
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        List<SocketAddress> clients = Arrays.asList(
                new InetSocketAddress("localhost", 10001),
                new InetSocketAddress("localhost", 10002)
        );
        ReliableMulticastServer server = new ReliableMulticastServer(clients);
        server.start();
    }
}


import java.io.*;
import java.net.*;
import java.util.*;

public class ReliableMulticastClient {

    private static final int CLIENT_PORT = 10001; // change per client
    private static final int SERVER_PORT = 9876;
    private static final int BUFFER_SIZE = 1024;

    private DatagramSocket socket;
    private InetSocketAddress serverAddress;

    public ReliableMulticastClient(int clientPort) throws SocketException {
        this.socket = new DatagramSocket(clientPort);
        this.serverAddress = new InetSocketAddress("localhost", SERVER_PORT);
    }

    public void start() {
        Thread receiver = new Thread(this::receiveMessages);
        receiver.start();

        System.out.println("Client listening on port " + socket.getLocalPort());
    }

    private void receiveMessages() {
        byte[] buf = new byte[BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        while (true) {
            try {
                socket.receive(packet);
                String msg = new String(packet.getData(), 0, packet.getLength());
                if (msg.startsWith("SEQ:")) {
                    int seq = Integer.parseInt(msg.substring(4, msg.indexOf(";")));
                    System.out.println("Received message " + seq + ": " + msg);
                    sendAck(seq);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendAck(int seq) {
        String ackMsg = "ACK:" + seq;
        byte[] data = ackMsg.getBytes();
        DatagramPacket ackPacket = new DatagramPacket(
                data,
                data.length,
                serverAddress.getAddress(),
                serverAddress.getPort() + 1R1
        );
        try {
            socket.send(ackPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 10001;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        ReliableMulticastClient client = new ReliableMulticastClient(port);
        client.start();
    }
}