package graph.scc;

import java.util.*;

public class SCCFinder {
    private int n;
    private List<List<Integer>> adj;
    private boolean[] visited;
    private int[] ids, low;
    private int id = 0;
    private Stack<Integer> stack = new Stack<>();
    private List<List<Integer>> sccs = new ArrayList<>();

    public SCCFinder(List<List<Integer>> graph) {
        this.n = graph.size();
        this.adj = graph;
        ids = new int[n];
        low = new int[n];
        visited = new boolean[n];
        Arrays.fill(ids, -1);
        for (int i = 0; i < n; i++)
            if (ids[i] == -1)
                dfs(i);
    }

    private void dfs(int at) {
        stack.push(at);
        visited[at] = true;
        ids[at] = low[at] = id++;

        for (int to : adj.get(at)) {
            if (ids[to] == -1) dfs(to);
            if (visited[to]) low[at] = Math.min(low[at], low[to]);
        }

        if (ids[at] == low[at]) {
            List<Integer> component = new ArrayList<>();
            while (true) {
                int node = stack.pop();
                visited[node] = false;
                component.add(node);
                low[node] = ids[at];
                if (node == at) break;
            }
            sccs.add(component);
        }
    }

    public List<List<Integer>> getSCCs() {
        return sccs;
    }
}
