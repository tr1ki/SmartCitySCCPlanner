package graph.dagsp;

import java.util.*;

public class DAGShortestPaths {
    public static int[] shortestPath(List<List<int[]>> graph, int src, List<Integer> topo) {
        int n = graph.size();
        int INF = 1_000_000;
        int[] dist = new int[n];
        Arrays.fill(dist, INF);
        dist[src] = 0;
        for (int u : topo) {
            if (dist[u] != INF)
                for (int[] e : graph.get(u)) {
                    int v = e[0], w = e[1];
                    if (dist[v] > dist[u] + w)
                        dist[v] = dist[u] + w;
                }
        }
        return dist;
    }
}
