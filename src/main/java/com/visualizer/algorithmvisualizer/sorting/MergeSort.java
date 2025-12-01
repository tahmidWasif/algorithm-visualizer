package com.visualizer.algorithmvisualizer.sorting;

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MergeSort extends Sorting {

    public MergeSort(List<StackPane> cellNodes, HBox arrayContainer, Slider speedSlider, int[] data) {
        super(cellNodes, arrayContainer, speedSlider, data);
    }

    @Override
    public void sort() {
        Thread t = new Thread(() -> {
           try {

               mergeSortVisual(0, data.length - 1, 1);
               success();

           } catch (Exception ignored) {}
        });
        t.start();
    }

    private void mergeSortVisual(int left, int right, int depth) throws InterruptedException {
        if (left == right) {
            return;
        }

        int mid = (left + right) / 2;

        List<StackPane> leftArr = showSubarray(left, mid, true);
        mergeSortVisual(left, mid, depth + 1);

        List<StackPane> rightArr = showSubarray(mid + 1, right, false);
        mergeSortVisual(mid + 1, right, depth + 1);

        mergeWithAnimation(left, depth, leftArr, rightArr);
    }

    private List<StackPane> showSubarray(int left, int right, boolean isLeft) throws InterruptedException {
        List<StackPane> clones = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        runOnFx(() -> {
            for (int i = left; i <= right; i++) {
                StackPane original = (StackPane) arrayContainer.getChildren().get(i);
                while (original.getChildren().size() >= 3) {
                    original = (StackPane) original.getChildren().get(2);
                }

                StackPane clone = cloneCell(original);
                original.getChildren().add(clone);
                clones.add(clone);

            }

            latch.countDown();
        });

        latch.await();

        animateDivide(clones, isLeft);

        pause();
        return clones;
    }

    private void animateDivide(List<StackPane> clones, boolean isLeft) throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        runOnFx(() -> {
            List<TranslateTransition> ttList = new ArrayList<>();
            for (StackPane clone : clones) {
                TranslateTransition tt = new TranslateTransition(getSwapDuration(), clone);
                tt.setByY(70);        // down

                if (isLeft) {       // left or right
                    tt.setByX(-30);
                } else {
                    tt.setByX(30);
                }

                ttList.add(tt);
            }

            ParallelTransition pt = new ParallelTransition();
            pt.getChildren().addAll(ttList);

            pt.setOnFinished(e -> latch.countDown());

            pt.play();
        });

        latch.await();
    }

    private StackPane setNodeValue(StackPane destination, StackPane source) throws InterruptedException {
        runOnFx(() -> {
            ((Label) destination.getChildren().get(1)).setText(((Label) source.getChildren().get(1)).getText());
            ((StackPane) source.getParent()).getChildren().remove(source);
        });

        return destination;
    }

    private StackPane cloneCell(StackPane original) {
        Rectangle oldRect = (Rectangle) original.getChildren().get(0);
        Label oldLabel = (Label) original.getChildren().get(1);

        Rectangle rect = new Rectangle(oldRect.getWidth(), oldRect.getHeight());
        rect.setFill(oldRect.getFill());
        rect.setStroke(oldRect.getStroke());
        rect.setStrokeWidth(oldRect.getStrokeWidth());

        Label label = new Label(oldLabel.getText());
        label.setStyle(oldLabel.getStyle());

        StackPane sp = new StackPane(rect, label);
        sp.setMinSize(original.getMinWidth(), original.getMinHeight());
        sp.setMaxSize(original.getMaxWidth(), original.getMaxHeight());

        return sp;
    }


    private void mergeWithAnimation(int left, int depth, List<StackPane> L, List<StackPane> R) throws InterruptedException {

        int i = 0, j = 0, k = 0;
        List<Integer> merged = new ArrayList<>();

        while (i < L.size() && j < R.size()) {
            StackPane leftNode = L.get(i);
            StackPane rightNode = R.get(j);

            int leftVal = Integer.parseInt(((Label) leftNode.getChildren().get(1)).getText());
            int rightVal = Integer.parseInt(((Label) rightNode.getChildren().get(1)).getText());

            StackPane parent = (StackPane) arrayContainer.getChildren().get(left + k);
            for (int d = 0; d < depth - 1; d++) {   // go to the parent of the last row
                parent = (StackPane) parent.getChildren().get(2);
            }
            showSelect(parent);

            highlight(leftNode);
            highlight(rightNode);
            pause();

            if (leftVal <= rightVal) {
                unhighlight(rightNode);
                swap(parent, leftNode);
                merged.add(leftVal);
                i++;
                k++;
            } else {
                unhighlight(leftNode);
                swap(parent, rightNode);
                merged.add(rightVal);
                j++;
                k++;
            }

            pause();
        }



        while (i < L.size()) {
            StackPane parent = (StackPane) arrayContainer.getChildren().get(left + k);
            for (int d = 0; d < depth - 1; d++) {   // go to the parent of the last row
                parent = (StackPane) parent.getChildren().get(2);
            }
            showSelect(parent);

            StackPane leftNode = L.get(i);
            swap(parent, leftNode);
            merged.add(Integer.parseInt(((Label) leftNode.getChildren().get(1)).getText()));
            i++;
            k++;
            pause();
        }

        while (j < R.size()) {
            StackPane parent = (StackPane) arrayContainer.getChildren().get(left + k);
            for (int d = 0; d < depth - 1; d++) {   // go to the parent of the last row
                parent = (StackPane) parent.getChildren().get(2);
            }
            showSelect(parent);

            StackPane rightNode = R.get(j);
            swap(parent, rightNode);
            merged.add(Integer.parseInt(((Label) rightNode.getChildren().get(1)).getText()));
            j++;
            k++;
            pause();
        }

        for (int x = 0; x < merged.size(); x++) {
            data[left + x] = merged.get(x);
        }

    }

    private void animatePullUp(StackPane node, StackPane parent) throws InterruptedException {
        runOnFxAndWait(() -> {
            Rectangle nodeR = (Rectangle) node.getChildren().get(0);
            Rectangle parentR = (Rectangle) parent.getChildren().get(0);

            Bounds boundsChild = nodeR.localToScene(nodeR.getBoundsInLocal());
            Bounds boundsParent = parentR.localToScene(parentR.getBoundsInLocal());

            double distanceX = boundsChild.getMinX() - boundsParent.getMinX();
            double distanceY = boundsChild.getMinY() - boundsParent.getMinY();

            TranslateTransition tt = new TranslateTransition(getSwapDuration(), node);

            tt.setByY(-distanceY);
            tt.setByX(-distanceX);

            tt.play();
        });

        pause();
        pause();
    }

    private void swap(StackPane parent, StackPane child) throws InterruptedException {
        Rectangle r1 = (Rectangle) child.getChildren().get(0);
        Rectangle r2 = (Rectangle) parent.getChildren().get(0);

        r1.setFill(Color.ORANGE);
        r2.setFill(Color.ORANGE);
        r1.setStroke(Color.RED);
        r2.setStroke(Color.RED);

        animatePullUp(child, parent);
        setNodeValue(parent, child);

        unhighlight(child);
        unhighlight(parent);
    }

    private void showSelect(StackPane node) throws InterruptedException {
        Rectangle r = (Rectangle) node.getChildren().get(0);
        r.setFill(Color.LIGHTCYAN);
        r.setStroke(Color.BLACK);
        pause();
    }

    // FX helper methods
    private void runOnFx(Runnable r) throws InterruptedException {
        Platform.runLater(r);
    }

    private void runOnFxAndWait(Runnable r) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                r.run();
            } finally {
                latch.countDown();
            }
        });

        latch.await();
    }
}
