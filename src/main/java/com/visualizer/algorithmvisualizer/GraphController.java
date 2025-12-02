package com.visualizer.algorithmvisualizer;

import com.visualizer.algorithmvisualizer.graph.*;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.HashSet;
import java.util.Set;

public class GraphController {

    @FXML private Canvas canvas;
    @FXML private Button addEdgeBtn;
    @FXML private Button clearBtn;
    @FXML private ComboBox<String> algorithmSelector;
    @FXML private Button runBtn;
    @FXML private TextField weightField;

    private GraphicsContext gc;

    private AdjacencyListGraph graph = new AdjacencyListGraph();

    private GraphNode selectedA = null;
    private GraphNode selectedB = null;

    private int nodeCounter = 0;

    @FXML
    public void initialize() {
        gc = canvas.getGraphicsContext2D();
        algorithmSelector.getItems().addAll("DFS", "BFS", "Dijkstra", "A*");

        draw();

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleCanvasClick);

        addEdgeBtn.setOnAction(e -> handleAddEdge());
        clearBtn.setOnAction(e -> clearGraph());
        runBtn.setOnAction(e -> runAlgorithm());
    }

    private void draw() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.web("#000000"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw edges
        for (GraphEdge edge : graph.getEdges()) {
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);

            var u = edge.from;
            var v = edge.to;

            gc.strokeLine(u.x, u.y, v.x, v.y);

            // weight
            gc.setFill(Color.WHITE);
            double midX = (u.x + v.x) / 2;
            double midY = (u.y + v.y) / 2;
            gc.fillText(String.valueOf(edge.weight), midX + 7, midY - 7);
        }

        // Draw nodes
        for (GraphNode node : graph.getNodes()) {
            if (node == selectedA || node == selectedB) {
                gc.setFill(Color.ORANGE);
            } else {
                gc.setFill(Color.DODGERBLUE);
            }

            gc.fillOval(node.x - 20, node.y - 20, 40, 40);

            gc.setFill(Color.WHITE);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText(node.name, node.x, node.y);
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void handleCanvasClick(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        GraphNode clicked = getNodeAt(x, y);

        if (clicked == null) {
            // Clicked empty space, so create new node
            graph.addNode("N" + graph.getNodes().size(), x, y);
            selectedA = selectedB = null;
        } else {
            if (selectedA == null) {
                selectedA = clicked;
            } else if (selectedB == null && clicked != selectedA) {
                selectedB = clicked;
            } else {
                selectedA = clicked;
                selectedB = null;
            }
        }

        draw();
    }

    private GraphNode getNodeAt(double x, double y) {
        for (GraphNode n : graph.getNodes()) {
            double dx = x - n.x;
            double dy = y - n.y;
            if (Math.sqrt(dx*dx + dy*dy) <= 20) {
                return n;
            }
        }

        return null;
    }

    private void handleAddEdge() {
        if (selectedA == null || selectedB == null) {
            showAlert("Please select two nodes by clicking them.");
            return;
        }

        int weight;
        try {
            weight = Integer.parseInt(weightField.getText());
        } catch (Exception e) {
            weight = 1;
        }

        graph.addEdge(selectedA, selectedB, weight);

        draw();
    }

    private void clearGraph() {
        graph.clear();
        nodeCounter = 0;
        draw();
    }

    private void runAlgorithm() {
        String alg = algorithmSelector.getValue();
        if (alg == null) return;

        switch (alg) {
            case "DFS" -> animateDFS();
            case "BFS" -> animateBFS();
            case "Dijkstra" -> animateDijkstra();
            case "A*" -> animateAStar();
        }
    }

    // -----------------------------
    // Algorithm animation skeletons
    // -----------------------------

    private void animateDFS() {
        System.out.println("DFS running...");
        if (graph.getNodes().isEmpty()) return;

        Dfs dfsRunner = new Dfs(graph, canvas);

        GraphNode start = graph.getNodes().get(0);
        dfsRunner.animateDFS(start);
    }

    private void animateBFS() {
        System.out.println("BFS running...");
        if (graph.getNodes().isEmpty()) return;

        Bfs bfsRunner = new Bfs(graph, canvas);

        GraphNode start = graph.getNodes().get(0);
        bfsRunner.animateBFS(start);
    }

    private void animateDijkstra() {
        System.out.println("Dijkstra running...");
        // TODO: Replace with animation callback
    }

    private void animateAStar() {
        System.out.println("A* running...");
        // TODO: Replace with animation callback
    }
}
