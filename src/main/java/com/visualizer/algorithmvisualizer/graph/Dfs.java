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

public class Dfs {
    private static final Color NODE_DEFAULT = Color.DODGERBLUE;
    private static final Color NODE_VISIT = Color.GREEN;
    private static final Color NODE_BACKTRACK = Color.ORANGE;
    private static final Color NODE_DONE = Color.GREEN;

    private static final Color EDGE_DEFAULT = Color.WHITE;
    private static final Color EDGE_VISIT = Color.YELLOW;
    private static final Color EDGE_BACKTRACK = Color.RED;

    private Timeline dfsTimeline = new Timeline();
    private double step = 0;
    private final double STEP_DURATION = 300; // ms

    private Map<GraphNode, Color> nodeColor = new HashMap<>();
    private Map<GraphEdge, Color> edgeColor = new HashMap<>();

    private Set<GraphNode> visited = new HashSet<>();
    private AdjacencyListGraph graph;
    private Canvas canvas;

    public Dfs(AdjacencyListGraph graph, Canvas canvas) {
        this.graph = graph;
        this.canvas = canvas;
    }

    public void animateDFS(GraphNode start) {
        dfsTimeline.stop();

        dfsTimeline = new Timeline();
        step = 0;

        visited = new HashSet<>();

        dfsVisit(start, visited);

        dfsTimeline.play();

    }

    private void dfsVisit(GraphNode node, Set<GraphNode> visited) {
        visited.add(node);

        // Color node as visited
        addStep(() -> {
            nodeColor.put(node, NODE_VISIT);
            drawGraph();
        });

        // Visit neighbors
        for (GraphEdge e : graph.getEdges()) {
            GraphNode next = null;

            if (e.from == node) {
                next = e.to;
            } else if (e.to == node) {
                next = e.from;
            }

            if (next != null) {
                if (!visited.contains(next)) {
                    addStep(() -> {
                        edgeColor.put(e, EDGE_VISIT);
                        drawGraph();
                    });

                    dfsVisit(next, visited);

                    // Backtrack
                    addStep(() -> {
                        edgeColor.put(e, EDGE_BACKTRACK);
                        drawGraph();
                    });
                }
            }
        }

        // Backtrack Node
        addStep(() -> {
            nodeColor.put(node, NODE_BACKTRACK);
            drawGraph();
        });

        // Mark node done
        addStep(() -> {
            nodeColor.put(node, NODE_DONE);
            drawGraph();
        });
    }

    private void addStep(Runnable r) {
        dfsTimeline.getKeyFrames().add(
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
        for (GraphEdge edge : graph.getEdges()) {
            gc.setStroke(edgeColor.getOrDefault(edge, EDGE_DEFAULT));
            gc.setLineWidth(2);

            var u = edge.from;
            var v = edge.to;

            gc.strokeLine(u.x, u.y, v.x, v.y);

            gc.setFill(Color.WHITE);
            double midX = (u.x + v.x) / 2;
            double midY = (u.y + v.y) / 2;
            gc.fillText(String.valueOf(edge.weight), midX + 5, midY - 5);
        }

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
