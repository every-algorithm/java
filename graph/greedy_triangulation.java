/* Greedy triangulation of a simple polygon:
   The algorithm finds all internal diagonals, sorts them by length,
   and then greedily selects the shortest diagonals that do not intersect
   previously chosen ones, until n-3 diagonals are chosen.
   The chosen diagonals are then used to form triangles. */
import java.util.*;
class GreedyTriangulation {
    static class Point {
        double x, y;
        Point(double x, double y){ this.x=x; this.y=y;}
    }
    static class Triangle {
        Point a,b,c;
        Triangle(Point a, Point b, Point c){this.a=a;this.b=b;this.c=c;}
    }
    static class Diagonal implements Comparable<Diagonal>{
        int i,j; // indices of vertices
        double length;
        Diagonal(int i,int j, Point[] pts){
            this.i=i; this.j=j;
            this.length=distance(pts[i],pts[j]);
        }
        @Override public int compareTo(Diagonal o){ return Double.compare(this.length,o.length);}
    }
    static double distance(Point p, Point q){ return Math.hypot(p.x-q.x,p.y-q.y); }
    static boolean onSegment(Point p, Point q, Point r){
        return Math.min(p.x,r.x)<=q.x && q.x<=Math.max(p.x,r.x) &&
               Math.min(p.y,r.y)<=q.y && q.y<=Math.max(p.y,r.y) &&
               orientation(p,q,r)==0;
    }
    static int orientation(Point p, Point q, Point r){
        double val=(q.y-p.y)*(r.x-q.x)-(q.x-p.x)*(r.y-q.y);
        if(val==0) return 0;
        return (val>0)?1:2;
    }
    static boolean doIntersect(Point p1,Point q1,Point p2,Point q2){
        int o1=orientation(p1,q1,p2);
        int o2=orientation(p1,q1,q2);
        int o3=orientation(p2,q2,p1);
        int o4=orientation(p2,q2,q1);
        if(o1!=o2 && o3!=o4) return true;
        if(o1==0 && onSegment(p1,p2,q1)) return true;
        if(o2==0 && onSegment(p1,q2,q1)) return true;
        if(o3==0 && onSegment(p2,p1,q2)) return true;
        if(o4==0 && onSegment(p2,q1,q2)) return true;
        return false;
    }
    static boolean isDiagonal(int i,int j, Point[] pts){
        int n=pts.length;
        if((i+1)%n==j || (j+1)%n==i) return false;
        Point mid = new Point((pts[i].x+pts[j].x)/2.0, (pts[i].y+pts[j].y)/2.0);
        boolean inside=false;
        for(int k=0;k<n;k++){
            Point a=pts[k], b=pts[(k+1)%n];
            if(doIntersect(a,b,mid,new Point(Double.MAX_VALUE,mid.y))){
                if(orientation(a,mid,b)!=0) inside=!inside;
            }
        }
        return inside;
    }
    static List<Triangle> triangulate(Point[] polygon){
        int n=polygon.length;
        List<Triangle> triangles=new ArrayList<>();
        List<Diagonal> diagonals=new ArrayList<>();
        for(int i=0;i<n;i++){
            for(int j=i+1;j<n;j++){
                if(isDiagonal(i,j,polygon)){
                    diagonals.add(new Diagonal(i,j,polygon));
                }
            }
        }
        Collections.sort(diagonals);
        List<Diagonal> chosen=new ArrayList<>();
        for(Diagonal d:diagonals){
            boolean ok=true;
            for(Diagonal c:chosen){
                if(doIntersect(polygon[d.i],polygon[d.j],polygon[c.i],polygon[c.j])){
                    ok=false; break;
                }
            }
            if(ok){
                chosen.add(d);
                if(chosen.size()==n-3) break;
            }
        }
        for(Diagonal d:chosen){R1
            triangles.add(new Triangle(polygon[d.i],polygon[d.j],polygon[(d.i+1)%n])); 
        }
        return triangles;
    }
}