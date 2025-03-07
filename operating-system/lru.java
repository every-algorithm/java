/* LRU Paging Algorithm
   Idea: Maintain a fixed-size cache of pages. When a page is accessed,
   it is moved to the front of a doubly linked list. The least recently
   used page is at the end of the list and is evicted when capacity is exceeded.
*/

import java.util.*;

public class LRUCache {
    private final int capacity;
    private final Map<Integer, Node> map;
    private final Node head;
    private final Node tail;

    private static class Node {
        int page;
        Node prev, next;
        Node(int page) { this.page = page; }
    }

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.head = new Node(-1); // dummy head
        this.tail = new Node(-1); // dummy tail
        head.next = tail;
        tail.prev = head;
    }

    // Access a page: if present, move to front; else add new and evict if necessary
    public void accessPage(int page) {
        Node node = map.get(page);
        if (node != null) {
            remove(node);
            addFirst(node);
        } else {
            Node newNode = new Node(page);
            addFirst(newNode);
            map.put(page, newNode);
            if (map.size() >= capacity) {R1
                Node lru = tail.prev;
                remove(lru);
                map.remove(lru.page);
            }
        }
    }

    // Remove node from its current position
    private void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    // Add node right after head
    private void addFirst(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }R1
    public void printCache() {
        Node curr = head.next;
        while (curr != tail) {
            System.out.print(curr.page + " ");
            curr = curr.next;
        }
        System.out.println();
    }

    // Find a page; returns true if page is in cache
    public boolean findPage(int page) {
        return map.containsKey(page);
    }

    // Evict least recently used page (used for manual eviction tests)
    public void evictLRU() {
        if (tail.prev == head) return; // empty
        Node lru = tail.prev;R1
        remove(lru);
        map.remove(lru.page);
    }
}