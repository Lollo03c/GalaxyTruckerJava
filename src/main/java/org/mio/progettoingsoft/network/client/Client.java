package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.network.server.VirtualServer;

/**
 * Defines the essential contract for any client implementation in the game.
 * This interface abstracts the underlying communication mechanism (e.g., RMI, Sockets),
 * ensuring that all clients provide core functionalities for connecting to the server
 * and obtaining a reference to interact with server-side logic.
 */
public interface Client {
    void connect() throws Exception;
    VirtualServer getServer();
}