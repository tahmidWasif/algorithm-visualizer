package com.visualizer.algorithmvisualizer.sorting;

import javafx.application.Platform;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class SelectionSort extends Sorting {

    public SelectionSort(List<StackPane> cellNodes, HBox arrayContainer, Slider speedSlider, int[] data) {
        super(cellNodes, arrayContainer, speedSlider, data);
    }

    @Override
    public void sort() {
        new Thread(() -> {
            try {
                int n = data.length;
                int minIndex;

                for (int i = 0; i < n; i++) {
                    minIndex = i;

                    for (int j = i + 1; j < n; j++) {

                        if (data[j] < data[minIndex]) {
                            minIndex = j;
                        }

                        compare(j, minIndex);
                    }

                    swap(i ,minIndex);
                }

                // Success flash
                success();

            } catch (Exception ignored) {}

        }).start();
    }
}
