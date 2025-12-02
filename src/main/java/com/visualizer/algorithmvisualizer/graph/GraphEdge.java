package com.visualizer.algorithmvisualizer.graph;

public class GraphEdge {
    public GraphNode from, to;
    public int weight;

    public GraphEdge(GraphNode from, GraphNode to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }
}
