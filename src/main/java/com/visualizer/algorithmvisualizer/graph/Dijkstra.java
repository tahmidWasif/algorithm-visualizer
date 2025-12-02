package com.visualizer.algorithmvisualizer.graph;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.*;

public class Dijkstra {

    private static final Color NODE_DEFAULT = Color.DODGERBLUE;
    private static final Color NODE_CURRENT = Color.RED;     // node being processed
    private static final Color NODE_RELAXED = Color.ORANGE;      // node distance updated
    private static final Color NODE_DONE = Color.GREEN;    // final shortest path tree

    private static final Color EDGE_DEFAULT = Color.WHITE;
    private static final Color EDGE_RELAX = Color.YELLOW;       // edge being relaxed
    private static final Color EDGE_FINAL = Color.GREEN;        // final SPT edge

    private Timeline timeline = new Timeline();
    private double step = 0;
    private final double STEP_DURATION = 350; // ms

    private Map<GraphNode, Color> nodeColor = new HashMap<>();
    private Map<GraphEdge, Color> edgeColor = new HashMap<>();

    private AdjacencyListGraph graph;
    private Canvas canvas;

    public Dijkstra(AdjacencyListGraph graph, Canvas canvas) {
        this.graph = graph;
        this.canvas = canvas;
    }

    public void animateDijkstra(GraphNode start) {
        timeline.stop();
        timeline = new Timeline();
        step = 0;

        nodeColor.clear();
        edgeColor.clear();

        Map<GraphNode, Integer> dist = new HashMap<>();
        Map<GraphNode, GraphEdge> parentEdge = new HashMap<>();

        for (GraphNode n : graph.getNodes()) dist.put(n, Integer.MAX_VALUE);
        dist.put(start, 0);

        PriorityQueue<GraphNode> pq = new PriorityQueue<>(Comparator.comparingInt(dist::get));
        pq.add(start);

        addStep(() -> {
            nodeColor.put(start, NODE_CURRENT);
            drawGraph();
        });

        while (!pq.isEmpty()) {
            GraphNode u = pq.poll();

            GraphNode finalU = u;
            addStep(() -> {
                nodeColor.put(finalU, NODE_CURRENT);
                drawGraph();
            });

            // Relax all neighbors
            for (GraphEdge e : graph.getEdges()) {
                GraphNode v = null;

                if (e.from == u) v = e.to;
                else if (e.to == u) v = e.from;

                if (v == null) continue;

                GraphNode finalV = v;
                GraphEdge finalE = e;

                int newDist = dist.get(u) + e.weight;

                // Visualize relaxation attempt
                addStep(() -> {
                    edgeColor.put(finalE, EDGE_RELAX);
                    drawGraph();
                });

                if (newDist < dist.get(v)) {
                    dist.put(v, newDist);
                    parentEdge.put(v, e);
                    pq.add(v);

                    // Node gets relaxed (distance improved)
                    addStep(() -> {
                        nodeColor.put(finalV, NODE_RELAXED);
                        drawGraph();
                    });
                }

                // Clear relaxation highlight
                addStep(() -> {
                    edgeColor.put(finalE, edgeColor.getOrDefault(finalE, EDGE_DEFAULT));
                    drawGraph();
                });
            }

            // Mark processed
            addStep(() -> {
                nodeColor.put(finalU, NODE_DONE);
                drawGraph();
            });
        }

        // Final shortest-path-tree coloring (optional)
        for (GraphEdge e : parentEdge.values()) {
            GraphEdge finalE = e;
            addStep(() -> {
                edgeColor.put(finalE, EDGE_FINAL);
                drawGraph();
            });
        }

        timeline.play();
    }

    private void addStep(Runnable r) {
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(step * STEP_DURATION), e -> r.run())
        );
        step++;
    }


    private void drawGraph() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.web("#000000"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setLineWidth(2);

        // Draw edges
        for (GraphEdge edge : graph.getEdges()) {
            gc.setStroke(edgeColor.getOrDefault(edge, EDGE_DEFAULT));
            gc.setLineWidth(2);

            GraphNode u = edge.from;
            GraphNode v = edge.to;

            gc.strokeLine(u.x, u.y, v.x, v.y);

            gc.setFill(Color.WHITE);
            double midX = (u.x + v.x) / 2;
            double midY = (u.y + v.y) / 2;
            gc.fillText(String.valueOf(edge.weight), midX + 7, midY - 7);
        }

        // Draw nodes
        for (GraphNode node : graph.getNodes()) {
            gc.setFill(nodeColor.getOrDefault(node, NODE_DEFAULT));

            gc.fillOval(node.x - 20, node.y - 20, 40, 40);

            gc.setFill(Color.WHITE);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText(node.name, node.x, node.y);
        }
    }
}
