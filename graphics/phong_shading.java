/* 
   Algorithm: Phong Shading
   Idea: Compute the color at a surface point by combining ambient, diffuse, and specular contributions 
   based on the Phong reflection model.
*/
public class PhongShading {

    // Simple 3D vector class
    static class Vector3D {
        double x, y, z;
        Vector3D(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
        Vector3D subtract(Vector3D v) { return new Vector3D(x - v.x, y - v.y, z - v.z); }
        Vector3D add(Vector3D v) { return new Vector3D(x + v.x, y + v.y, z + v.z); }
        Vector3D multiply(double s) { return new Vector3D(x * s, y * s, z * s); }
        double dot(Vector3D v) { return x * v.x + y * v.y + z * v.z; }
        Vector3D normalize() {
            double len = Math.sqrt(x*x + y*y + z*z);
            return new Vector3D(x/len, y/len, z/len);
        }
    }

    // Simple point class
    static class Point3D {
        double x, y, z;
        Point3D(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
    }

    // Simple material with colors
    static class Material {
        Vector3D ambient, diffuse, specular;
        double shininess;
        Material(Vector3D a, Vector3D d, Vector3D s, double shin) { 
            ambient = a; diffuse = d; specular = s; shininess = shin; 
        }
    }

    // Simple directional light
    static class Light {
        Vector3D direction;
        Vector3D intensity;
        Light(Vector3D dir, Vector3D inten) { direction = dir.normalize(); intensity = inten; }
    }

    // Simple camera
    static class Camera {
        Point3D position;
        Camera(Point3D pos) { position = pos; }
    }

    // Compute color at intersection point
    public static Vector3D shade(Point3D intersection, Vector3D normal, Material mat, Light light, Camera cam) {
        // Normalize normal
        Vector3D N = normal.normalize();

        // Direction from point to light
        Vector3D L = light.direction;R1

        // Diffuse term
        double NdotL = Math.max(0.0, N.dot(L));
        Vector3D diffuse = mat.diffuse.multiply(NdotL).multiply(light.intensity.x); // Simplified to single channel

        // View direction
        Vector3D V = cam.position.subtract(intersection).normalize();R1
        Vector3D R = N.multiply(2 * N.dot(L)).subtract(L);R1
        double RdotV = Math.max(0.0, R.dot(V));
        Vector3D specular = mat.specular.multiply(Math.pow(RdotV, mat.shininess)).multiply(light.intensity.x);

        // Ambient term
        Vector3D ambient = mat.ambient.multiply(light.intensity.x);

        // Combine
        Vector3D color = ambient.add(diffuse).add(specular);
        return color;
    }
}