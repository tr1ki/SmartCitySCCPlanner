# Smart City / Smart Campus Scheduling Planner

A Java application for analyzing task dependencies using graph algorithms: Strongly Connected Components (SCC), Topological Sort, and Shortest/Longest Paths in DAGs.

## Overview

This project implements algorithms for processing city-service task dependencies:
- **SCC Detection**: Finds strongly connected components using Tarjan's algorithm
- **Condensation Graph**: Builds a DAG from SCC components
- **Topological Sort**: Computes valid ordering using Kahn's algorithm
- **Shortest/Longest Paths**: Finds optimal paths in DAGs using dynamic programming

## Project Structure

```
SmartCitySCCPlanner/
├── src/
│   ├── main/java/
│   │   ├── app/
│   │   │   └── Main.java              # Main application
│   │   └── graph/
│   │       ├── scc/
│   │       │   └── SCCFinder.java     # Tarjan's SCC algorithm
│   │       ├── topo/
│   │       │   └── TopoSort.java      # Kahn's topological sort
│   │       ├── dagsp/
│   │       │   └── DAGShortestPaths.java  # Shortest/longest paths
│   │       └── metrics/
│   │           ├── Metrics.java        # Metrics interface
│   │           └── SimpleMetrics.java # Metrics implementation
│   └── test/java/
│       └── graph/
│           └── GraphTests.java        # JUnit tests
├── data/
│   ├── small1.json, small2.json, small3.json  # Small datasets (6-10 nodes)
│   ├── medium1.json, medium2.json, medium3.json  # Medium datasets (10-20 nodes)
│   ├── large1.json, large2.json, large3.json  # Large datasets (20-50 nodes)
│   └── tasks.json                     # Default dataset
└── pom.xml                            # Maven configuration
```

## Requirements

- **Java**: JDK 21 or higher
- **Maven**: 3.6+ (for building and testing)

## Building the Project

From the project root directory:

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package as JAR
mvn package
```

## Usage

### Process All Datasets

Run all 9 test datasets:

```bash
mvn exec:java -Dexec.mainClass="app.Main"
```

Or with compiled JAR:

```bash
java -cp target/SmartCitySCCPlanner-1.0.jar app.Main
```

### Process Single Dataset

Process only `tasks.json`:

```bash
mvn exec:java -Dexec.mainClass="app.Main" -Dexec.args="--single"
```

Or:

```bash
java -cp target/SmartCitySCCPlanner-1.0.jar app.Main --single
```

## Dataset Format

Each JSON file follows this structure:

```json
{
  "directed": true,
  "n": <number_of_vertices>,
  "edges": [
    {"u": 0, "v": 1, "w": 2},
    {"u": 1, "v": 2, "w": 3}
  ],
  "source": <source_vertex_index>,
  "weight_model": "edge"
}
```

- `directed`: Always `true` for directed graphs
- `n`: Number of vertices (0-indexed)
- `edges`: Array of edges with `u` (source), `v` (destination), `w` (weight)
- `source`: Starting vertex for path algorithms
- `weight_model`: `"edge"` for edge-weighted graphs

## Algorithm Details

### 1. Strongly Connected Components (SCC)

- **Algorithm**: Tarjan's algorithm
- **Complexity**: O(V + E)
- **Output**: List of SCCs, component sizes, component ID mapping

### 2. Condensation Graph

- **Purpose**: Converts cyclic graph to DAG by collapsing SCCs
- **Method**: Each SCC becomes a single vertex, edges connect different components
- **Output**: DAG adjacency list

### 3. Topological Sort

- **Algorithm**: Kahn's algorithm (BFS-based)
- **Complexity**: O(V + E)
- **Input**: Condensation graph (DAG)
- **Output**: Valid topological ordering of components

### 4. Shortest Paths in DAG

- **Algorithm**: Dynamic programming over topological order
- **Complexity**: O(V + E)
- **Output**: Shortest distances from source to all components

### 5. Longest Paths (Critical Path)

- **Algorithm**: Same DP approach with maximization
- **Complexity**: O(V + E)
- **Output**: Longest distances, critical path length, reconstructed path

## Metrics and Instrumentation

The application tracks detailed metrics for each algorithm:

- **DFS Visits**: Number of DFS calls (SCC algorithm)
- **Edges Processed**: Total edges examined
- **Queue Operations**: Pops and pushes (TopoSort)
- **Relaxations**: Edge relaxations (path algorithms)
- **Execution Time**: Nanosecond-precision timing

Metrics are displayed after each algorithm execution.

## Running Tests

Execute JUnit tests:

```bash
mvn test
```

Test coverage includes:
- SCC detection with cycles
- Topological sort validation
- Shortest path correctness
- Condensation graph construction
- Critical path reconstruction

## Output Example

```
Processing: data/small1.json

--- SCC Analysis ---
Number of SCCs: 3
Component sizes: [3, 1, 4]
SCCs: [[0, 1, 2], [3], [4, 5, 6, 7]]
=== Metrics ===
DFS visits: 8
Edges processed: 10
Time (ns): 150000
Time (ms): 0.15

--- Condensation Graph ---
Number of components (nodes in condensation): 3
Condensation graph edges: 4

--- Topological Sort (Condensation) ---
Topological order: [0, 1, 2]
=== Metrics ===
Queue pops: 3
Queue pushes: 1
Edges processed: 4
Time (ns): 50000

--- Shortest Paths (Condensation DAG) ---
Shortest distances from component 0: [0, 3, 10]
=== Metrics ===
Relaxations: 4
Time (ns): 20000

--- Longest Paths (Critical Path) ---
Longest distances from component 0: [0, 7, 18]
Critical path length: 18
Critical path (components): [0, 2]
=== Metrics ===
Relaxations: 4
Time (ns): 25000
```

## Dataset Summary

| Category | Files | Nodes | Edges | Description |
|----------|-------|-------|-------|-------------|
| Small | small1-3 | 6-10 | 8-12 | Simple cases, 1-2 cycles |
| Medium | medium1-3 | 10-20 | 20-40 | Mixed structures, multiple SCCs |
| Large | large1-3 | 20-50 | 50-120 | Performance tests, complex structures |

All datasets:
- Are directed graphs (`directed: true`)
- Use edge weight model (`weight_model: "edge"`)
- Have weights in range 1-10
- Contain at least one SCC (cycle)
- Use `source: 0` as starting vertex

## Development

### Code Quality

- **Packages**: Clean separation (`graph.scc`, `graph.topo`, `graph.dagsp`, `graph.metrics`)
- **Documentation**: Javadoc comments for all public classes and methods
- **Testing**: JUnit tests with edge case coverage
- **Metrics**: Comprehensive instrumentation interface

### Extending

To add new algorithms:
1. Create class in appropriate package under `graph/`
2. Implement with `Metrics` support (optional parameter)
3. Add tests in `GraphTests.java`
4. Update `Main.java` if integration needed

## License

This project is created for educational purposes (Assignment 4).

## Author

Smart City SCC Planner - Graph Algorithms Assignment
