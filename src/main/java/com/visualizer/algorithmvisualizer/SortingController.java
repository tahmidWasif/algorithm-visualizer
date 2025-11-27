package com.visualizer.algorithmvisualizer;

import com.visualizer.algorithmvisualizer.sorting.BubbleSort;
import com.visualizer.algorithmvisualizer.sorting.SelectionSort;
import com.visualizer.algorithmvisualizer.sorting.Sorting;
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

public class SortingController {

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
        Sorting sorter = null;
        switch (algo) {
            case "Bubble Sort" -> {
                sorter = new BubbleSort(cellNodes, arrayContainer, speedSlider, data);
                System.out.println("BubbleSort selected");
            }
            case "Selection Sort" -> {
                sorter = new SelectionSort(cellNodes, arrayContainer, speedSlider, data);
                System.out.println("SelectionSort selected");
            }
            default -> System.out.println("Algorithm not implemented");
        }
        try {
            System.out.println("Executing sort");
            sorter.sort();
        } catch (Exception ex) {
            ex.printStackTrace();
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
}
