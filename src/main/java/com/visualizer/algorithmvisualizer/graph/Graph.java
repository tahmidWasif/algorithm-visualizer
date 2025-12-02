package com.visualizer.algorithmvisualizer.graph;

import java.util.List;

public interface Graph {
    List<GraphNode> getNodes();
    List<GraphEdge> getEdges();
    void addNode(String name, double x, double y);
    void addEdge(GraphNode a, GraphNode b, int weight);
    void clear();
    List<GraphNode> getNeighbors(GraphNode node);
}

