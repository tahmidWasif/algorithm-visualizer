module com.visualizer.algorithmvisualizer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.visualizer.algorithmvisualizer to javafx.fxml;
    exports com.visualizer.algorithmvisualizer;
}