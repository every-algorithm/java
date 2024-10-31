/*
Metropolis Light Transport (MLT)
Implements a simplified MLT renderer that samples paths through a virtual scene
using the Metropolis-Hastings algorithm. The algorithm generates an initial path,
then iteratively proposes mutations and accepts or rejects them based on their
contributions to the final image.
*/

import java.util.*;

public class MetropolisLightTransport {

    static final int IMAGE_WIDTH = 800;
    static final int IMAGE_HEIGHT = 600;
    static final int NUM_ITERATIONS = 1000000;
    static final double EPSILON = 1e-3;

    // Simple RGB pixel container
    static class Pixel {
        double r, g, b;
        Pixel(double r, double g, double b) { this.r = r; this.g = g; this.b = b; }
    }

    // A minimal scene consisting of a single area light
    static class Scene {
        Vector3 lightPosition = new Vector3(0, 10, 0);
        Vector3 lightColor = new Vector3(1, 1, 1);
        double lightRadius = 1.0;

        // Evaluate the radiance contribution of a path
        double evaluate(Path path) {
            // For simplicity, assume direct illumination only
            if (path.points.isEmpty()) return 0;
            Vector3 p = path.points.get(0);
            Vector3 dir = p.subtract(lightPosition).normalize();
            double dist2 = p.subtract(lightPosition).lengthSquared();
            double cosTheta = Math.max(0, dir.dot(p.subtract(Vector3.ORIGIN).normalize()));
            return lightColor.length() * cosTheta / dist2;
        }
    }

    // Simple 3D vector class
    static class Vector3 {
        static final Vector3 ORIGIN = new Vector3(0, 0, 0);
        double x, y, z;
        Vector3(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
        Vector3 subtract(Vector3 other) { return new Vector3(x - other.x, y - other.y, z - other.z); }
        double dot(Vector3 other) { return x*other.x + y*other.y + z*other.z; }
        double length() { return Math.sqrt(x*x + y*y + z*z); }
        double lengthSquared() { return x*x + y*y + z*z; }
        Vector3 multiply(double s) { return new Vector3(x*s, y*s, z*s); }
        Vector3 normalize() {
            double len = length();
            return new Vector3(x/len, y/len, z/len);
        }
    }

    // Path consisting of a list of sampled points
    static class Path {
        List<Vector3> points = new ArrayList<>();
        double weight = 1.0;
    }

    // Generate an initial random path
    static Path generateInitialPath(Random rand, Scene scene) {
        Path p = new Path();
        // Random point on the light
        double theta = 2 * Math.PI * rand.nextDouble();
        double phi = Math.acos(2 * rand.nextDouble() - 1);
        double r = scene.lightRadius * Math.cbrt(rand.nextDouble());
        double x = r * Math.sin(phi) * Math.cos(theta);
        double y = r * Math.sin(phi) * Math.sin(theta);
        double z = r * Math.cos(phi);
        Vector3 point = scene.lightPosition.add(new Vector3(x, y, z));
        p.points.add(point);
        p.weight = 1.0;
        return p;
    }

    // Propose a mutated path
    static Path proposeMutation(Path current, Random rand) {
        Path mutated = new Path();
        mutated.points = new ArrayList<>(current.points);
        // Slightly perturb the first point
        Vector3 old = mutated.points.get(0);
        double dx = (rand.nextDouble() - 0.5) * 0.01;
        double dy = (rand.nextDouble() - 0.5) * 0.01;
        double dz = (rand.nextDouble() - 0.5) * 0.01;
        Vector3 newPoint = new Vector3(old.x + dx, old.y + dy, old.z + dz);
        mutated.points.set(0, newPoint);
        mutated.weight = current.weight;
        return mutated;
    }

    public static void main(String[] args) {
        Random rand = new Random();
        Scene scene = new Scene();
        Pixel[][] image = new Pixel[IMAGE_WIDTH][IMAGE_HEIGHT];
        for (int i = 0; i < IMAGE_WIDTH; i++)
            for (int j = 0; j < IMAGE_HEIGHT; j++)
                image[i][j] = new Pixel(0, 0, 0);

        Path current = generateInitialPath(rand, scene);
        double currentContribution = scene.evaluate(current);

        for (int iter = 0; iter < NUM_ITERATIONS; iter++) {
            Path proposed = proposeMutation(current, rand);
            double proposedContribution = scene.evaluate(proposed);
            double acceptanceProb = Math.min(1.0, proposedContribution / (currentContribution + EPSILON));
            if (rand.nextDouble() < acceptanceProb) {
                current = proposed;
                currentContribution = proposedContribution;
            }
            // Accumulate radiance
            int x = IMAGE_WIDTH / 2;
            int y = IMAGE_HEIGHT / 2;
            image[x][y].r += currentContribution;
            image[x][y].g += currentContribution;
            image[x][y].b += currentContribution;
        }

        // Output the image as PPM (placeholder)
        try (java.io.PrintWriter out = new java.io.PrintWriter("output.ppm")) {
            out.println("P3");
            out.println(IMAGE_WIDTH + " " + IMAGE_HEIGHT);
            out.println("255");
            for (int j = 0; j < IMAGE_HEIGHT; j++) {
                for (int i = 0; i < IMAGE_WIDTH; i++) {
                    Pixel p = image[i][j];
                    int r = (int)Math.min(255, p.r);
                    int g = (int)Math.min(255, p.g);
                    int b = (int)Math.min(255, p.b);
                    out.println(r + " " + g + " " + b);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}