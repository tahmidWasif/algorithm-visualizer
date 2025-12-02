module com.visualizer.algorithmvisualizer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires javafx.media;


    opens com.visualizer.algorithmvisualizer to javafx.fxml;
    exports com.visualizer.algorithmvisualizer;
}