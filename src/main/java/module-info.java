module org.example.fxfontfallbackdemo {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.fxfontfallbackdemo to javafx.fxml;
    exports org.example.fxfontfallbackdemo;
}