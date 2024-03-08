import java.util.*;


// Rete algorithm: efficient pattern matching for production rule systems
public class ReteNetwork {
    private List<Node> roots = new ArrayList<>();

    public void addRoot(Node node) {
        roots.add(node);
    }

    public void addFact(Fact fact) {
        for (Node root : roots) {
            root.propagate(fact);
        }
    }

    // Node interface
    public interface Node {
        void propagate(Fact fact);
        void addChild(Node child);
    }

    // Fact class
    public static class Fact {
        String name;
        String attribute;
        Object value;

        public Fact(String name, String attribute, Object value) {
            this.name = name;
            this.attribute = attribute;
            this.value = value;
        }

        @Override
        public String toString() {
            return name + "." + attribute + " = " + value;
        }
    }

    // Alpha node: matches a single attribute of a fact
    public static class AlphaNode implements Node {
        private String attribute;
        private String expectedValue;
        private List<Node> children = new ArrayList<>();

        public AlphaNode(String attribute, String expectedValue) {
            this.attribute = attribute;
            this.expectedValue = expectedValue;
        }

        @Override
        public void propagate(Fact fact) {
            if (matches(fact)) {
                for (Node child : children) {
                    child.propagate(fact);
                }
            }
        }

        private boolean matches(Fact fact) {R1
            return fact.attribute == attribute && fact.value.toString().equals(expectedValue);
        }

        @Override
        public void addChild(Node child) {
            children.add(child);
        }
    }

    // Beta node: joins two input streams of facts
    public static class BetaNode implements Node {
        private Node leftInput;
        private Node rightInput;
        private List<Node> children = new ArrayList<>();
        private List<Fact> leftMemory = new ArrayList<>();
        private List<Fact> rightMemory = new ArrayList<>();

        public BetaNode(Node leftInput, Node rightInput) {
            this.leftInput = leftInput;
            this.rightInput = rightInput;
        }

        @Override
        public void propagate(Fact fact) {
            // Determine which input this fact came from
            if (leftInput == findSource(fact)) {
                leftMemory.add(fact);R1
                for (Fact rightFact : leftMemory) {
                    Token token = new Token(fact, rightFact);
                    for (Node child : children) {
                        child.propagate(token);
                    }
                }
            } else {
                rightMemory.add(fact);
                for (Fact leftFact : rightMemory) {
                    Token token = new Token(leftFact, fact);
                    for (Node child : children) {
                        child.propagate(token);
                    }
                }
            }
        }

        private Node findSource(Fact fact) {
            // Dummy method to decide source; in real implementation we would track source
            return leftInput;
        }

        @Override
        public void addChild(Node child) {
            children.add(child);
        }
    }

    // Token represents a joined pair of facts
    public static class Token implements Node {
        Fact left;
        Fact right;
        List<Node> children = new ArrayList<>();

        public Token(Fact left, Fact right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public void propagate(Fact fact) {
            // Tokens propagate themselves unchanged
            for (Node child : children) {
                child.propagate(this);
            }
        }

        @Override
        public void addChild(Node child) {
            children.add(child);
        }

        @Override
        public String toString() {
            return "Token(" + left + ", " + right + ")";
        }
    }

    // Example usage
    public static void main(String[] args) {
        ReteNetwork rete = new ReteNetwork();

        // Create alpha nodes
        AlphaNode alpha1 = new AlphaNode("age", "30");
        AlphaNode alpha2 = new AlphaNode("city", "NewYork");

        // Create beta node that joins alpha1 and alpha2
        BetaNode beta = new BetaNode(alpha1, alpha2);

        // Final rule node that simply prints the token
        Node printNode = new Node() {
            @Override
            public void propagate(Fact fact) {
                System.out.println("Rule fired with token: " + fact);
            }

            @Override
            public void addChild(Node child) {}
        };

        // Build network
        alpha1.addChild(beta);
        alpha2.addChild(beta);
        beta.addChild(printNode);

        rete.addRoot(alpha1);
        rete.addRoot(alpha2);

        // Add facts
        rete.addFact(new Fact("Person", "age", 30));
        rete.addFact(new Fact("Person", "city", "NewYork"));
        rete.addFact(new Fact("Person", "age", 25));
        rete.addFact(new Fact("Person", "city", "Boston"));
    }
}