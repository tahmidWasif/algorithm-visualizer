package com.visualizer.algorithmvisualizer.graph;

import java.util.ArrayList;
import java.util.List;

public class AdjacencyListGraph implements Graph {

    private final List<GraphNode> nodes = new ArrayList<>();
    private final List<GraphEdge> edges = new ArrayList<>();

    @Override
    public List<GraphNode> getNodes() {
        return nodes;
    }

    @Override
    public List<GraphEdge> getEdges() {
        return edges;
    }

    @Override
    public void addNode(String name, double x, double y) {
        nodes.add(new GraphNode(name, x, y));
    }

    @Override
    public void addEdge(GraphNode a, GraphNode b, int weight) {
        edges.add(new GraphEdge(a, b, weight));
//        edges.add(new GraphEdge(b, a, weight));
    }

    @Override
    public void clear() {
        nodes.clear();
        edges.clear();
    }

    @Override
    public List<GraphNode> getNeighbors(GraphNode node) {
        List<GraphNode> list = new ArrayList<>();
        for (GraphEdge e : edges) {
            if (e.from == node) list.add(e.to);
            if (e.to == node) list.add(e.from);
        }

        return list;
    }
}
