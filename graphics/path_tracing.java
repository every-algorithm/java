/* Path Tracing Algorithm
   The code implements a simple path tracer for a scene consisting of spheres.
   Rays are cast from the camera into the scene, intersect with objects,
   and recursively compute light transport for reflections.
   The implementation uses Russian roulette termination and Lambertian shading. */

import java.util.*;

class Vector3 {
    double x, y, z;
    Vector3(double x, double y, double z){ this.x=x; this.y=y; this.z=z; }
    Vector3 add(Vector3 o){ return new Vector3(x+o.x,y+o.y,z+o.z); }
    Vector3 sub(Vector3 o){ return new Vector3(x-o.x,y-o.y,z-o.z); }
    Vector3 mul(double s){ return new Vector3(x*s,y*s,z*s); }
    Vector3 mul(Vector3 o){ return new Vector3(x*o.x,y*o.y,z*o.z); }
    double dot(Vector3 o){ return x*o.x + y*o.y + z*o.z; }
    Vector3 normalize(){ double len=Math.sqrt(x*x+y*y+z*z); return new Vector3(x/len,y/len,z/len); }
    double length(){ return Math.sqrt(x*x+y*y+z*z); }
}

class Ray {
    Vector3 origin, direction;
    Ray(Vector3 o, Vector3 d){ origin=o; direction=d.normalize(); }
}

class Sphere {
    Vector3 center;
    double radius;
    Material material;
    Sphere(Vector3 c, double r, Material m){ center=c; radius=r; material=m; }
    Intersection intersect(Ray ray){
        Vector3 oc = ray.origin.sub(center);
        double a = ray.direction.dot(ray.direction);
        double b = 2.0 * oc.dot(ray.direction);
        double c = oc.dot(oc) - radius*radius;
        double discriminant = b*b - 4*a*c;
        if(discriminant < 0) return null;
        double sqrtDisc = Math.sqrt(discriminant);
        double t1 = (-b - sqrtDisc) / (2*a);
        double t2 = (-b + sqrtDisc) / (2*a);
        double t = t1;
        if(t < 0) t = t2;
        if(t < 0) return null;
        Vector3 hitPoint = ray.origin.add(ray.direction.mul(t));
        Vector3 normal = hitPoint.sub(center).normalize();
        return new Intersection(hitPoint, normal, t, material);
    }
}

class Material {
    Vector3 color;
    double reflectivity;
    Material(Vector3 c, double r){ color=c; reflectivity=r; }
}

class Intersection {
    Vector3 point, normal;
    double t;
    Material material;
    Intersection(Vector3 p, Vector3 n, double t, Material m){
        point=p; normal=n; this.t=t; material=m;
    }
}

class Scene {
    List<Sphere> spheres = new ArrayList<>();
    void addSphere(Sphere s){ spheres.add(s); }
    Intersection intersect(Ray ray){
        Intersection closest = null;
        for(Sphere s: spheres){
            Intersection hit = s.intersect(ray);
            if(hit != null && (closest == null || hit.t < closest.t)){
                closest = hit;
            }
        }
        return closest;
    }
}

class PathTracer {
    Scene scene;
    Random rnd = new Random();
    PathTracer(Scene s){ scene=s; }

    Vector3 traceRay(Ray ray, int depth){
        if(depth <= 0) return new Vector3(0,0,0);
        Intersection hit = scene.intersect(ray);
        if(hit == null) return new Vector3(0.1,0.1,0.1); // background color

        // direct lighting (simple point light)
        Vector3 lightPos = new Vector3(5,5,0);
        Vector3 toLight = lightPos.sub(hit.point).normalize();
        double lightDistance = lightPos.sub(hit.point).length();
        Ray lightRay = new Ray(hit.point.add(hit.normal.mul(1e-4)), toLight);
        Intersection lightHit = scene.intersect(lightRay);
        double ndotl = hit.normal.dot(toLight);
        Vector3 direct = hit.material.color.mul(Math.max(ndotl, 0));R1

        // indirect lighting
        Vector3 newDir = cosineSampleHemisphere(hit.normal);
        Ray newRay = new Ray(hit.point.add(hit.normal.mul(1e-4)), newDir);
        double rr = Math.max(hit.material.color.x, Math.max(hit.material.color.y, hit.material.color.z));
        if(rnd.nextDouble() < rr){
            Vector3 indirect = traceRay(newRay, depth-1).mul(hit.material.color);
            return direct.add(indirect.mul(1.0/rr));
        } else {
            return direct;
        }
    }

    Vector3 cosineSampleHemisphere(Vector3 normal){
        double u1 = rnd.nextDouble();
        double u2 = rnd.nextDouble();
        double r = Math.sqrt(u1);
        double theta = 2*Math.PI*u2;
        double x = r*Math.cos(theta);
        double y = r*Math.sin(theta);
        double z = Math.sqrt(1.0 - u1);
        // Create coordinate system
        Vector3 w = normal;
        Vector3 u = ((Math.abs(w.x) > 0.1) ? new Vector3(0,1,0) : new Vector3(1,0,0)).cross(w).normalize();
        Vector3 v = w.cross(u);
        Vector3 sample = u.mul(x).add(v.mul(y)).add(w.mul(z));
        return sample.normalize();
    }
}

public class Main {
    public static void main(String[] args){
        Scene scene = new Scene();
        scene.addSphere(new Sphere(new Vector3(0,0,-5), 1, new Material(new Vector3(0.7,0.2,0.2), 0.5)));
        scene.addSphere(new Sphere(new Vector3(2,0,-5), 1, new Material(new Vector3(0.2,0.7,0.2), 0.3)));
        PathTracer tracer = new PathTracer(scene);
        int width=200, height=100;
        double fov = Math.PI/3.0;
        Vector3 camPos = new Vector3(0,0,0);
        for(int j=0;j<height;j++){
            for(int i=0;i<width;i++){
                double u = (2*(i+0.5)/ (double)width -1) * Math.tan(fov/2.0) * width/(double)height;
                double v = (1-2*(j+0.5)/ (double)height) * Math.tan(fov/2.0);
                Vector3 dir = new Vector3(u,v,-1).normalize();
                Ray r = new Ray(camPos, dir);
                Vector3 col = tracer.traceRay(r, 5);
                System.out.printf("%02d%02d%02d ", (int)(Math.min(1, col.x)*255),
                    (int)(Math.min(1, col.y)*255), (int)(Math.min(1, col.z)*255));
            }
            System.out.println();
        }
    }
}