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

public class Bfs {

    private static final Color NODE_DEFAULT = Color.DODGERBLUE;
    private static final Color NODE_VISIT = Color.GREEN;
    private static final Color NODE_DONE = Color.GREEN;

    private static final Color EDGE_DEFAULT = Color.WHITE;
    private static final Color EDGE_VISIT = Color.YELLOW;

    private Timeline bfsTimeline = new Timeline();
    private double step = 0;
    private final double STEP_DURATION = 300; // ms

    private Map<GraphNode, Color> nodeColor = new HashMap<>();
    private Map<GraphEdge, Color> edgeColor = new HashMap<>();

    private AdjacencyListGraph graph;
    private Canvas canvas;

    public Bfs(AdjacencyListGraph graph, Canvas canvas) {
        this.graph = graph;
        this.canvas = canvas;
    }

    public void animateBFS(GraphNode start) {
        bfsTimeline.stop();
        bfsTimeline = new Timeline();
        step = 0;

        nodeColor.clear();
        edgeColor.clear();

        Set<GraphNode> visited = new HashSet<>();
        Queue<GraphNode> queue = new LinkedList<>();

        // Start BFS
        visited.add(start);
        queue.add(start);

        // First frame: highlight start
        addStep(() -> {
            nodeColor.put(start, NODE_VISIT);
            drawGraph();
        });

        // BFS loop
        while (!queue.isEmpty()) {
            GraphNode node = queue.poll();

            for (GraphEdge e : graph.getEdges()) {

                GraphNode next = null;
                if (e.from == node) next = e.to;
                else if (e.to == node) next = e.from;

                if (next == null) continue;

                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.add(next);

                    GraphNode finalNode = node;
                    GraphNode finalNext = next;
                    GraphEdge finalEdge = e;

                    // Highlight edge being used
                    addStep(() -> {
                        edgeColor.put(finalEdge, EDGE_VISIT);
                        nodeColor.put(finalNode, NODE_VISIT);
                        drawGraph();
                    });

                    // Highlight new discovered node
                    addStep(() -> {
                        nodeColor.put(finalNext, NODE_VISIT);
                        drawGraph();
                    });
                }
            }

            // Node finished expanding
            addStep(() -> {
                nodeColor.put(node, NODE_DONE);
                drawGraph();
            });
        }

        bfsTimeline.play();
    }

    private void addStep(Runnable r) {
        bfsTimeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(step * STEP_DURATION), e -> r.run())
        );
        step++;
    }

    private void drawGraph() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.web("#000000"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

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
