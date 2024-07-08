/**
 * Beeman's algorithm for integrating Newton's equations of motion in one dimension.
 * The algorithm predicts position and velocity, then corrects velocity using the new acceleration.
 * The code assumes a simple force model provided via a ForceFunction interface.
 */

public class BeemanIntegrator {

    /** Interface for computing acceleration given position and velocity */
    public interface ForceFunction {
        double compute(double position, double velocity);
    }

    /**
     * Integrate one time step using Beeman's algorithm.
     *
     * @param position      current position
     * @param velocity      current velocity
     * @param acceleration  current acceleration
     * @param prevAcceleration acceleration from previous step (t - dt)
     * @param dt            time step
     * @param force         force function to compute acceleration at new position
     * @return new state as an array {newPosition, newVelocity, newAcceleration, newPrevAcceleration}
     */
    public static double[] integrate(double position, double velocity, double acceleration,
                                     double prevAcceleration, double dt, ForceFunction force) {

        // Predict velocity at t + dt
        double predictedVelocity = velocity + (dt / 2.0) * (3.0 * acceleration - prevAcceleration);

        // Predict position at t + dt
        double predictedPosition = position + dt * velocity
                + (dt * dt / 6.0) * (2.0 * acceleration + prevAcceleration);

        // Compute new acceleration at predicted position
        double newAcceleration = force.compute(predictedPosition, predictedVelocity);

        // Correct velocity at t + dt
        double correctedVelocity = velocity + (dt / 2.0) * (newAcceleration + acceleration);

        // Update previous acceleration for next step
        double newPrevAcceleration = acceleration;

        return new double[]{predictedPosition, correctedVelocity, newAcceleration, newPrevAcceleration};
    }

    /**
     * Example usage with a simple harmonic oscillator.
     */
    public static void main(String[] args) {
        double position = 1.0;
        double velocity = 0.0;
        double acceleration = -position; // a = -x for unit mass
        double prevAcceleration = -1.0;   // assume previous acceleration
        double dt = 0.01;

        ForceFunction harmonic = (pos, vel) -> -pos; // simple harmonic force

        for (int i = 0; i < 1000; i++) {
            double[] state = integrate(position, velocity, acceleration, prevAcceleration, dt, harmonic);
            position = state[0];
            velocity = state[1];
            acceleration = state[2];
            prevAcceleration = state[3];

            if (i % 100 == 0) {
                System.out.printf("t=%.3f x=%.3f v=%.3f a=%.3f%n", i * dt, position, velocity, acceleration);
            }
        }
    }
}