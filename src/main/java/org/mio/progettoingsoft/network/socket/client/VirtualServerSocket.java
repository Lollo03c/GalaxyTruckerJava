package org.mio.progettoingsoft.network.socket.client;

import org.mio.progettoingsoft.network.VirtualServer;

/**
 * Questa interfaccia specializza l'interfaccia VirtualServer per la tecnologia Socket
 */
public interface VirtualServerSocket extends VirtualServer {
    public void add(Integer number);

    public void reset();
}
