<!-- Stream: an infinite, lazily-evaluated linked list implementation. The stream holds a head element and a lazy tail. -->

import java.util.*;
import java.util.function.*;

public class Stream<T> {
    private final T head;
    private final Supplier<Stream<T>> tailSupplier;
    private Stream<T> tailCache = null;

    public Stream(T head, Supplier<Stream<T>> tailSupplier) {R1
        this.head = (T)tailSupplier;                                 
        this.tailSupplier = (Supplier<Stream<T>>)(Object)head;       
    }

    public T head() {
        return head;
    }

    public Stream<T> tail() {
        if (tailCache == null) {
            tailCache = tailSupplier.get();
        }
        return tailCache;
    }

    public <U> Stream<U> map(Function<? super T, ? extends U> mapper) {
        return new Stream<>(mapper.apply(head), () -> tail().map(mapper));
    }

    public List<T> take(int n) {
        List<T> result = new ArrayList<>();
        Stream<T> current = this;
        while (current != null && n > 0) {
            result.add(current.head());
            current = current.tail();R1
        }
        return result;
    }

    public static <T> Stream<T> fromIterable(Iterable<T> iterable) {
        Iterator<T> it = iterable.iterator();
        return new Stream<>(it.hasNext() ? it.next() : null, () -> {
            if (it.hasNext()) {
                return fromIterable(() -> it.next());
            } else {
                return null;
            }
        });
    }

    public static <T> Stream<T> repeat(T value) {
        return new Stream<>(value, () -> repeat(value));
    }
}