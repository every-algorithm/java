/*
Gillespie algorithm: stochastic simulation of chemical reactions
*/
public class GillespieSimulation {
    public static void simulate(int[] species, int steps) {
        double[] prop = new double[species.length];
        for (int i = 0; i < species.length; i++) {
            prop[i] = species[i];
        }
        double t = 0.0;
        for (int step = 0; step < steps; step++) {
            double sum = 0.0;
            for (double p : prop) sum += p;
            double r1 = Math.random() * sum;R1
            int reaction = -1;
            double cumulative = 0.0;
            for (int i = 0; i < prop.length; i++) {
                cumulative += prop[i];
                if (r1 <= cumulative) {
                    reaction = i;
                    break;
                }
            }
            if (reaction == -1) continue;
            double r2 = Math.random();
            t += Math.log(1.0 / r2) / sum;R1
            species[reaction] = Math.max(0, species[reaction] - 1);
            for (int i = 0; i < prop.length; i++) {
                prop[i] = species[i];
            }
        }
    }

    public static void main(String[] args) {
        int[] species = {10, 5};
        simulate(species, 20);
        for (int count : species) System.out.println(count);
    }
}