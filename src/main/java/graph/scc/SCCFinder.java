package graph.scc;

import graph.metrics.Metrics;
import java.util.*;

/**
 * Strongly Connected Components finder using Tarjan's algorithm.
 * Provides SCC detection and condensation graph construction.
 */
public class SCCFinder {
    private final int n;
    private final List<List<Integer>> adj;
    private final boolean[] visited;
    private final int[] ids;
    private final int[] low;
    private final int[] componentId;
    private int id = 0;
    private final Stack<Integer> stack = new Stack<>();
    private final List<List<Integer>> sccs = new ArrayList<>();
    private Metrics metrics;

    /**
     * Constructs SCCFinder and finds all strongly connected components.
     *
     * @param graph adjacency list representation of the directed graph
     */
    public SCCFinder(List<List<Integer>> graph) {
        this(graph, null);
    }

    /**
     * Constructs SCCFinder with metrics tracking.
     *
     * @param graph adjacency list representation of the directed graph
     * @param metrics metrics collector for operation counting
     */
    public SCCFinder(List<List<Integer>> graph, Metrics metrics) {
        this.n = graph.size();
        this.adj = graph;
        this.ids = new int[n];
        this.low = new int[n];
        this.componentId = new int[n];
        this.visited = new boolean[n];
        this.metrics = metrics;
        Arrays.fill(ids, -1);
        Arrays.fill(componentId, -1);
        
        long startTime = System.nanoTime();
        
        for (int i = 0; i < n; i++) {
            if (ids[i] == -1) {
                dfs(i);
            }
        }
        
        assignComponentIds();
        
        if (metrics != null) {
            long endTime = System.nanoTime();
            metrics.recordTime(endTime - startTime);
        }
    }

    private void dfs(int at) {
        if (metrics != null) {
            metrics.incrementDfsVisits();
        }
        
        stack.push(at);
        visited[at] = true;
        ids[at] = low[at] = id++;

        for (int to : adj.get(at)) {
            if (metrics != null) {
                metrics.incrementEdgesProcessed();
            }
            
            if (ids[to] == -1) {
                dfs(to);
            }
            if (visited[to]) {
                low[at] = Math.min(low[at], low[to]);
            }
        }

        if (ids[at] == low[at]) {
            List<Integer> component = new ArrayList<>();
            while (true) {
                int node = stack.pop();
                visited[node] = false;
                component.add(node);
                low[node] = ids[at];
                if (node == at) {
                    break;
                }
            }
            sccs.add(component);
        }
    }

    private void assignComponentIds() {
        for (int compIdx = 0; compIdx < sccs.size(); compIdx++) {
            for (int node : sccs.get(compIdx)) {
                componentId[node] = compIdx;
            }
        }
    }

    /**
     * Returns the list of strongly connected components.
     *
     * @return list where each element is a list of vertices in one SCC
     */
    public List<List<Integer>> getSCCs() {
        return sccs;
    }

    /**
     * Returns the component ID for a given vertex.
     *
     * @param vertex the vertex
     * @return component ID (0-indexed)
     */
    public int getComponentId(int vertex) {
        return componentId[vertex];
    }

    /**
     * Builds the condensation graph (DAG of SCCs).
     * Each SCC becomes a single vertex, edges connect different components.
     *
     * @return adjacency list of the condensation graph
     */
    public List<List<Integer>> buildCondensationGraph() {
        int numComponents = sccs.size();
        List<List<Integer>> condensationGraph = new ArrayList<>();
        Set<String> edgeSet = new HashSet<>();

        for (int i = 0; i < numComponents; i++) {
            condensationGraph.add(new ArrayList<>());
        }

        for (int u = 0; u < n; u++) {
            int compU = componentId[u];
            for (int v : adj.get(u)) {
                int compV = componentId[v];
                if (compU != compV) {
                    String edge = compU + "," + compV;
                    if (!edgeSet.contains(edge)) {
                        edgeSet.add(edge);
                        condensationGraph.get(compU).add(compV);
                    }
                }
            }
        }

        return condensationGraph;
    }

    /**
     * Returns the size of each component.
     *
     * @return array where index i contains the size of component i
     */
    public int[] getComponentSizes() {
        int[] sizes = new int[sccs.size()];
        for (int i = 0; i < sccs.size(); i++) {
            sizes[i] = sccs.get(i).size();
        }
        return sizes;
    }
}
