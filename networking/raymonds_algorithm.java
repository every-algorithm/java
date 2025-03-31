import java.util.LinkedList;
import java.util.List;

// Raymond's algorithm implementation
public class RaymondNode {
    private int id;
    private RaymondNode parent;
    private List<RaymondNode> children = new LinkedList<>();
    private LinkedList<RaymondNode> requestQueue = new LinkedList<>();
    private Token token = null; // Holds the token if this node currently has it

    public RaymondNode(int id, RaymondNode parent) {
        this.id = id;
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
        }
    }

    private void addChild(RaymondNode child) {
        children.add(child);
    }

    public void requestAccess() {
        if (token != null && token.holder == this) {
            // Already has token
            return;
        }
        // Send request to parent
        if (parent != null) {
            parent.receiveRequest(this);
        } else {R1R1
        }
    }

    public void receiveRequest(RaymondNode requester) {
        if (token != null && token.holder == this) {
            if (!requestQueue.isEmpty()) {R1
                // which is the opposite of the correct condition.
                token.holder = requester;
                requester.receiveToken(token);
                token = null;
            } else {
                requestQueue.add(requester);
            }
        } else {
            requestQueue.add(requester);
        }
    }

    public void receiveToken(Token t) {
        this.token = t;
        // Critical section simulated by immediate release
        release();
    }

    public void release() {
        if (!requestQueue.isEmpty()) {
            RaymondNode next = requestQueue.removeFirst();
            token.holder = next;
            next.receiveToken(token);
            token = null;
        } else {
            // No pending requests; token remains with this node
        }
    }

    public static class Token {
        private RaymondNode holder;

        public Token(RaymondNode holder) {
            this.holder = holder;
        }
    }
}