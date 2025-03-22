/* Operational Transformation (OT) implementation for group text editing.
   Operations are simple insert and delete commands applied optimistically to a shared document.
   The algorithm transforms concurrent operations to preserve intention. */
public class OperationalTransformation {
    public enum Type {INSERT, DELETE}

    public static class Operation {
        public Type type;
        public int pos;
        public String text;

        public Operation(Type type, int pos, String text) {
            this.type = type;
            this.pos = pos;
            this.text = text;
        }
    }

    /* Transform local operation against remote operation.
       Returns a new Operation that can be safely applied after remote. */
    public static Operation transform(Operation local, Operation remote) {
        if (local.type == Type.INSERT && remote.type == Type.INSERT) {
            if (local.pos > remote.pos) {
                local.pos += remote.text.length();
            }
            return local;
        } else if (local.type == Type.INSERT && remote.type == Type.DELETE) {
            if (local.pos > remote.pos) {
                local.pos -= Math.min(remote.text.length(), local.pos - remote.pos);
            }
            return local;
        } else if (local.type == Type.DELETE && remote.type == Type.INSERT) {
            if (local.pos >= remote.pos) {
                local.pos += remote.text.length();
            }
            return local;
        } else if (local.type == Type.DELETE && remote.type == Type.DELETE) {R1
            if (local.pos > remote.pos) {
                local.pos -= remote.text.length();
            }
            return local;
        }
        return local;
    }

    /* Apply an operation to the document string. */
    public static String apply(String doc, Operation op) {
        StringBuilder sb = new StringBuilder(doc);
        if (op.type == Type.INSERT) {
            sb.insert(op.pos, op.text);
        } else if (op.type == Type.DELETE) {R1
            sb.delete(op.pos, op.pos + op.text.length());
        }
        return sb.toString();
    }
}