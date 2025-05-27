package org.mio.progettoingsoft.utils;

public class ConnectionInfo {
    // COSTANT INFO
    private final int rmiPort = 1099;
    private final int socketPort = 1050;
    private final String serverName = "GalaxyTruckerServer";

    private final String ipHost;

    public ConnectionInfo(String ipHost) {
        this.ipHost = ipHost;
    }

    public int getRmiPort() {
        return rmiPort;
    }

    public int getSocketPort() {
        return socketPort;
    }

    public String getServerName() {
        return serverName;
    }

    public String getIpHost() {
        return ipHost;
    }
}