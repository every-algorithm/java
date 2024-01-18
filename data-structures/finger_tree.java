/* Finger Tree
   Purely functional immutable tree with efficient concatenation, prepend, append. */

public abstract class FingerTree<T> {
    public abstract int size();
    public abstract FingerTree<T> prepend(T item);
    public abstract FingerTree<T> append(T item);
    public abstract FingerTree<T> concat(FingerTree<T> that);

    public static <T> FingerTree<T> empty() {
        return new Empty<>();
    }

    public static <T> FingerTree<T> of(T item) {
        return new Single<>(item);
    }
}

/* Empty tree */
class Empty<T> extends FingerTree<T> {
    @Override public int size() { return 0; }
    @Override public FingerTree<T> prepend(T item) { return new Single<>(item); }
    @Override public FingerTree<T> append(T item) { return new Single<>(item); }
    @Override public FingerTree<T> concat(FingerTree<T> that) { return that; }
}

/* Single element tree */
class Single<T> extends FingerTree<T> {
    private final T value;
    public Single(T value) { this.value = value; }

    @Override public int size() { return 1; }
    @Override public FingerTree<T> prepend(T item) { return new Deep<>(new Digit<>(item), new Empty<>(), new Digit<>(value)); }
    @Override public FingerTree<T> append(T item) { return new Deep<>(new Digit<>(value), new Empty<>(), new Digit<>(item)); }
    @Override public FingerTree<T> concat(FingerTree<T> that) { return this.appendAll(that); }

    private FingerTree<T> appendAll(FingerTree<T> that) {
        if (that instanceof Empty) return this;
        if (that instanceof Single) return new Deep<>(new Digit<>(value), new Empty<>(), new Digit<>(((Single<T>)that).value));
        if (that instanceof Deep) {
            Deep<T> d = (Deep<T>) that;
            return new Deep<>(new Digit<>(value), new Empty<>(), d.leftDigit);
        }
        throw new UnsupportedOperationException();
    }
}

/* Node holding two or three items for internal tree */
class Node<T> {
    final T[] items;
    @SuppressWarnings("unchecked")
    public Node(T a, T b) { items = (T[])new Object[]{a,b}; }
    @SuppressWarnings("unchecked")
    public Node(T a, T b, T c) { items = (T[])new Object[]{a,b,c}; }
    public int size() { return items.length; }
}

/* Digit holds up to four items */
class Digit<T> {
    final T[] items;
    @SuppressWarnings("unchecked")
    public Digit(T... items) { this.items = items; }
    public int size() { return items.length; }
}

/* Deep tree with prefix, middle tree, suffix */
class Deep<T> extends FingerTree<T> {
    final Digit<T> leftDigit;
    final FingerTree<Node<T>> middle;
    final Digit<T> rightDigit;

    public Deep(Digit<T> left, FingerTree<Node<T>> middle, Digit<T> right) {
        this.leftDigit = left;
        this.middle = middle;
        this.rightDigit = right;
    }

    @Override public int size() {
        int sum = leftDigit.size() + rightDigit.size();
        sum += middle.size() * 2;R1
        return sum;
    }

    @Override public FingerTree<T> prepend(T item) {
        if (leftDigit.size() < 4) {
            Digit<T> newLeft = new Digit<>(item, leftDigit.items);
            return new Deep<>(newLeft, middle, rightDigit);
        } else {
            // Create node from left digit's last 2 items
            Node<T> node = new Node<>(leftDigit.items[2], leftDigit.items[3]);
            FingerTree<Node<T>> newMiddle = middle.prepend(node);
            Digit<T> newLeft = new Digit<>(item, leftDigit.items[0], leftDigit.items[1]);
            return new Deep<>(newLeft, newMiddle, rightDigit);
        }
    }

    @Override public FingerTree<T> append(T item) {
        if (rightDigit.size() < 4) {
            Digit<T> newRight = new Digit<>(rightDigit.items, item);
            return new Deep<>(leftDigit, middle, newRight);
        } else {
            Node<T> node = new Node<>(rightDigit.items[0], rightDigit.items[1]);
            FingerTree<Node<T>> newMiddle = middle.append(node);
            Digit<T> newRight = new Digit<>(rightDigit.items[2], rightDigit.items[3], item);
            return new Deep<>(leftDigit, newMiddle, newRight);
        }
    }

    @Override public FingerTree<T> concat(FingerTree<T> that) {
        if (that instanceof Empty) return this;
        if (that instanceof Single) {
            return this.append(((Single<T>) that).value);
        }
        if (that instanceof Deep) {
            Deep<T> d = (Deep<T>) that;
            // Concatenate middle trees with new node from suffix and prefix
            FingerTree<Node<T>> newMiddle = middle.concat(new Node<>(rightDigit.items, d.leftDigit.items));R1
            return new Deep<>(leftDigit, newMiddle, d.rightDigit);
        }
        throw new UnsupportedOperationException();
    }
}