package org.mio.progettoingsoft.views;

import org.mio.progettoingsoft.network.ConnectionType;

public interface VirtualView {

    ConnectionType askConnectionType();
    String askNickname();

}
