package graph.topo;

import java.util.*;

public class TopoSort {
    public List<Integer> topoSort(List<List<Integer>> graph) {
        int n = graph.size();
        int[] indeg = new int[n];
        for (List<Integer> list : graph)
            for (int v : list)
                indeg[v]++;
        Queue<Integer> q = new LinkedList<>();
        for (int i = 0; i < n; i++)
            if (indeg[i] == 0)
                q.add(i);

        List<Integer> order = new ArrayList<>();
        while (!q.isEmpty()) {
            int node = q.poll();
            order.add(node);
            for (int v : graph.get(node)) {
                if (--indeg[v] == 0)
                    q.add(v);
            }
        }
        return order;
    }

    // Статический метод для удобства использования
    public static List<Integer> sort(List<List<Integer>> graph) {
        TopoSort topoSort = new TopoSort();
        return topoSort.topoSort(graph);
    }
}
