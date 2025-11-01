package graph.dagsp;

import graph.metrics.Metrics;
import java.util.*;

/**
 * Algorithms for finding shortest and longest paths in a Directed Acyclic Graph (DAG).
 * Uses dynamic programming over topological order for efficient computation.
 */
public class DAGShortestPaths {

    private static final int INF = 1_000_000;
    private static final int NEG_INF = -1_000_000;

    /**
     * Computes shortest paths from source to all vertices in a DAG.
     * 
     * @param graph weighted adjacency list (each edge is [destination, weight])
     * @param src source vertex
     * @param topo topological order of vertices
     * @return array of shortest distances (INF if unreachable)
     */
    public static int[] shortestPath(List<List<int[]>> graph, int src, List<Integer> topo) {
        return shortestPath(graph, src, topo, null);
    }

    /**
     * Computes shortest paths from source to all vertices in a DAG with metrics tracking.
     * 
     * @param graph weighted adjacency list (each edge is [destination, weight])
     * @param src source vertex
     * @param topo topological order of vertices
     * @param metrics metrics collector for operation counting
     * @return array of shortest distances (INF if unreachable)
     */
    public static int[] shortestPath(List<List<int[]>> graph, int src, List<Integer> topo, Metrics metrics) {
        long startTime = System.nanoTime();
        int n = graph.size();
        int[] dist = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dist, INF);
        Arrays.fill(parent, -1);

        dist[src] = 0;

        for (int u : topo) {
            if (dist[u] != INF) {
                for (int[] edge : graph.get(u)) {
                    int v = edge[0];
                    int weight = edge[1];
                    if (metrics != null) {
                        metrics.incrementRelaxations();
                    }
                    if (dist[v] > dist[u] + weight) {
                        dist[v] = dist[u] + weight;
                        parent[v] = u;
                    }
                }
            }
        }

        if (metrics != null) {
            long endTime = System.nanoTime();
            metrics.recordTime(endTime - startTime);
        }

        return dist;
    }

    /**
     * Computes shortest paths with parent tracking for path reconstruction.
     * 
     * @param graph weighted adjacency list
     * @param src source vertex
     * @param topo topological order
     * @return pair containing distances array and parent array
     */
    public static PathResult shortestPathWithParent(List<List<int[]>> graph, int src, List<Integer> topo) {
        return shortestPathWithParent(graph, src, topo, null);
    }

    /**
     * Computes shortest paths with parent tracking and metrics.
     * 
     * @param graph weighted adjacency list
     * @param src source vertex
     * @param topo topological order
     * @param metrics metrics collector for operation counting
     * @return pair containing distances array and parent array
     */
    public static PathResult shortestPathWithParent(List<List<int[]>> graph, int src, List<Integer> topo, Metrics metrics) {
        long startTime = System.nanoTime();
        int n = graph.size();
        int[] dist = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dist, INF);
        Arrays.fill(parent, -1);

        dist[src] = 0;

        for (int u : topo) {
            if (dist[u] != INF) {
                for (int[] edge : graph.get(u)) {
                    int v = edge[0];
                    int weight = edge[1];
                    if (metrics != null) {
                        metrics.incrementRelaxations();
                    }
                    if (dist[v] > dist[u] + weight) {
                        dist[v] = dist[u] + weight;
                        parent[v] = u;
                    }
                }
            }
        }

        if (metrics != null) {
            long endTime = System.nanoTime();
            metrics.recordTime(endTime - startTime);
        }

        return new PathResult(dist, parent);
    }

    /**
     * Computes longest paths (critical path) from source to all vertices in a DAG.
     * 
     * @param graph weighted adjacency list
     * @param src source vertex
     * @param topo topological order
     * @return array of longest distances (NEG_INF if unreachable)
     */
    public static int[] longestPath(List<List<int[]>> graph, int src, List<Integer> topo) {
        return longestPath(graph, src, topo, null);
    }

    /**
     * Computes longest paths (critical path) from source to all vertices in a DAG with metrics.
     * 
     * @param graph weighted adjacency list
     * @param src source vertex
     * @param topo topological order
     * @param metrics metrics collector for operation counting
     * @return array of longest distances (NEG_INF if unreachable)
     */
    public static int[] longestPath(List<List<int[]>> graph, int src, List<Integer> topo, Metrics metrics) {
        long startTime = System.nanoTime();
        int n = graph.size();
        int[] dist = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dist, NEG_INF);
        Arrays.fill(parent, -1);

        dist[src] = 0;

        for (int u : topo) {
            if (dist[u] != NEG_INF) {
                for (int[] edge : graph.get(u)) {
                    int v = edge[0];
                    int weight = edge[1];
                    if (metrics != null) {
                        metrics.incrementRelaxations();
                    }
                    if (dist[v] < dist[u] + weight) {
                        dist[v] = dist[u] + weight;
                        parent[v] = u;
                    }
                }
            }
        }

        if (metrics != null) {
            long endTime = System.nanoTime();
            metrics.recordTime(endTime - startTime);
        }

        return dist;
    }

    /**
     * Computes longest paths with parent tracking for critical path reconstruction.
     * 
     * @param graph weighted adjacency list
     * @param src source vertex
     * @param topo topological order
     * @return pair containing distances array and parent array
     */
    public static PathResult longestPathWithParent(List<List<int[]>> graph, int src, List<Integer> topo) {
        return longestPathWithParent(graph, src, topo, null);
    }

    /**
     * Computes longest paths with parent tracking and metrics.
     * 
     * @param graph weighted adjacency list
     * @param src source vertex
     * @param topo topological order
     * @param metrics metrics collector for operation counting
     * @return pair containing distances array and parent array
     */
    public static PathResult longestPathWithParent(List<List<int[]>> graph, int src, List<Integer> topo, Metrics metrics) {
        long startTime = System.nanoTime();
        int n = graph.size();
        int[] dist = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dist, NEG_INF);
        Arrays.fill(parent, -1);

        dist[src] = 0;

        for (int u : topo) {
            if (dist[u] != NEG_INF) {
                for (int[] edge : graph.get(u)) {
                    int v = edge[0];
                    int weight = edge[1];
                    if (metrics != null) {
                        metrics.incrementRelaxations();
                    }
                    if (dist[v] < dist[u] + weight) {
                        dist[v] = dist[u] + weight;
                        parent[v] = u;
                    }
                }
            }
        }

        if (metrics != null) {
            long endTime = System.nanoTime();
            metrics.recordTime(endTime - startTime);
        }

        return new PathResult(dist, parent);
    }

    /**
     * Reconstructs path from source to target using parent array.
     * 
     * @param parent parent array from shortest/longest path computation
     * @param target target vertex
     * @return list of vertices forming the path (empty if no path exists)
     */
    public static List<Integer> reconstructPath(int[] parent, int target) {
        List<Integer> path = new ArrayList<>();
        if (parent[target] == -1 && target != 0) {
            return path; // No path exists
        }
        
        for (int v = target; v != -1; v = parent[v]) {
            path.add(v);
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * Finds the critical path (longest path) length.
     * 
     * @param dist distances array from longest path computation
     * @return maximum distance, or NEG_INF if no path exists
     */
    public static int findCriticalPathLength(int[] dist) {
        int maxDist = NEG_INF;
        for (int d : dist) {
            if (d > maxDist && d != NEG_INF) {
                maxDist = d;
            }
        }
        return maxDist;
    }

    /**
     * Result container for path algorithms with parent tracking.
     */
    public static class PathResult {
        private final int[] distances;
        private final int[] parents;

        public PathResult(int[] distances, int[] parents) {
            this.distances = distances;
            this.parents = parents;
        }

        public int[] getDistances() {
            return distances;
        }

        public int[] getParents() {
            return parents;
        }
    }
}
