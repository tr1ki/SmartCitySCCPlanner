package app;

import com.google.gson.*;
import graph.scc.SCCFinder;
import graph.topo.TopoSort;
import graph.dagsp.DAGShortestPaths;
import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
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

        // 1️⃣ SCC
        SCCFinder sccFinder = new SCCFinder(graph);
        List<List<Integer>> sccs = sccFinder.getSCCs();
        System.out.println("SCCs: " + sccs);

        // 2️⃣ Topological order
        TopoSort topoSort = new TopoSort();
        List<Integer> topo = topoSort.topoSort(graph);
        System.out.println("Topological order: " + topo);

        // 3️⃣ Shortest paths
        int src = obj.get("source").getAsInt();
        int[] dist = DAGShortestPaths.shortestPath(weighted, src, topo);
        System.out.println("Shortest distances from " + src + ": " + Arrays.toString(dist));
    }
}
