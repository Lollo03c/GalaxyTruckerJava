package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.utils.ConnectionInfo;
import org.mio.progettoingsoft.views.gui.Gui;
import org.mio.progettoingsoft.views.tui.Tui;
import org.mio.progettoingsoft.views.View;

/**
 * The main application class for the client, responsible for initializing the
 * client-side components and launching the user interface.
 * It sets up the connection to the server and determines whether to use a GUI or TUI.
 */
public class ClientApp implements Runnable{
    private final View view;

    /**
     * Constructs a new ClientApp.
     * This constructor first initializes the {@link ClientController} with the provided
     * connection information. Then, it instantiates either a {@link Gui} or a {@link Tui}
     * based on the {@code isGui} flag, setting up the client's user interface.
     * @param isGui A boolean flag; true to use a Graphical User Interface (GUI), false for a Text User Interface (TUI).
     * @param connectionInfo The {@link ConnectionInfo} object containing the IP address and port details for the server connection.
     */
    public ClientApp(boolean isGui, ConnectionInfo connectionInfo) {
        ClientController.create(connectionInfo);
        view = isGui ? new Gui() : new Tui();
    }

    /**
     * Starts the client application by running its user interface.
     */
    @Override
    public void run(){
        view.run();
    }
}