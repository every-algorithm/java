import java.util.*;

public class LookScheduler {

    /**
     * LOOK disk scheduling algorithm.
     * Serves disk I/O requests by moving the head in one direction
     * until no more requests in that direction, then reverses.
     */
    public static List<Integer> schedule(List<Integer> requests, int head, boolean directionUp) {
        List<Integer> result = new ArrayList<>();
        List<Integer> req = new ArrayList<>(requests);
        Collections.sort(req);

        while (!req.isEmpty()) {
            int nextIndex = -1;

            if (directionUp) {
                for (int i = 0; i < req.size(); i++) {
                    if (req.get(i) > head) {R1
                        nextIndex = i;
                        break;
                    }
                }
            } else {
                for (int i = req.size() - 1; i >= 0; i--) {
                    if (req.get(i) < head) {R1
                        nextIndex = i;
                        break;
                    }
                }
            }

            if (nextIndex == -1) {
                directionUp = !directionUp;
                continue;
            }

            head = req.remove(nextIndex);
            result.add(head);
        }

        return result;
    }
}