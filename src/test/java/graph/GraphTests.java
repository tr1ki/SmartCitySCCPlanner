package graph;

import graph.dagsp.DAGShortestPaths;
import graph.scc.SCCFinder;
import graph.topo.TopoSort;
import graph.metrics.Metrics;
import graph.metrics.SimpleMetrics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class GraphTests {

    public GraphTests() { }

    // -------------------- SCC Tests --------------------
    @Test
    void testSCCFinder() {
        // Simple graph with cycle 1->2->3->1
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < 4; i++) graph.add(new ArrayList<>());
        graph.get(1).add(2);
        graph.get(2).add(3);
        graph.get(3).add(1);
        graph.get(0).add(1);

        SCCFinder sccFinder = new SCCFinder(graph);
        List<List<Integer>> sccs = sccFinder.getSCCs();

        boolean hasCycle = sccs.stream().anyMatch(c -> c.containsAll(List.of(1, 2, 3)));
        Assertions.assertTrue(hasCycle, "SCCFinder should find cycle {1,2,3}");
        Assertions.assertTrue(sccs.size() >= 1, "Should have at least one SCC");
    }

    @Test
    void testSCCSingleComponent() {
        // Graph with single SCC containing all vertices
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < 3; i++) graph.add(new ArrayList<>());
        graph.get(0).add(1);
        graph.get(1).add(2);
        graph.get(2).add(0);

        SCCFinder sccFinder = new SCCFinder(graph);
        List<List<Integer>> sccs = sccFinder.getSCCs();

        Assertions.assertEquals(1, sccs.size(), "Should have exactly one SCC");
        Assertions.assertEquals(3, sccs.get(0).size(), "SCC should contain all 3 vertices");
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

        // Verify order: 0 -> [1,2] -> 3
        Assertions.assertTrue(topo.indexOf(0) < topo.indexOf(1), "0 should come before 1");
        Assertions.assertTrue(topo.indexOf(0) < topo.indexOf(2), "0 should come before 2");
        Assertions.assertTrue(topo.indexOf(1) < topo.indexOf(3), "1 should come before 3");
        Assertions.assertTrue(topo.indexOf(2) < topo.indexOf(3), "2 should come before 3");
        Assertions.assertEquals(4, topo.size(), "Topo order should contain all vertices");
    }

    @Test
    void testTopoSortLinearGraph() {
        // Linear graph: 0->1->2->3
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < 4; i++) graph.add(new ArrayList<>());
        graph.get(0).add(1);
        graph.get(1).add(2);
        graph.get(2).add(3);

        List<Integer> topo = TopoSort.sort(graph);

        Assertions.assertEquals(List.of(0, 1, 2, 3), topo, "Linear graph should have unique topo order");
    }

    // -------------------- DAG Shortest Paths Tests --------------------
    @Test
    void testShortestPath() {
        List<List<int[]>> graph = new ArrayList<>();
        for (int i = 0; i < 4; i++) graph.add(new ArrayList<>());

        // Graph: 0->1(2), 0->2(5), 1->2(3), 2->3(1)
        graph.get(0).add(new int[]{1, 2});
        graph.get(0).add(new int[]{2, 5});
        graph.get(1).add(new int[]{2, 3});
        graph.get(2).add(new int[]{3, 1});

        // Topological order for DAG
        List<Integer> topo = List.of(0, 1, 2, 3);

        int[] dist = DAGShortestPaths.shortestPath(graph, 0, topo);

        Assertions.assertEquals(0, dist[0], "Distance from source to itself should be 0");
        Assertions.assertEquals(2, dist[1], "Shortest path 0->1 should be 2");
        Assertions.assertEquals(5, dist[2], "Shortest path 0->2 should be 5 (direct)");
        Assertions.assertEquals(6, dist[3], "Shortest path 0->3 should be 6 (via 2)");
    }

    @Test
    void testShortestPathUnreachable() {
        List<List<int[]>> graph = new ArrayList<>();
        for (int i = 0; i < 3; i++) graph.add(new ArrayList<>());
        graph.get(0).add(new int[]{1, 1});

        List<Integer> topo = List.of(0, 1, 2);
        int[] dist = DAGShortestPaths.shortestPath(graph, 0, topo);

        Assertions.assertEquals(0, dist[0]);
        Assertions.assertEquals(1, dist[1]);
        Assertions.assertEquals(1_000_000, dist[2], "Vertex 2 should be unreachable");
    }

    // -------------------- Condensation Graph Tests --------------------
    @Test
    void testCondensationGraph() {
        // Graph with two cycles: 0->1->2->0 and 3->4->3, connected by 2->3
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < 5; i++) graph.add(new ArrayList<>());
        graph.get(0).add(1);
        graph.get(1).add(2);
        graph.get(2).add(0); // Cycle 1
        graph.get(2).add(3);
        graph.get(3).add(4);
        graph.get(4).add(3); // Cycle 2

        SCCFinder sccFinder = new SCCFinder(graph);
        List<List<Integer>> sccs = sccFinder.getSCCs();
        List<List<Integer>> condensation = sccFinder.buildCondensationGraph();

        // Should have 2 components
        Assertions.assertTrue(sccs.size() >= 2, "Should have at least 2 SCCs");
        
        // Condensation should be a DAG (fewer or equal vertices)
        Assertions.assertTrue(condensation.size() <= graph.size(), 
            "Condensation should have <= vertices than original");
    }

    // -------------------- Topological Sort on Condensation --------------------
    @Test
    void testTopoSortCondensation() {
        // Create graph with cycles
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < 6; i++) graph.add(new ArrayList<>());
        graph.get(0).add(1);
        graph.get(1).add(2);
        graph.get(2).add(0); // Cycle
        graph.get(2).add(3);
        graph.get(3).add(4);
        graph.get(4).add(5);

        SCCFinder sccFinder = new SCCFinder(graph);
        List<List<Integer>> condensation = sccFinder.buildCondensationGraph();
        List<Integer> topo = TopoSort.sort(condensation);

        // Topological order should be valid for condensation
        Assertions.assertEquals(condensation.size(), topo.size(), 
            "Topo order should contain all components");
    }

    // -------------------- Longest Path (Critical Path) Tests --------------------
    @Test
    void testLongestPath() {
        List<List<int[]>> graph = new ArrayList<>();
        for (int i = 0; i < 4; i++) graph.add(new ArrayList<>());

        // Graph: 0->1(2), 0->2(5), 1->3(4), 2->3(1)
        graph.get(0).add(new int[]{1, 2});
        graph.get(0).add(new int[]{2, 5});
        graph.get(1).add(new int[]{3, 4});
        graph.get(2).add(new int[]{3, 1});

        List<Integer> topo = List.of(0, 1, 2, 3);
        int[] longest = DAGShortestPaths.longestPath(graph, 0, topo);

        // Longest path 0->2->3 = 5+1 = 6
        // But 0->1->3 = 2+4 = 6, so both are same length
        Assertions.assertTrue(longest[3] >= 6, "Longest path to 3 should be at least 6");
        Assertions.assertEquals(0, longest[0], "Longest path from source to itself should be 0");
    }

    @Test
    void testCriticalPathReconstruction() {
        List<List<int[]>> graph = new ArrayList<>();
        for (int i = 0; i < 4; i++) graph.add(new ArrayList<>());
        graph.get(0).add(new int[]{1, 10});
        graph.get(0).add(new int[]{2, 5});
        graph.get(1).add(new int[]{3, 5});
        graph.get(2).add(new int[]{3, 5});

        List<Integer> topo = List.of(0, 1, 2, 3);
        DAGShortestPaths.PathResult result = DAGShortestPaths.longestPathWithParent(graph, 0, topo);

        int[] dist = result.getDistances();
        int criticalLength = DAGShortestPaths.findCriticalPathLength(dist);
        
        Assertions.assertEquals(15, criticalLength, "Critical path should be 0->1->3 = 15");
        
        List<Integer> path = DAGShortestPaths.reconstructPath(result.getParents(), 3);
        Assertions.assertFalse(path.isEmpty(), "Critical path should exist");
        Assertions.assertTrue(path.contains(0), "Path should start from source");
        Assertions.assertTrue(path.contains(3), "Path should end at target");
    }

    // -------------------- Path Reconstruction Tests --------------------
    @Test
    void testPathReconstruction() {
        List<List<int[]>> graph = new ArrayList<>();
        for (int i = 0; i < 4; i++) graph.add(new ArrayList<>());

        graph.get(0).add(new int[]{1, 2});
        graph.get(1).add(new int[]{2, 3});
        graph.get(2).add(new int[]{3, 1});

        List<Integer> topo = List.of(0, 1, 2, 3);
        DAGShortestPaths.PathResult result = DAGShortestPaths.shortestPathWithParent(graph, 0, topo);

        List<Integer> path = DAGShortestPaths.reconstructPath(result.getParents(), 3);
        Assertions.assertFalse(path.isEmpty(), "Path should exist");
        Assertions.assertEquals(0, path.get(0), "Path should start from source");
        Assertions.assertEquals(3, path.get(path.size() - 1), "Path should end at target");
    }

    @Test
    void testMetricsIntegration() {
        // Test SCC with metrics
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < 4; i++) graph.add(new ArrayList<>());
        graph.get(0).add(1);
        graph.get(1).add(2);
        graph.get(2).add(0);

        Metrics metrics = new SimpleMetrics();
        SCCFinder sccFinder = new SCCFinder(graph, metrics);
        
        Assertions.assertTrue(metrics.getDfsVisits() > 0, "Should have DFS visits");
        Assertions.assertTrue(metrics.getEdgesProcessed() > 0, "Should have processed edges");
        Assertions.assertTrue(metrics.getTimeNs() > 0, "Should have recorded time");

        // Test TopoSort with metrics
        Metrics topoMetrics = new SimpleMetrics();
        List<List<Integer>> dag = new ArrayList<>();
        for (int i = 0; i < 3; i++) dag.add(new ArrayList<>());
        dag.get(0).add(1);
        dag.get(0).add(2);

        TopoSort.sort(dag, topoMetrics);
        Assertions.assertTrue(topoMetrics.getQueuePops() > 0, "Should have queue pops");
        Assertions.assertTrue(topoMetrics.getQueuePushes() > 0, "Should have queue pushes");

        // Test DAGShortestPaths with metrics
        Metrics pathMetrics = new SimpleMetrics();
        List<List<int[]>> weighted = new ArrayList<>();
        for (int i = 0; i < 3; i++) weighted.add(new ArrayList<>());
        weighted.get(0).add(new int[]{1, 1});
        weighted.get(0).add(new int[]{2, 2});

        DAGShortestPaths.shortestPath(weighted, 0, List.of(0, 1, 2), pathMetrics);
        Assertions.assertTrue(pathMetrics.getRelaxations() > 0, "Should have relaxations");
    }

    @Test
    void testComponentIdMapping() {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < 5; i++) graph.add(new ArrayList<>());
        graph.get(0).add(1);
        graph.get(1).add(2);
        graph.get(2).add(0); // Cycle: 0,1,2
        graph.get(3).add(4); // Component: 3,4

        SCCFinder sccFinder = new SCCFinder(graph);
        int comp0 = sccFinder.getComponentId(0);
        int comp1 = sccFinder.getComponentId(1);
        int comp2 = sccFinder.getComponentId(2);

        // All cycle vertices should be in same component
        Assertions.assertEquals(comp0, comp1, "0 and 1 should be in same component");
        Assertions.assertEquals(comp1, comp2, "1 and 2 should be in same component");
    }
}
