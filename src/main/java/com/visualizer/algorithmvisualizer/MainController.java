package com.visualizer.algorithmvisualizer;

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class MainController {

    @FXML private ChoiceBox<String> algorithmChoice;
    @FXML private HBox arrayContainer;
    @FXML private Slider speedSlider;
    @FXML private Label speedLabel;

    private int[] data;
    private List<StackPane> cellNodes = new ArrayList<>();

    @FXML
    public void initialize() {
        // set default algorithm
        algorithmChoice.getSelectionModel().selectFirst();

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            speedLabel.setText(String.format("%.1fx", newVal.doubleValue()));
        });
    }

    @FXML
    private void handleGenerate() {
        data = generateRandomArray(20);
        displayArray();
    }

    @FXML
    private void handleStart() {
        if (data == null) return;

        String algo = algorithmChoice.getValue();
        switch (algo) {
            case "Bubble Sort" -> bubbleSort();
//            case "Selection Sort" -> selectionSort();
            default -> System.out.println("Algorithm not implemented");
        }
    }

    private int[] generateRandomArray(int size) {
        Random r = new Random();
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) arr[i] = r.nextInt(300) + 10;
        return arr;
    }

    /*
    Displaying Animation
     */
    private void displayArray() {
        arrayContainer.getChildren().clear();
        cellNodes.clear();

        for (int value : data) {
            StackPane cell = createCell(value);
            cellNodes.add(cell);
            arrayContainer.getChildren().add(cell);
        }
    }

    private StackPane createCell(int value) {
        Rectangle rect = new Rectangle(50, 50);
        rect.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;");

        rect.setUserData("cellRect");   // Tag

        Label label = new Label(String.valueOf(value));
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        StackPane stack = new StackPane(rect, label);
        return stack;
    }

    private void highlight(int index) {
        Rectangle r = (Rectangle) cellNodes.get(index).getChildren().get(0);
        r.setFill(Color.YELLOW);
        r.setStroke(Color.RED);
    }

    private void unhighlight(int index) {
        Rectangle r = (Rectangle) cellNodes.get(index).getChildren().get(0);
        r.setFill(Color.WHITE);
        r.setStroke(Color.BLACK);
    }

    private void highlightSwap(int i, int j) {
        Rectangle r1 = (Rectangle) cellNodes.get(i).getChildren().get(0);
        Rectangle r2 = (Rectangle) cellNodes.get(j).getChildren().get(0);

        r1.setFill(Color.ORANGE);
        r2.setFill(Color.ORANGE);
    }

    private void flashSuccess() {
        for (Node n : arrayContainer.getChildren()) {
            Rectangle r = (Rectangle) ((StackPane) n).getChildren().get(0);
            r.setFill(Color.LIGHTGREEN);
        }
    }

    private void resetColor() {
        for (Node n : arrayContainer.getChildren()) {
            Rectangle r = (Rectangle) ((StackPane) n).getChildren().get(0);
            r.setFill(Color.WHITE);
        }
    }

    /*
    Swapping Animation
     */
    private double getAnimationSpeed() {
        return speedSlider.getValue();
    }

    private Duration getSwapDuration() {
        double base = 300;
        return Duration.millis(base / getAnimationSpeed());
    }

    private long getComparisonDelay() {
        long base = 150;
        return (long) (base / getAnimationSpeed());
    }

    private void animateSwap(int i, int j, Runnable onFinished) {
        StackPane nodeA = cellNodes.get(i);
        StackPane nodeB = cellNodes.get(j);

        double distance = nodeB.getLayoutX() - nodeA.getLayoutX();
        System.out.println("Distance NodeA: " + nodeA.getLayoutX());
        System.out.println("Distance NodeB: " + nodeB.getLayoutX());

        TranslateTransition moveA = new TranslateTransition(getSwapDuration(), nodeA);
        TranslateTransition moveB = new TranslateTransition(getSwapDuration(), nodeB);

        moveA.setByX(distance);
        moveB.setByX(-distance);
        System.out.println("Moved the nodes");

        ParallelTransition pt = new ParallelTransition(moveA, moveB);
        pt.setOnFinished(event -> {
            try {
                // Reset the positions
                nodeA.setTranslateX(0);
                nodeB.setTranslateX(0);
                System.out.println("Reset the positions");

                // Swap in HBox
                Collections.swap(cellNodes, i, j);
                arrayContainer.getChildren().setAll(cellNodes);
                System.out.println("Replaced children with setAll");

                onFinished.run();

            } catch (Exception ex) {
                ex.printStackTrace();
                onFinished.run();
            }
        });

        pt.play();
    }

    /*
    Sorting Algorithms
     */
    private void bubbleSort() {
        new Thread(() -> {
            try {
                for (int i = 0; i < data.length - 1; i++) {
                    for (int j = 0; j < data.length - i - 1; j++) {
                        int x = j;
                        int y = j + 1;

                        Platform.runLater(() -> {
                            highlight(x);
                            highlight(y);
                        });

                        Thread.sleep((long)(150 / getAnimationSpeed()));

                        if (data[x] > data[y]) {

                            // Color change while swapping
                            Platform.runLater(() -> highlightSwap(x, y));

                            int temp = data[x];
                            data[x] = data[y];
                            data[y] = temp;

                            // Block thread until swap completes
                            CountDownLatch latch = new CountDownLatch(1);

                            Platform.runLater(() -> {
                                animateSwap(x, y, latch::countDown);
                            });

                            latch.await();
                        }

                        Platform.runLater(() -> {
                            unhighlight(x);
                            unhighlight(y);
                        });

                        Thread.sleep(getComparisonDelay());
                    }
                }

                // Success flash
                Platform.runLater(() -> flashSuccess());
                Thread.sleep(250);
                Platform.runLater(() -> resetColor());

            } catch (Exception ignored) {}
        }).start();
    }
}
