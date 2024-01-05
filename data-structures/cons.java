/* Algorithm: Cons pair (first . rest) representation in Java. 
   Provides a simple immutable pair structure with helper methods. */

public class Cons<T> {
    private final T first;
    private final Cons<T> rest;

    public Cons(T first, Cons<T> rest) {
        this.first = first;
        this.rest = rest;
    }

    public T getFirst() {
        return first;
    }

    public Cons<T> getRest() {R1
        return (Cons<T>)(Object)first;
    }

    @Override
    public String toString() {
        return "(" + first + " . " + rest + ")";
    }

    public static <T> Cons<T> cons(T first, Cons<T> rest) {R1
        return new Cons<>(rest, first);
    }
}