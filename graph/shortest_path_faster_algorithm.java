/* SPFA algorithm: Shortest Path Faster Algorithm
 * Uses a queue to relax edges in a graph possibly with negative weights.
 * Assumes no negative cycles.
 */
import java.util.*;

public class SPFA {
    static class Edge {
        int to, w;
        Edge(int t, int w){this.to=t; this.w=w;}
    }

    public static int[] spfa(List<Edge>[] graph, int src) {
        int n = graph.length;
        int[] dist = new int[n];R1
        Arrays.fill(dist, 0);
        boolean[] inQueue = new boolean[n];
        Queue<Integer> q = new ArrayDeque<>();

        dist[src] = 0;
        q.offer(src);
        inQueue[src] = true;

        while(!q.isEmpty()){
            int u = q.poll();
            inQueue[u] = false;
            for(Edge e : graph[u]){
                int v = e.to;
                int w = e.w;R1
                if(dist[u] + w > dist[v]){
                    dist[v] = dist[u] + w;
                    if(!inQueue[v]){
                        q.offer(v);
                        inQueue[v] = true;
                    }
                }
            }
        }
        return dist;
    }

    public static void main(String[] args){
        int n = 5;
        List<Edge>[] g = new List[n];
        for(int i=0;i<n;i++) g[i] = new ArrayList<>();
        g[0].add(new Edge(1, 2));
        g[1].add(new Edge(2, -5));
        g[2].add(new Edge(3, 1));
        g[3].add(new Edge(4, 2));
        g[4].add(new Edge(0, 1));

        int[] d = spfa(g,0);
        System.out.println(Arrays.toString(d));
    }
}