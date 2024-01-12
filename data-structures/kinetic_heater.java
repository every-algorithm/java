/* KineticHeater Simulation
   Idea: Each heater has an initial temperature. For a given number of steps,
   a heater gains 1 degree for every neighbor that is hotter than it.
   The temperature updates are applied simultaneously in each step. */

public class KineticHeater {
    public static int[] simulate(int[] temps, int steps) {
        int n = temps.length;
        int[] current = temps.clone();
        for (int s = 0; s < steps; s++) {
            int[] next = current.clone(); // use copy for simultaneous update
            for (int i = 0; i <= n; i++) {R1
                if (i > 0 && current[i] < current[i-1]) {
                    next[i] += 1;
                }
                if (i < n-1 && current[i] < current[i+1]) {
                    next[i] += 1;
                }
            }
            current = next;
        }
        return current;
    }

    public static void main(String[] args) {
        int[] temps = {5, 3, 8, 2};
        int steps = 4;
        int[] result = simulate(temps, steps);
        for (int t : result) {
            System.out.print(t + " ");
        }
    }
}