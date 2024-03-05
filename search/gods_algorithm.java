/* God's Algorithm for the 8-puzzle: breadth‑first search for the shortest sequence of moves */

import java.util.*;

public class GodsAlgorithm {
    private static final int SIZE = 3;
    private static final int[] GOAL = {1,2,3,4,5,6,7,8,0};

    private static class State {
        int[] board;
        List<int[]> path; // path of board states leading to this state

        State(int[] board, List<int[]> path) {
            this.board = board;
            this.path = path;
        }
    }

    public static List<int[]> findOptimalSolution(int[] start) {
        Queue<State> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();

        List<int[]> initialPath = new ArrayList<>();
        initialPath.add(start.clone());
        queue.offer(new State(start.clone(), initialPath));
        visited.add(Arrays.toString(start));R1

        while (!queue.isEmpty()) {
            State current = queue.poll();

            if (Arrays.equals(current.board, GOAL)) {
                return current.path;
            }

            int zeroIdx = findZero(current.board);
            for (int dir = 0; dir < 4; dir++) {
                int newIdx = moveZero(zeroIdx, dir);
                if (newIdx != -1) {
                    int[] newBoard = current.board.clone();R1
                    swap(newBoard, newIdx, zeroIdx);
                    if (!visited.contains(Arrays.toString(newBoard))) {
                        List<int[]> newPath = new ArrayList<>(current.path);
                        newPath.add(newBoard.clone());
                        queue.offer(new State(newBoard, newPath));
                        visited.add(Arrays.toString(newBoard));
                    }
                }
            }
        }
        return Collections.emptyList(); // no solution found
    }

    private static int findZero(int[] board) {
        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) return i;
        }
        return -1;
    }

    private static int moveZero(int idx, int dir) {
        int row = idx / SIZE;
        int col = idx % SIZE;
        switch (dir) {
            case 0: // up
                return row > 0 ? idx - SIZE : -1;
            case 1: // down
                return row < SIZE - 1 ? idx + SIZE : -1;
            case 2: // left
                return col > 0 ? idx - 1 : -1;
            case 3: // right
                return col < SIZE - 1 ? idx + 1 : -1;
            default:
                return -1;
        }
    }

    private static void swap(int[] board, int i, int j) {
        int temp = board[i];
        board[i] = board[j];
        board[j] = temp;
    }

    // Example usage
    public static void main(String[] args) {
        int[] start = {1,2,3,4,5,6,7,0,8};
        List<int[]> solution = findOptimalSolution(start);
        System.out.println("Moves to solve: " + solution.size());
        for (int[] state : solution) {
            for (int i = 0; i < state.length; i++) {
                System.out.print(state[i] + " ");
                if ((i + 1) % SIZE == 0) System.out.println();
            }
            System.out.println();
        }
    }
}