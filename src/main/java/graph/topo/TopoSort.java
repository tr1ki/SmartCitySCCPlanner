package graph.topo;

import graph.metrics.Metrics;
import java.util.*;

/**
 * Topological sort implementation using Kahn's algorithm.
 * Computes a valid topological ordering of vertices in a directed acyclic graph (DAG).
 */
public class TopoSort {

    /**
     * Computes topological order using Kahn's algorithm.
     * 
     * @param graph adjacency list representation of the DAG
     * @return list of vertices in topological order
     */
    public List<Integer> topoSort(List<List<Integer>> graph) {
        return topoSort(graph, null);
    }

    /**
     * Computes topological order using Kahn's algorithm with metrics tracking.
     * 
     * @param graph adjacency list representation of the DAG
     * @param metrics metrics collector for operation counting
     * @return list of vertices in topological order
     */
    public List<Integer> topoSort(List<List<Integer>> graph, Metrics metrics) {
        long startTime = System.nanoTime();
        int n = graph.size();
        int[] indeg = new int[n];
        
        // Calculate in-degrees
        for (List<Integer> list : graph) {
            for (int v : list) {
                indeg[v]++;
            }
        }
        
        // Initialize queue with vertices having zero in-degree
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (indeg[i] == 0) {
                queue.add(i);
                if (metrics != null) {
                    metrics.incrementQueuePushes();
                }
            }
        }

        List<Integer> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            int node = queue.poll();
            if (metrics != null) {
                metrics.incrementQueuePops();
            }
            order.add(node);
            
            // Remove edges and update in-degrees
            for (int v : graph.get(node)) {
                if (metrics != null) {
                    metrics.incrementEdgesProcessed();
                }
                indeg[v]--;
                if (indeg[v] == 0) {
                    queue.add(v);
                    if (metrics != null) {
                        metrics.incrementQueuePushes();
                    }
                }
            }
        }
        
        if (metrics != null) {
            long endTime = System.nanoTime();
            metrics.recordTime(endTime - startTime);
        }
        
        return order;
    }

    /**
     * Static utility method for topological sorting.
     * 
     * @param graph adjacency list representation of the DAG
     * @return list of vertices in topological order
     */
    public static List<Integer> sort(List<List<Integer>> graph) {
        TopoSort topoSort = new TopoSort();
        return topoSort.topoSort(graph);
    }

    /**
     * Static utility method for topological sorting with metrics.
     * 
     * @param graph adjacency list representation of the DAG
     * @param metrics metrics collector for operation counting
     * @return list of vertices in topological order
     */
    public static List<Integer> sort(List<List<Integer>> graph, Metrics metrics) {
        TopoSort topoSort = new TopoSort();
        return topoSort.topoSort(graph, metrics);
    }
}
