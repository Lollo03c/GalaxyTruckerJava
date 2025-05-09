module org.mio.progettoingsoft {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires java.smartcardio;
    requires java.rmi;
    requires org.slf4j;

    opens org.mio.progettoingsoft to javafx.fxml;
    exports org.mio.progettoingsoft;

    exports org.mio.progettoingsoft.network;
    exports org.mio.progettoingsoft.network.client;
    exports org.mio.progettoingsoft.network.client.rmi;
    exports org.mio.progettoingsoft.network.server;
    exports org.mio.progettoingsoft.network.server.rmi;
}