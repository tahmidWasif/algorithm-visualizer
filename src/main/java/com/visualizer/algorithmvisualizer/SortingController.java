package com.visualizer.algorithmvisualizer;

import com.visualizer.algorithmvisualizer.sorting.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SortingController {

    @FXML private ComboBox<String> algorithmChoice;
    @FXML private HBox arrayContainer;
    @FXML private Slider speedSlider;
    @FXML private Label speedLabel;
    @FXML private Button generateBtn;
    @FXML private Button startBtn;
    @FXML private Button resetBtn;
    @FXML private Label heading;
    @FXML private VBox levelsContainer;
    @FXML private Slider arraySize;
    @FXML private Label sizeLabel;
    @FXML private Label description;
    @FXML private Label timeComplexity;

    private int[] data;
    private List<StackPane> cellNodes = new ArrayList<>();

    String bubbleSortText = "Bubble sort is a comparison-based sorting algorithm that repeatedly steps through the list, compares each pair of adjacent elements, and swaps them if they are in the wrong order. This process continues until no more swaps are needed, meaning the list is sorted.";
    String insertionSortText = "Insertion sort is a sorting algorithm that builds the final sorted list one element at a time by taking each element and inserting it into its correct position among the previously sorted elements.";
    String selectionSortText = "Selection sort is a sorting algorithm that repeatedly selects the smallest element from the unsorted portion of the list and swaps it with the first unsorted element, gradually expanding the sorted portion from left to right.";
    String mergeSortText = "Merge sort is a divide-and-conquer sorting algorithm that recursively splits the list into smaller halves, sorts each half, and then merges the sorted halves back together to produce a fully sorted list.";

    String bubbleTime = "   Best Case: O(n²)    Worst Case: O(n²) Average Case: O(n²)";
    String insertionTime = "   Best Case: O(n)    Worst Case: O(n²) Average Case: O(n²)";
    String selectionTime = "   Best Case: O(n²)    Worst Case: O(n²) Average Case: O(n²)";
    String mergeTime = "Best Case: O(nlogn) Worst Case: O(nlogn) Average Case: O(nlogn)";

    @FXML
    public void initialize() {
        startBtn.setDisable(true);

        // set default algorithm
        algorithmChoice.getSelectionModel().selectFirst();

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            speedLabel.setText(String.format("%.1fx", newVal.doubleValue()));
        });

        // array size slider
        arraySize.valueProperty().addListener((obs, oldVal, newVal) -> {
            sizeLabel.setText(String.format("%d", newVal.intValue()));
        });

        // reset button

        resetBtn.setOnMousePressed(e -> {
            resetBtn.setStyle("-fx-background-color: red; -fx-text-fill: white");
        });

        resetBtn.setOnMouseReleased(e -> {
            resetBtn.setStyle("-fx-background-color: #2b2d30; -fx-text-fill: white;");
        });

        // start button
        startBtn.setOnMousePressed(e -> {
            startBtn.setStyle("-fx-background-color: green; -fx-text-fill: white");
        });

        startBtn.setOnMouseReleased(e -> {
            startBtn.setStyle("-fx-background-color: #2b2d30; -fx-text-fill: white;");
        });

        // description
        description.setText(bubbleSortText);
        timeComplexity.setText(bubbleTime);

        algorithmChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            heading.setText(newVal);

            switch (newVal) {
                case "Bubble Sort" -> {
                    description.setText(bubbleSortText);
                    timeComplexity.setText(bubbleTime);
                }
                case "Insertion Sort" -> {
                    description.setText(insertionSortText);
                    timeComplexity.setText(insertionTime);
                }
                case "Selection Sort" -> {
                    description.setText(selectionSortText);
                    timeComplexity.setText(selectionTime);
                }
                case "Merge Sort" -> {
                    description.setText(mergeSortText);
                    timeComplexity.setText(mergeTime);
                }
            }
        });
    }

    @FXML
    private void switchToMain(ActionEvent e) throws IOException {
        // Load video
        Media media = new Media(new File("background.mp4").toURI().toString());
        MediaPlayer player = new MediaPlayer(media);
        player.setCycleCount(MediaPlayer.INDEFINITE); // loop forever
        player.setAutoPlay(true);

        MediaView mediaView = new MediaView(player);
        mediaView.setPreserveRatio(false);

        StackPane root = new StackPane();

        Parent newRoot = FXMLLoader.load(getClass().getResource("main-view.fxml"));
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();

        root.getChildren().addAll(mediaView, newRoot);

        Scene scene = new Scene(root, 1280, 720);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleGenerate() {
        data = generateRandomArray((int) arraySize.getValue());
        displayArray();
        startBtn.setDisable(false);
    }

    @FXML
    private void handleStart() {
        if (data == null) {
            System.out.println("There is no data");
            return;
        }

        algorithmChoice.setDisable(true);
        generateBtn.setDisable(true);
        startBtn.setDisable(true);

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
            case "Merge Sort" -> {
                sorter = new MergeSort(cellNodes, arrayContainer, speedSlider, data);
                System.out.println("MergeSort selected");
            }
            default -> System.out.println("Algorithm not implemented");
        }
        try {
            System.out.println("Executing sort");
            sorter.sort();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void handleReset() {

        // Clear UI
        arrayContainer.getChildren().clear();
        cellNodes.clear();

        data = null;

        algorithmChoice.setDisable(false);
        generateBtn.setDisable(false);
        startBtn.setDisable(true);
        startBtn.setStyle("-fx-background-color: #2b2d30; -fx-text-fill: white;");

        // reset speed slider
        speedSlider.adjustValue(1);

        // reset array size slider
        arraySize.adjustValue(10);

        // remove any extra rows created by merge sort
        while (levelsContainer.getChildren().size() > 2) {
            levelsContainer.getChildren().remove(2);
        }

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
        rect.setStyle("-fx-fill: lightgrey; -fx-stroke: black; -fx-stroke-width: 2;");

        rect.setUserData("cellRect");   // Tag

        Label label = new Label(String.valueOf(value));
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        StackPane stack = new StackPane(rect, label);
        return stack;
    }
}