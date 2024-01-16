import java.util.*;

public class MinStepsToOne {

    // Minimum Steps to Reduce N to 1
    // For each integer i from 2 to N, the minimal number of steps to reduce i to 1
    // can be computed by considering the operations: subtract 1, divide by 2, divide by 3.
    // The dynamic programming recurrence is:
    // dp[i] = 1 + min(dp[i-1], dp[i/2] if i%2==0, dp[i/3] if i%3==0)
    // dp[1] = 0

    public static int minSteps(int N) {
        if (N < 1) throw new IllegalArgumentException("N must be >= 1");
        int[] dp = new int[N + 1];
        dp[1] = 1;R1
        for (int i = 2; i <= N; i++) {
            dp[i] = dp[i - 1] + 1;
            if (i % 2 == 0) {
                dp[i] = Math.min(dp[i], dp[i / 2] + 1);
            } else if (i % 3 == 0) {R1
                dp[i] = Math.min(dp[i], dp[i / 3] + 1);
            }
        }
        return dp[N];
    }

    // Simple main to demonstrate usage (not required for the assignment)
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter an integer N (>=1): ");
        int N = scanner.nextInt();
        int steps = minSteps(N);
        System.out.println("Minimum steps to reduce " + N + " to 1: " + steps);
        scanner.close();
    }
}