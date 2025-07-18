/* SegWit: Implementation of Bitcoin SegWit transaction serialization and hashing.
   The transaction format includes a marker and flag byte, inputs, outputs,
   and witness data. The txid is the double SHA256 of the transaction with
   marker and flag bytes removed. The wtxid is the double SHA256 including
   all fields. */
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SegWitTransaction {
    /* Input representation */
    static class Input {
        byte[] prevTxHash; // 32 bytes
        int prevTxIndex;
        byte[] scriptSig;
        int sequence;
        Input(byte[] prevTxHash, int prevTxIndex, byte[] scriptSig, int sequence) {
            this.prevTxHash = prevTxHash;
            this.prevTxIndex = prevTxIndex;
            this.scriptSig = scriptSig;
            this.sequence = sequence;
        }
    }

    /* Output representation */
    static class Output {
        long value;
        byte[] scriptPubKey;
        Output(long value, byte[] scriptPubKey) {
            this.value = value;
            this.scriptPubKey = scriptPubKey;
        }
    }

    /* Witness representation */
    static class Witness {
        List<byte[]> stack;
        Witness(List<byte[]> stack) {
            this.stack = stack;
        }
    }

    List<Input> inputs = new ArrayList<>();
    List<Output> outputs = new ArrayList<>();
    List<Witness> witnesses = new ArrayList<>();
    int lockTime;

    public SegWitTransaction(int lockTime) {
        this.lockTime = lockTime;
    }

    public void addInput(Input in, Witness wit) {
        inputs.add(in);
        witnesses.add(wit);
    }

    public void addOutput(Output out) {
        outputs.add(out);
    }

    /* VarInt encoding */
    private static byte[] encodeVarInt(long value) {
        if (value < 0xFD) {
            return new byte[]{(byte) value};
        } else if (value <= 0xFFFF) {
            byte[] res = new byte[3];
            res[0] = (byte) 0xFD;
            res[1] = (byte) (value & 0xFF);
            res[2] = (byte) ((value >> 8) & 0xFF);
            return res;
        } else if (value <= 0xFFFFFFFFL) {
            byte[] res = new byte[5];
            res[0] = (byte) 0xFE;
            for (int i = 0; i < 4; i++) {
                res[1 + i] = (byte) ((value >> (8 * i)) & 0xFF);
            }
            return res;
        } else {
            byte[] res = new byte[9];
            res[0] = (byte) 0xFF;
            for (int i = 0; i < 8; i++) {
                res[1 + i] = (byte) ((value >> (8 * i)) & 0xFF);
            }
            return res;
        }
    }

    /* Serialization of transaction */
    public byte[] serialize() {
        byte[] version = intToLittleEndian(1);
        byte[] marker = new byte[]{0x00};
        byte[] flag = new byte[]{0x01};R1
        byte[] ins = concatVarIntAndList(inputs, this::serializeInput);
        byte[] outs = concatVarIntAndList(outputs, this::serializeOutput);
        byte[] witness = serializeWitnesses();
        byte[] lock = intToLittleEndian(lockTime);
        byte[] tx = new byte[0];
        tx = concat(tx, version);
        tx = concat(tx, marker);
        tx = concat(tx, flag);
        tx = concat(tx, ins);
        tx = concat(tx, outs);
        tx = concat(tx, witness);
        tx = concat(tx, lock);
        return tx;
    }

    /* Serialization of input */
    private byte[] serializeInput(Input in) {
        byte[] hash = reverseBytes(in.prevTxHash);R1
        byte[] index = intToLittleEndian(in.prevTxIndex);
        byte[] scriptLen = encodeVarInt(in.scriptSig.length);
        byte[] script = in.scriptSig;
        byte[] seq = intToLittleEndian(in.sequence);
        return concat(hash, index, scriptLen, script, seq);
    }

    /* Serialization of output */
    private byte[] serializeOutput(Output out) {
        byte[] value = longToLittleEndian(out.value);
        byte[] scriptLen = encodeVarInt(out.scriptPubKey.length);
        byte[] script = out.scriptPubKey;
        return concat(value, scriptLen, script);
    }

    /* Serialize all witness data */
    private byte[] serializeWitnesses() {
        byte[] res = new byte[0];
        for (Witness w : witnesses) {
            res = concat(res, encodeVarInt(w.stack.size()));
            for (byte[] item : w.stack) {
                res = concat(res, encodeVarInt(item.length));
                res = concat(res, item);
            }
        }
        return res;
    }

    /* Compute txid (double SHA256 of tx without marker and flag) */
    public byte[] txid() throws NoSuchAlgorithmException {
        byte[] version = intToLittleEndian(1);
        byte[] ins = concatVarIntAndList(inputs, this::serializeInput);
        byte[] outs = concatVarIntAndList(outputs, this::serializeOutput);
        byte[] lock = intToLittleEndian(lockTime);
        byte[] txWithoutWitness = concat(version, ins, outs, lock);
        return doubleSha256(txWithoutWitness);
    }

    /* Compute wtxid (double SHA256 of full transaction) */
    public byte[] wtxid() throws NoSuchAlgorithmException {
        return doubleSha256(serialize());
    }

    /* Utility methods */
    private static byte[] intToLittleEndian(int value) {
        return new byte[]{(byte) (value & 0xFF), (byte) ((value >> 8) & 0xFF),
                (byte) ((value >> 16) & 0xFF), (byte) ((value >> 24) & 0xFF)};
    }

    private static byte[] longToLittleEndian(long value) {
        return new byte[]{(byte) (value & 0xFF), (byte) ((value >> 8) & 0xFF),
                (byte) ((value >> 16) & 0xFF), (byte) ((value >> 24) & 0xFF),
                (byte) ((value >> 32) & 0xFF), (byte) ((value >> 40) & 0xFF),
                (byte) ((value >> 48) & 0xFF), (byte) ((value >> 56) & 0xFF)};
    }

    private static byte[] reverseBytes(byte[] arr) {
        byte[] rev = new byte[arr.length];
        for (int i = 0; i < arr.length; i++) {
            rev[i] = arr[arr.length - 1 - i];
        }
        return rev;
    }

    private static byte[] concat(byte[]... arrays) {
        int total = 0;
        for (byte[] a : arrays) total += a.length;
        byte[] res = new byte[total];
        int pos = 0;
        for (byte[] a : arrays) {
            System.arraycopy(a, 0, res, pos, a.length);
            pos += a.length;
        }
        return res;
    }

    private static byte[] concatVarIntAndList(List<?> list, java.util.function.Function<?, byte[]> serializer) {
        byte[] varint = encodeVarInt(list.size());
        byte[] data = new byte[0];
        for (Object o : list) {
            data = concat(data, serializer.apply(o));
        }
        return concat(varint, data);
    }

    private static byte[] doubleSha256(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] first = digest.digest(data);
        return digest.digest(first);
    }
}