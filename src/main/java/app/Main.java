package app;

import com.google.gson.*;
import graph.scc.SCCFinder;
import graph.topo.TopoSort;
import graph.dagsp.DAGShortestPaths;
import graph.dagsp.DAGShortestPaths.PathResult;
import graph.metrics.Metrics;
import graph.metrics.SimpleMetrics;

import java.io.*;
import java.util.*;

/**
 * Main application for Smart City / Smart Campus Scheduling.
 * Processes graph datasets to find SCCs, topological order, and shortest/longest paths.
 */
public class Main {
    
    private static final String[] DATASETS = {
        "small1.json", "small2.json", "small3.json",
        "medium1.json", "medium2.json", "medium3.json",
        "large1.json", "large2.json", "large3.json"
    };
    
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--single")) {
            // Process single dataset (tasks.json for backward compatibility)
            processDataset("data/tasks.json");
        } else {
            // Process all 9 datasets
            System.out.println("=== Processing all datasets ===\n");
            for (String dataset : DATASETS) {
                processDataset("data/" + dataset);
                System.out.println("\n" + "=".repeat(80) + "\n");
            }
        }
    }
    
    /**
     * Processes a single dataset file.
     * 
     * @param filename path to JSON dataset file
     */
    private static void processDataset(String filename) {
        try {
            System.out.println("Processing: " + filename);
            
            // Load graph from JSON
            GraphData graphData = loadGraphFromJson(filename);
            
            // Step 1: Find SCCs with metrics
            Metrics sccMetrics = new SimpleMetrics();
            SCCFinder sccFinder = new SCCFinder(graphData.graph, sccMetrics);
            List<List<Integer>> sccs = sccFinder.getSCCs();
            int[] componentSizes = sccFinder.getComponentSizes();
            
            System.out.println("\n--- SCC Analysis ---");
            System.out.println("Number of SCCs: " + sccs.size());
            System.out.println("Component sizes: " + Arrays.toString(componentSizes));
            System.out.println("SCCs: " + sccs);
            sccMetrics.printMetrics();
            
            // Step 2: Build condensation graph
            long condStart = System.nanoTime();
            List<List<Integer>> condensationGraph = sccFinder.buildCondensationGraph();
            long condTime = System.nanoTime() - condStart;
            
            System.out.println("\n--- Condensation Graph ---");
            System.out.println("Number of components (nodes in condensation): " + condensationGraph.size());
            System.out.println("Condensation graph edges: " + countEdges(condensationGraph));
            System.out.println("Condensation build time: " + condTime / 1_000_000.0 + " ms");
            
            // Step 3: Topological sort of condensation graph with metrics
            Metrics topoMetrics = new SimpleMetrics();
            List<Integer> topoOrder = TopoSort.sort(condensationGraph, topoMetrics);
            
            System.out.println("\n--- Topological Sort (Condensation) ---");
            System.out.println("Topological order: " + topoOrder);
            topoMetrics.printMetrics();
            
            // Map original source vertex to its component
            int originalSource = graphData.source;
            int sourceComponent = sccFinder.getComponentId(originalSource);
            System.out.println("Source vertex: " + originalSource + " -> Component: " + sourceComponent);
            
            // Step 4: Build weighted condensation graph for shortest/longest paths
            List<List<int[]>> weightedCondensation = buildWeightedCondensation(
                graphData.weightedGraph, sccFinder, condensationGraph
            );
            
            // Step 5: Shortest paths in condensation DAG with metrics
            Metrics shortestMetrics = new SimpleMetrics();
            int[] shortestDist = DAGShortestPaths.shortestPath(
                weightedCondensation, sourceComponent, topoOrder, shortestMetrics
            );
            
            System.out.println("\n--- Shortest Paths (Condensation DAG) ---");
            System.out.println("Shortest distances from component " + sourceComponent + ": " 
                + Arrays.toString(shortestDist));
            shortestMetrics.printMetrics();
            
            // Step 6: Longest paths (critical path) in condensation DAG with metrics
            Metrics longestMetrics = new SimpleMetrics();
            PathResult longestResult = DAGShortestPaths.longestPathWithParent(
                weightedCondensation, sourceComponent, topoOrder, longestMetrics
            );
            
            int[] longestDist = longestResult.getDistances();
            int criticalPathLength = DAGShortestPaths.findCriticalPathLength(longestDist);
            
            System.out.println("\n--- Longest Paths (Critical Path) ---");
            System.out.println("Longest distances from component " + sourceComponent + ": " 
                + Arrays.toString(longestDist));
            System.out.println("Critical path length: " + criticalPathLength);
            
            // Find target component with maximum distance
            int targetComponent = -1;
            for (int i = 0; i < longestDist.length; i++) {
                if (longestDist[i] == criticalPathLength) {
                    targetComponent = i;
                    break;
                }
            }
            
            if (targetComponent != -1 && criticalPathLength > Integer.MIN_VALUE + 1000000) {
                List<Integer> criticalPath = DAGShortestPaths.reconstructPath(
                    longestResult.getParents(), targetComponent
                );
                System.out.println("Critical path (components): " + criticalPath);
            }
            
            longestMetrics.printMetrics();
            
            // Summary metrics
            System.out.println("\n--- Summary Metrics ---");
            System.out.println("Total vertices: " + graphData.n);
            System.out.println("Total edges: " + graphData.weightedGraph.stream()
                .mapToInt(List::size).sum());
            System.out.println("Weight model: " + graphData.weightModel);
            
        } catch (Exception e) {
            System.err.println("Error processing " + filename + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Loads graph data from JSON file.
     * 
     * @param filename JSON file path
     * @return GraphData object containing graph structures
     * @throws IOException if file cannot be read
     */
    private static GraphData loadGraphFromJson(String filename) throws IOException {
        JsonObject obj = JsonParser.parseReader(new FileReader(filename)).getAsJsonObject();
        int n = obj.get("n").getAsInt();
        JsonArray edges = obj.getAsJsonArray("edges");
        int source = obj.get("source").getAsInt();
        String weightModel = obj.has("weight_model") 
            ? obj.get("weight_model").getAsString() 
            : "edge";

        List<List<Integer>> graph = new ArrayList<>();
        List<List<int[]>> weighted = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
            weighted.add(new ArrayList<>());
        }

        for (JsonElement e : edges) {
            JsonObject ed = e.getAsJsonObject();
            int u = ed.get("u").getAsInt();
            int v = ed.get("v").getAsInt();
            int w = ed.get("w").getAsInt();
            graph.get(u).add(v);
            weighted.get(u).add(new int[]{v, w});
        }

        return new GraphData(n, graph, weighted, source, weightModel);
    }
    
    /**
     * Builds weighted condensation graph from original weighted graph.
     * Aggregates edge weights between components.
     * 
     * @param originalWeighted original weighted adjacency list
     * @param sccFinder SCC finder with component mappings
     * @param condensationGraph condensation graph structure
     * @return weighted condensation graph
     */
    private static List<List<int[]>> buildWeightedCondensation(
            List<List<int[]>> originalWeighted,
            SCCFinder sccFinder,
            List<List<Integer>> condensationGraph) {
        
        int numComponents = condensationGraph.size();
        List<List<int[]>> weightedCondensation = new ArrayList<>();
        Map<String, Integer> minWeight = new HashMap<>(); // Track minimum weight between components
        
        for (int i = 0; i < numComponents; i++) {
            weightedCondensation.add(new ArrayList<>());
        }
        
        // Build edges between components with minimum weight
        for (int u = 0; u < originalWeighted.size(); u++) {
            int compU = sccFinder.getComponentId(u);
            for (int[] edge : originalWeighted.get(u)) {
                int v = edge[0];
                int w = edge[1];
                int compV = sccFinder.getComponentId(v);
                
                if (compU != compV) {
                    String key = compU + "," + compV;
                    minWeight.put(key, Math.min(minWeight.getOrDefault(key, Integer.MAX_VALUE), w));
                }
            }
        }
        
        // Build adjacency list from min weights
        for (Map.Entry<String, Integer> entry : minWeight.entrySet()) {
            String[] parts = entry.getKey().split(",");
            int compU = Integer.parseInt(parts[0]);
            int compV = Integer.parseInt(parts[1]);
            int weight = entry.getValue();
            weightedCondensation.get(compU).add(new int[]{compV, weight});
        }
        
        return weightedCondensation;
    }
    
    /**
     * Counts total number of edges in a graph.
     * 
     * @param graph adjacency list
     * @return total edge count
     */
    private static int countEdges(List<List<Integer>> graph) {
        return graph.stream().mapToInt(List::size).sum();
    }
    
    /**
     * Container class for graph data.
     */
    private static class GraphData {
        final int n;
        final List<List<Integer>> graph;
        final List<List<int[]>> weightedGraph;
        final int source;
        final String weightModel;
        
        GraphData(int n, List<List<Integer>> graph, List<List<int[]>> weightedGraph, 
                  int source, String weightModel) {
            this.n = n;
            this.graph = graph;
            this.weightedGraph = weightedGraph;
            this.source = source;
            this.weightModel = weightModel;
        }
    }
}
