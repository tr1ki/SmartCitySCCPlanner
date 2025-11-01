package graph;

import graph.scc.SCCFinder;
import graph.dagsp.DAGShortestPaths;
import graph.topo.TopoSort;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class GraphTests {

    @Test
    void testSCCFinder() {
        // Простой граф с циклом 1 -> 2 -> 3 -> 1
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < 4; i++) graph.add(new ArrayList<>());
        graph.get(1).add(2);
        graph.get(2).add(3);
        graph.get(3).add(1);
        graph.get(0).add(1);

        SCCFinder sccFinder = new SCCFinder(graph);
        List<List<Integer>> sccs = sccFinder.getSCCs();

        // Проверяем, что есть 1 SCC с {1,2,3}
        boolean hasCycle = sccs.stream().anyMatch(c -> c.containsAll(List.of(1, 2, 3)));
        assertTrue(hasCycle, "SCCFinder не нашел цикл {1,2,3}");
    }

    @Test
    void testShortestPath() {
        // Простой DAG
        List<List<int[]>> graph = new ArrayList<>();
        for (int i = 0; i < 4; i++) graph.add(new ArrayList<>());
        graph.get(0).add(new int[]{1, 2});
        graph.get(1).add(new int[]{2, 3});
        graph.get(0).add(new int[]{2, 5});
        graph.get(2).add(new int[]{3, 1});

        List<Integer> topo = List.of(0, 1, 2, 3);
        int[] dist = DAGShortestPaths.shortestPath(graph, 0, topo);

        assertEquals(6, dist[3], "Кратчайший путь от 0 до 3 должен быть 6");
    }
}
