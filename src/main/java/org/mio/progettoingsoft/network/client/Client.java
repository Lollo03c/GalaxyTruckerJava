package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.network.server.VirtualServer;

public interface Client {
    void connect() throws Exception;
    VirtualServer getServer();
}

