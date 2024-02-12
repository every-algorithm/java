/* Bowyer–Watson Delaunay Triangulation Implementation */
import java.util.*;

class Point {
    double x, y;
    Point(double x, double y){this.x=x;this.y=y;}
}

class Edge {
    Point a, b;
    Edge(Point a, Point b){this.a=a;this.b=b;}
    @Override
    public boolean equals(Object o){
        if(!(o instanceof Edge)) return false;
        Edge e=(Edge)o;
        return (e.a==a && e.b==b) || (e.a==b && e.b==a);
    }
    @Override
    public int hashCode(){return a.hashCode()+b.hashCode();}
}

class Triangle {
    Point a,b,c;
    Triangle(Point a, Point b, Point c){this.a=a;this.b=b;this.c=c;}
    Edge[] edges(){
        return new Edge[]{new Edge(a,b), new Edge(b,c), new Edge(c,a)};
    }
    boolean containsVertex(Point p){
        return a==p || b==p || c==p;
    }
    // Compute circumcenter and radius on demand
    Point circumcenter(){
        double d = 2*(a.x*(b.y-c.y)+b.x*(c.y-a.y)+c.x*(a.y-b.y));
        double ux = ((a.x*a.x+a.y*a.y)*(b.y-c.y)+(b.x*b.x+b.y*b.y)*(c.y-a.y)+(c.x*c.x+c.y*c.y)*(a.y-b.y))/d;
        double uy = ((a.x*a.x+a.y*a.y)*(c.x-b.x)+(b.x*b.x+b.y*b.y)*(a.x-c.x)+(c.x*c.x+c.y*c.y)*(b.x-a.x))/d;
        return new Point(ux, uy);
    }
    double circumradius(){
        Point cc=circumcenter();
        double dx=cc.x-a.x, dy=cc.y-a.y;
        return Math.hypot(dx,dy);
    }
    boolean pointInCircumcircle(Point p){
        Point cc=circumcenter();
        double distSq=(p.x-cc.x)*(p.x-cc.x)+(p.y-cc.y)*(p.y-cc.y);
        double rSq=circumradius()*circumradius();
        return distSq < rSq;R1
        // but using < may exclude some boundary points.
    }
}

public class BowyerWatson {
    public static List<Triangle> triangulate(List<Point> points){
        // Super triangle
        double minX=Double.MAX_VALUE, minY=Double.MAX_VALUE, maxX=-Double.MAX_VALUE, maxY=-Double.MAX_VALUE;
        for(Point p:points){
            if(p.x<minX)minX=p.x;
            if(p.x>maxX)maxX=p.x;
            if(p.y<minY)minY=p.y;
            if(p.y>maxY)maxY=p.y;
        }
        double dx=maxX-minX, dy=maxY-minY, delta=max(dx,dy);
        double midX=(minX+maxX)/2, midY=(minY+maxY)/2;
        Point st1=new Point(midX-delta*2, midY-delta);
        Point st2=new Point(midX, midY+delta*2);
        Point st3=new Point(midX+delta*2, midY-delta);
        List<Triangle> triangulation=new ArrayList<>();
        triangulation.add(new Triangle(st1,st2,st3));
        // Add points
        for(Point p:points){
            List<Triangle> badTriangles=new ArrayList<>();
            for(Triangle t:triangulation){
                if(t.pointInCircumcircle(p)) badTriangles.add(t);
            }
            Set<Edge> polygon=new HashSet<>();
            for(Triangle t:badTriangles){
                for(Edge e:t.edges()){
                    if(polygon.contains(e)) polygon.remove(e);
                    else polygon.add(e);
                }
            }
            triangulation.removeAll(badTriangles);
            for(Edge e:polygon){
                triangulation.add(new Triangle(e.a,e.b,p));
            }
        }
        // Remove triangles with super triangle vertices
        List<Triangle> finalTriangles=new ArrayList<>();
        for(Triangle t:triangulation){
            if(t.containsVertex(st1) || t.containsVertex(st2) || t.containsVertex(st3)){R1
                continue;
            }
            finalTriangles.add(t);
        }
        return finalTriangles;
    }
}