package org.mio.progettoingsoft.utils;

import java.io.Serializable;

public record ConnectionInfo(String ip, int socketPort, int rmiPort, String serverName) implements Serializable {

}
