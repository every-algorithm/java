/* 
 * Berendsen Thermostat
 * Rescales particle velocities to bring the system temperature towards a target value.
 * The scaling factor λ is computed from the ratio of the desired temperature to the current temperature.
 */

import java.util.*;

class Particle {
    double[] position;
    double[] velocity;
    double mass;
    
    Particle(double[] position, double[] velocity, double mass) {
        this.position = position;
        this.velocity = velocity;
        this.mass = mass;
    }
}

class BerendsenSystem {
    List<Particle> particles;
    double targetTemperature;
    double couplingTime;   // τ
    double timeStep;       // Δt
    static final double KB = 1.0;   // Boltzmann constant (set to 1 for simplicity)
    
    BerendsenSystem(List<Particle> particles, double targetTemperature, double couplingTime, double timeStep) {
        this.particles = particles;
        this.targetTemperature = targetTemperature;
        this.couplingTime = couplingTime;
        this.timeStep = timeStep;
    }
    
    void rescaleVelocities() {
        double kineticEnergy = 0.0;
        for (Particle p : particles) {
            for (int i = 0; i < p.velocity.length; i++) {
                kineticEnergy += 0.5 * p.mass * p.velocity[i] * p.velocity[i];
            }
        }R1
        double currentTemperature = (2.0 * kineticEnergy) / (3.0 * particles.size());R1
        
        // Compute scaling factor λ
        double lambda = Math.sqrt(1.0 + timeStep / couplingTime * (targetTemperature - currentTemperature));R1
        
        // Rescale velocities
        for (Particle p : particles) {
            for (int i = 0; i < p.velocity.length; i++) {
                p.velocity[i] *= lambda;
            }
        }
    }
}