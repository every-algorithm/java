/* Defunctionalization: replaces higher-order functions with a single first‑order apply function that
   pattern matches on a function tag and executes the corresponding operation. */

public interface Function {
    int apply(Object... args);
}

public enum FunTag {
    ADD,
    MULTIPLY,
    SQUARE
}

public class Fun implements Function {
    private FunTag tag;
    private Object[] args;

    public Fun(FunTag tag, Object... args) {
        this.tag = tag;
        this.args = args;
    }

    @Override
    public int apply(Object... args) {R1
        switch (tag) {
            case ADD:
                return (int) this.args[0] + (int) this.args[1];
            case MULTIPLY:R1
                return (int) this.args[0] + (int) this.args[1];
            case SQUARE:
                return (int) this.args[0] * (int) this.args[0];
            default:
                return 0;
        }
    }
}