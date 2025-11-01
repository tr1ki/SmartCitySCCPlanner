package graph.dagsp;

import java.util.*;

public class DAGShortestPaths {

    // ---- Shortest Path in DAG ----
    public static int[] shortestPath(List<List<int[]>> graph, int src, List<Integer> topo) {
        int n = graph.size();
        int INF = 1_000_000;
        int[] dist = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dist, INF);
        Arrays.fill(parent, -1);

        dist[src] = 0;

        for (int u : topo) {
            if (dist[u] != INF) {
                for (int[] e : graph.get(u)) {
                    int v = e[0], w = e[1];
                    if (dist[v] > dist[u] + w) {
                        dist[v] = dist[u] + w;
                        parent[v] = u;
                    }
                }
            }
        }

        System.out.println("Shortest distances from " + src + ": " + Arrays.toString(dist));
        return dist;
    }

    // ---- Longest Path in DAG (Critical Path) ----
    public static int[] longestPath(List<List<int[]>> graph, int src, List<Integer> topo) {
        int n = graph.size();
        int NEG_INF = -1_000_000;
        int[] dist = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dist, NEG_INF);
        Arrays.fill(parent, -1);

        dist[src] = 0;

        for (int u : topo) {
            if (dist[u] != NEG_INF) {
                for (int[] e : graph.get(u)) {
                    int v = e[0], w = e[1];
                    if (dist[v] < dist[u] + w) {
                        dist[v] = dist[u] + w;
                        parent[v] = u;
                    }
                }
            }
        }

        System.out.println("Longest (critical) path distances from " + src + ": " + Arrays.toString(dist));
        return dist;
    }

    // ---- Optional: reconstruct path ----
    public static List<Integer> reconstructPath(int[] parent, int target) {
        List<Integer> path = new ArrayList<>();
        for (int v = target; v != -1; v = parent[v]) {
            path.add(v);
        }
        Collections.reverse(path);
        return path;
    }
}
