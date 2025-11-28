package com.visualizer.algorithmvisualizer.sorting;

import javafx.application.Platform;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class BubbleSort extends Sorting {

    public BubbleSort(List<StackPane> nodes, HBox container, Slider speedSlider, int[] data) {
        super(nodes, container, speedSlider, data);
    }

    @Override
    public void sort() {
        new Thread(() -> {
            try {
                for (int i = 0; i < data.length - 1; i++) {
                    for (int j = 0; j < data.length - i - 1; j++) {

                        compare(j, j + 1);

                        if (data[j] > data[j + 1]) {

                            swap(j, j + 1);
                        }
                    }
                }

                // Success flash
                success();

            } catch (Exception ignored) {}
        }).start();
    }


}
