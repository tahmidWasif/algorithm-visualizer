package com.visualizer.algorithmvisualizer.sorting;

import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.List;

public class InsertionSort extends Sorting {

    public InsertionSort(List<StackPane> cellNodes, HBox arrayContainer, Slider speedSlider, int[] data) {
        super(cellNodes, arrayContainer, speedSlider, data);
    }

    @Override
    public void sort() {
        new Thread(() -> {
            try {
                int k, key, n = data.length;
                for (int i = 1; i < n; i++) {
                    key = data[i];
                    k = i - 1;
                    while (k >= 0 && data[k] > key) {
                        compare(k, k + 1);
                        swap(k, k + 1);
                        k--;
                    }
                    if (k >= 0)
                        compare(k, k + 1);
                }

                // Success flash
                success();
            } catch(Exception ignored) {}
        }).start();

    }
}
