package graph;

import graph.dagsp.DAGShortestPaths;
import graph.scc.SCCFinder;
import graph.topo.TopoSort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class GraphTests {

    public GraphTests() { }

    // -------------------- SCC Tests --------------------
    @Test
    void testSCCFinder() {
        // Простой граф с циклом 1->2->3->1
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < 4; i++) graph.add(new ArrayList<>());
        graph.get(1).add(2);
        graph.get(2).add(3);
        graph.get(3).add(1);
        graph.get(0).add(1);

        SCCFinder sccFinder = new SCCFinder(graph);
        List<List<Integer>> sccs = sccFinder.getSCCs();

        boolean hasCycle = sccs.stream().anyMatch(c -> c.containsAll(List.of(1, 2, 3)));
        Assertions.assertTrue(hasCycle, "SCCFinder не нашел цикл {1,2,3}");
    }

    // -------------------- Topological Sort Tests --------------------
    @Test
    void testTopoSort() {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < 4; i++) graph.add(new ArrayList<>());
        graph.get(0).add(1);
        graph.get(0).add(2);
        graph.get(1).add(3);
        graph.get(2).add(3);

        List<Integer> topo = TopoSort.sort(graph);

        // Проверяем порядок: 0 -> [1,2] -> 3
        Assertions.assertTrue(topo.indexOf(0) < topo.indexOf(1));
        Assertions.assertTrue(topo.indexOf(0) < topo.indexOf(2));
        Assertions.assertTrue(topo.indexOf(1) < topo.indexOf(3));
        Assertions.assertTrue(topo.indexOf(2) < topo.indexOf(3));
    }

    // -------------------- DAG Shortest Paths Tests --------------------
    @Test
    void testShortestPath() {
        List<List<int[]>> graph = new ArrayList<>();
        for (int i = 0; i < 4; i++) graph.add(new ArrayList<>());

        // граф: 0->1(2), 0->2(5), 1->2(3), 2->3(1)
        graph.get(0).add(new int[]{1, 2});
        graph.get(0).add(new int[]{2, 5});
        graph.get(1).add(new int[]{2, 3});
        graph.get(2).add(new int[]{3, 1});

        // Топологическая сортировка для DAG
        List<Integer> topo = List.of(0, 1, 2, 3);

        // Засекаем время работы
        long start = System.nanoTime();
        int[] dist = DAGShortestPaths.shortestPath(graph, 0, topo);
        long end = System.nanoTime();

        System.out.println("DAGShortestPaths: Time (ns) = " + (end - start));
        System.out.println("Distances: 0->1=" + dist[1] + ", 0->2=" + dist[2] + ", 0->3=" + dist[3]);

        Assertions.assertEquals(2, dist[1], "Кратчайший путь 0->1");
        Assertions.assertEquals(5, dist[2], "Кратчайший путь 0->2");
        Assertions.assertEquals(6, dist[3], "Кратчайший путь 0->3");
    }

    // -------------------- Utility: Test multiple datasets --------------------
    void runMultipleDAGTests(List<List<List<int[]>>> datasets) {
        List<List<Integer>> topologies = new ArrayList<>();
        // Пример: для каждого графа указываем топологический порядок
        for (List<List<int[]>> g : datasets) {
            List<Integer> topo = new ArrayList<>();
            for (int i = 0; i < g.size(); i++) topo.add(i);
            topologies.add(topo);
        }

        for (int i = 0; i < datasets.size(); i++) {
            long start = System.nanoTime();
            int[] dist = DAGShortestPaths.shortestPath(datasets.get(i), 0, topologies.get(i));
            long end = System.nanoTime();
            System.out.println("Dataset #" + i + ": Time (ns) = " + (end - start));
        }
    }
}
