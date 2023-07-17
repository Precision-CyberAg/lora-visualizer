module com.example.loravisualizer {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;


    opens com.example.loravisualizer to javafx.fxml;
    exports com.example.loravisualizer;
    exports com.example.loravisualizer.model;
}