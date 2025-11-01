package app;

import com.google.gson.*;
import graph.scc.SCCFinder;
import graph.topo.TopoSort;
import graph.dagsp.DAGShortestPaths;
import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        long startTime = System.nanoTime();

        // ---------- 1. Read input ----------
        JsonObject obj = JsonParser.parseReader(new FileReader("data/tasks.json")).getAsJsonObject();
        int n = obj.get("n").getAsInt();
        JsonArray edges = obj.getAsJsonArray("edges");

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

        // ---------- 2. Find SCC ----------
        SCCFinder sccFinder = new SCCFinder(graph);
        List<List<Integer>> sccs = sccFinder.getSCCs();
        System.out.println("SCCs: " + sccs);

        // ---------- 3. Topological Sort ----------
        TopoSort topoSort = new TopoSort();
        List<Integer> topo = topoSort.topoSort(graph);
        System.out.println("Topological order: " + topo);

        // ---------- 4. Shortest Paths ----------
        int src = obj.get("source").getAsInt();
        long shortestStart = System.nanoTime();
        int[] shortest = DAGShortestPaths.shortestPath(weighted, src, topo);
        long shortestEnd = System.nanoTime();

        // ---------- 5. Longest (Critical) Paths ----------
        long longestStart = System.nanoTime();
        int[] longest = DAGShortestPaths.longestPath(weighted, src, topo);
        long longestEnd = System.nanoTime();

        // ---------- 6. Output ----------
        System.out.println("Shortest distances from " + src + ": " + Arrays.toString(shortest));
        System.out.println("Longest (critical) path distances from " + src + ": " + Arrays.toString(longest));

        long totalTime = System.nanoTime() - startTime;
        System.out.println("\n⏱ Metrics:");
        System.out.println("Total runtime (ns): " + totalTime);
        System.out.println("Shortest Path time (ns): " + (shortestEnd - shortestStart));
        System.out.println("Longest Path time (ns): " + (longestEnd - longestStart));
    }
}
