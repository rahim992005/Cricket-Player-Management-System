module com.example.kheladhula {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.kheladhula to javafx.fxml;
    exports com.example.kheladhula;
}