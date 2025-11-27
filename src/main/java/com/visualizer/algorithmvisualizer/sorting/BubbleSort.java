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
