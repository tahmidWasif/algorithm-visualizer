package com.visualizer.algorithmvisualizer;

import com.visualizer.algorithmvisualizer.sorting.BubbleSort;
import com.visualizer.algorithmvisualizer.sorting.InsertionSort;
import com.visualizer.algorithmvisualizer.sorting.SelectionSort;
import com.visualizer.algorithmvisualizer.sorting.Sorting;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SortingController {

    @FXML private ChoiceBox<String> algorithmChoice;
    @FXML private HBox arrayContainer;
    @FXML private Slider speedSlider;
    @FXML private Label speedLabel;
    @FXML private Button startBtn;
    @FXML private Pane spacer;

    private int[] data;
    private List<StackPane> cellNodes = new ArrayList<>();

    @FXML
    public void initialize() {
        HBox.setHgrow(spacer, Priority.ALWAYS);
        startBtn.setDisable(true);

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
        startBtn.setDisable(false);
    }

    @FXML
    private void handleStart() {
        if (data == null) {
            System.out.println("There is no data");
            return;
        }

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
            case "Insertion Sort" -> {
                sorter = new InsertionSort(cellNodes, arrayContainer, speedSlider, data);
                System.out.println("InsertionSort selected");
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

    @FXML
    private void handleReset() {
        // Clear UI
        arrayContainer.getChildren().clear();
        cellNodes.clear();

        data = null;
        startBtn.setDisable(true);

        System.out.println("Reset complete");
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
}
