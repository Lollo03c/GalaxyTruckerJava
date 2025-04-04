module org.mio.progettoingsoft {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires java.smartcardio;

    opens org.mio.progettoingsoft to javafx.fxml;
    exports org.mio.progettoingsoft;
}