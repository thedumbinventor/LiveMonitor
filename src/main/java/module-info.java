module com.example.livemonitor {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fazecast.jSerialComm;
//    requires commons.math3;


    opens com.example.livemonitor to javafx.fxml;
    exports com.example.livemonitor;
}