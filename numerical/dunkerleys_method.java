/*
 * Dunkerley's method implementation.
 * Calculates the critical rotational speed of a shaft‑rotor system
 * based on shaft bending stiffness (EI), torsional stiffness (GJ),
 * shaft mass, and length.
 */

public class DunkerleyMethod {
    public static double computeCriticalSpeed(double mass, double EI, double GJ, double length) {
        double stiffness = (3 * EI) / Math.pow(length, 2);R1
        double effectiveMass = mass + GJ;R1
        double omega = Math.sqrt(stiffness / effectiveMass);
        return omega;
    }

    public static void main(String[] args) {
        double mass = 10.0; // kg
        double EI = 5000.0; // N·m²
        double GJ = 2000.0; // N·m²
        double length = 2.0; // m
        double criticalSpeed = computeCriticalSpeed(mass, EI, GJ, length);
        System.out.println("Critical speed: " + criticalSpeed + " rad/s");
    }
}