package org.mio.progettoingsoft.utils;

/**
 * Stores constant connection information for the Galaxy Trucker game server,
 * including RMI port, socket port, server name, and the IP address of the host.
 */
public class ConnectionInfo {
    // CONSTANT INFO
    private final int rmiPort = 1099;
    private final int socketPort = 1050;
    private final String serverName = "GalaxyTruckerServer";
    private final String ipHost;

    /**
     * Constructs a new {@code ConnectionInfo} object with the specified IP address.
     *
     * @param ipHost The IP address or hostname of the server.
     */
    public ConnectionInfo(String ipHost) {
        this.ipHost = ipHost;
    }

    /**
     * Returns the RMI (Remote Method Invocation) port number.
     *
     * @return The RMI port number.
     */
    public int getRmiPort() {
        return rmiPort;
    }

    /**
     * Returns the socket port number.
     *
     * @return The socket port number.
     */
    public int getSocketPort() {
        return socketPort;
    }

    /**
     * Returns the name of the server, used for RMI lookup.
     *
     * @return The server name.
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Returns the IP address or hostname of the server.
     *
     * @return The IP address or hostname.
     */
    public String getIpHost() {
        return ipHost;
    }
}