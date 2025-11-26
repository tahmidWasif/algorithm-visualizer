module com.visualizer.algorithmvisualizer {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.visualizer.algorithmvisualizer to javafx.fxml;
    exports com.visualizer.algorithmvisualizer;
}