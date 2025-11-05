module com.example.btljavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop; // For javax.imageio and java.awt.image used by GetVietQR

    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
//    requires com.example.btljavafx;

    opens com.example.btljavafx.app to javafx.graphics, javafx.fxml;
    opens com.example.btljavafx.controller to javafx.fxml;
    exports com.example.btljavafx.app;
}