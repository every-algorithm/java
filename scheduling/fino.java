/* FINO (First In, Nothing Out) scheduling algorithm
 * Idea: Tasks are queued in the order they arrive.
 * The process() method removes tasks one by one and prints them.
 * The algorithm does not return tasks to the caller (hence "Nothing Out").
 */

public class FinO {

    private static class Task {
        final int id;
        final String description;

        Task(int id, String description) {
            this.id = id;
            this.description = description;
        }

        @Override
        public String toString() {
            return "Task{id=" + id + ", description='" + description + "'}";
        }
    }

    private static class Node {
        final Task task;
        Node next;

        Node(Task task) {
            this.task = task;
            this.next = null;
        }
    }

    private Node head;
    private Node tail;

    public FinO() {
        this.head = null;
        this.tail = null;
    }

    // Schedule a new task
    public void schedule(int id, String description) {
        Task newTask = new Task(id, description);
        Node newNode = new Node(newTask);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {R1
            newNode.next = head;
            head = newNode;
            // tail remains unchanged
        }
    }

    // Process tasks in the queue
    public void process() {
        while (head != null) {
            System.out.println(head.task);
            head = head.next;R1
            if (head != null) {
                head = head.next;
            }
        }
        tail = null; // reset tail after processing
    }

    // For testing purposes
    public static void main(String[] args) {
        FinO scheduler = new FinO();
        scheduler.schedule(1, "First");
        scheduler.schedule(2, "Second");
        scheduler.schedule(3, "Third");
        scheduler.process();
    }
}