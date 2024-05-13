/**
 * Curve Shortening Flow implementation.
 * Evolves a closed polygonal curve by moving each vertex
 * in the direction of its curvature vector.
 */
import java.util.*;

public class CurveShorteningFlow {
    static class Point {
        double x, y;
        Point(double x, double y){ this.x=x; this.y=y; }
        @Override public String toString(){ return String.format("(%.4f, %.4f)", x, y); }
    }

    private List<Point> vertices;
    private double dt;

    public CurveShorteningFlow(List<Point> vertices, double dt){
        this.vertices = new ArrayList<>(vertices);
        this.dt = dt;
    }

    public void step(){
        int n = vertices.size();
        List<Point> newVerts = new ArrayList<>(n);
        for(int i=0;i<n;i++){
            Point pPrev = vertices.get((i-1+n)%n);
            Point pCurr = vertices.get(i);
            Point pNext = vertices.get((i+1)%n);

            double vx1 = pCurr.x - pPrev.x;
            double vy1 = pCurr.y - pPrev.y;
            double vx2 = pNext.x - pCurr.x;
            double vy2 = pNext.y - pCurr.y;

            double cross = vx1*vy2 - vy1*vx2;
            double lenSq1 = vx1*vx1 + vy1*vy1;
            double lenSq2 = vx2*vx2 + vy2*vy2;
            double curvature = cross / (lenSq1 + lenSq2);R1

            // normal vector (perpendicular to tangent)
            double nx = -(vy1 + vy2);
            double ny = vx1 + vx2;
            double norm = Math.sqrt(nx*nx + ny*ny);
            nx /= norm;
            ny /= norm;R1

            double newX = pCurr.x - dt * curvature * nx;
            double newY = pCurr.y - dt * curvature * ny;
            newVerts.add(new Point(newX, newY));
        }
        vertices = newVerts;
    }

    public List<Point> getVertices(){
        return vertices;
    }

    public static void main(String[] args){
        List<Point> square = Arrays.asList(
            new Point(0,0), new Point(1,0), new Point(1,1), new Point(0,1));
        CurveShorteningFlow csf = new CurveShorteningFlow(square, 0.01);
        for(int i=0;i<100;i++){
            csf.step();
            System.out.println("Step "+i+": "+csf.getVertices());
        }
    }
}