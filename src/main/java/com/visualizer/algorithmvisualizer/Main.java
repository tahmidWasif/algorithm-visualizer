package com.visualizer.algorithmvisualizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Load video
        Media media = new Media(new File("background.mp4").toURI().toString());
        MediaPlayer player = new MediaPlayer(media);
        player.setCycleCount(MediaPlayer.INDEFINITE); // loop forever
        player.setAutoPlay(true);

        MediaView mediaView = new MediaView(player);
        mediaView.setPreserveRatio(false);

        StackPane root = new StackPane();

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
        Parent ui = fxmlLoader.load();

        // Add MediaView first (background), then UI
        root.getChildren().addAll(mediaView, ui);

        Scene scene = new Scene(root, 1280, 720);
        stage.setTitle("Algorithm Visualizer");
        stage.setScene(scene);
        stage.show();

        player.setOnError(() -> System.out.println("Error: " + player.getError()));
        media.setOnError(() -> System.out.println("Media error: " + media.getError()));
    }


    public static void main(String[] args) {
        launch();
    }
}