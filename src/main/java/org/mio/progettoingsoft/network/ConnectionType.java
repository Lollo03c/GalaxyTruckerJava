package org.mio.progettoingsoft.network;

public class ConnectionType {

    private final boolean isRmi;
    private final String host;
    private final int port;
    private final String serverName;

    public ConnectionType(boolean isRmi, String host, int port, String serverName) {
        this.isRmi = isRmi;
        this.host = host;
        this.port = port;
        this.serverName = serverName;
    }

    public boolean isRmi() {
        return isRmi;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getServerName() {
        return serverName;
    }
}
