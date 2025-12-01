package com.visualizer.algorithmvisualizer.sorting;

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public abstract class Sorting {

    protected int[] data;
    protected List<StackPane> cellNodes;
    protected HBox arrayContainer;
    protected Slider speedSlider;

    public Sorting(List<StackPane> cellNodes, HBox arrayContainer, Slider speedSlider, int[] data) {
        this.cellNodes = cellNodes;
        this.arrayContainer = arrayContainer;
        this.speedSlider = speedSlider;
        this.data = data;
    }

    /*
    Coloring the cells in the array
     */
    protected void highlight(int index) {
        Rectangle r = (Rectangle) cellNodes.get(index).getChildren().get(0);
        r.setFill(Color.YELLOW);
        r.setStroke(Color.RED);
    }

    protected void unhighlight(int index) {
        Rectangle r = (Rectangle) cellNodes.get(index).getChildren().get(0);
        r.setFill(Color.LIGHTGREY);
        r.setStroke(Color.BLACK);
    }

    protected void highlightSwap(int i, int j) {
        Rectangle r1 = (Rectangle) cellNodes.get(i).getChildren().get(0);
        Rectangle r2 = (Rectangle) cellNodes.get(j).getChildren().get(0);

        r1.setFill(Color.ORANGE);
        r2.setFill(Color.ORANGE);
    }

    protected void flashSuccess() {
        for (Node n : arrayContainer.getChildren()) {
            Rectangle r = (Rectangle) ((StackPane) n).getChildren().get(0);
            r.setFill(Color.LIGHTGREEN);
        }
    }

    protected void resetColor() {
        for (Node n : arrayContainer.getChildren()) {
            Rectangle r = (Rectangle) ((StackPane) n).getChildren().get(0);
            r.setFill(Color.LIGHTGREY);
            r.setStroke(Color.BLACK);
        }
    }

    /*
    Swapping Animation
     */
    protected double getAnimationSpeed() {
        return speedSlider.getValue();
    }

    protected Duration getSwapDuration() {
        double base = 500;
        return Duration.millis(base / getAnimationSpeed());
    }

    protected long getComparisonDelay() {
        long base = 350;
        return (long) (base / getAnimationSpeed());
    }

    protected void animateSwap(int i, int j, Runnable onFinished) {
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
                onFinished.run();
            }
        });

        pt.play();
    }

    // helper functions
    protected void swap(int x, int y) throws InterruptedException {
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

        Platform.runLater(() -> {
            unhighlight(x);
            unhighlight(y);
        });
    }

    protected void compare(int x, int y) throws InterruptedException {
        Platform.runLater(() -> {
            highlight(x);
            highlight(y);
        });

        Thread.sleep((long)(150 / getAnimationSpeed()));

        Platform.runLater(() -> {
            unhighlight(x);
            unhighlight(y);
        });

        Thread.sleep(getComparisonDelay());
    }

    protected void success() throws InterruptedException {
        Platform.runLater(() -> flashSuccess());
        Thread.sleep(250);
        Platform.runLater(() -> resetColor());
    }

    // MergeSort
    protected void pause() throws InterruptedException {
        Thread.sleep((long)(250 / getAnimationSpeed()));
    }

    protected void highlight(StackPane node) {
        Rectangle r = (Rectangle) node.getChildren().get(0);
        r.setFill(Color.YELLOW);
        r.setStroke(Color.RED);
    }

    protected void unhighlight(StackPane node) {
        Rectangle r = (Rectangle) node.getChildren().get(0);
        r.setFill(Color.LIGHTGREY);
        r.setStroke(Color.BLACK);
    }

    public abstract void sort();
}
