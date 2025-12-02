package com.visualizer.algorithmvisualizer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private void switchToSorting(ActionEvent e) throws IOException {
        Parent newRoot = FXMLLoader.load(getClass().getResource("sorting-view.fxml"));
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();

        Scene scene = new Scene(newRoot, 1280, 720);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void switchToGraph(ActionEvent e) throws IOException {
        Parent newRoot = FXMLLoader.load(getClass().getResource("graph-view.fxml"));
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();

        Scene scene = new Scene(newRoot, 1280, 720);
        stage.setScene(scene);
        stage.show();
    }
}
