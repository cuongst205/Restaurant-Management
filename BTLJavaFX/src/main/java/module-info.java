module com.example.btljavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens com.example.btljavafx.app to javafx.graphics, javafx.fxml;
    opens com.example.btljavafx.controller to javafx.fxml;
    exports com.example.btljavafx.app;
}