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

                        int x = j;
                        int y = minIndex;

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

                    int x = i;
                    int y = minIndex;
                    Platform.runLater(() -> highlightSwap(x, y));

                    int temp = data[minIndex];
                    data[minIndex] = data[i];
                    data[i] = temp;

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

                // Success flash
                Platform.runLater(() -> flashSuccess());
                Thread.sleep(250);
                Platform.runLater(() -> resetColor());

            } catch (Exception ignored) {}

        }).start();
    }
}
