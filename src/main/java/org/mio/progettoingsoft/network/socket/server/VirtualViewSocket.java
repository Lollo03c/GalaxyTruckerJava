package org.mio.progettoingsoft.network.socket.server;

import org.mio.progettoingsoft.network.VirtualView;

/**
 * Questa interfaccia specializza l'interfaccia VirtualView per la tecnologia Socket
 */
public interface VirtualViewSocket extends VirtualView {
    @Override
    public void showUpdate(Integer number);
    @Override
    public void reportError(String details);
}
