module org.mio.progettoingsoft {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.desktop;

    opens org.mio.progettoingsoft to javafx.fxml;
    exports org.mio.progettoingsoft;
}