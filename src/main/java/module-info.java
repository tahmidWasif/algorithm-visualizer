module com.visualizer.algorithmvisualizer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.visualizer.algorithmvisualizer to javafx.fxml;
    exports com.visualizer.algorithmvisualizer;
}