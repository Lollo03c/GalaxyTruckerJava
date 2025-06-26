package org.mio.progettoingsoft;

/**
 * Represents the different types of connectors that can be found on spaceship components.
 * Connectors determine how components can be attached to each other.
 * Each connector type has a string representation and defines rules for compatibility and connection.
 */
public enum Connector {
    SINGLE("single"), DOUBLE("double"), TRIPLE("triple"), FLAT("flat");

    private String type;

    /**
     * Constructs a {@code Connector} enum constant with the specified string type.
     * @param type The string name of the connector type.
     */
    Connector(String type){
        this.type = type;
    }

    /**
     * Returns the string representation of this connector type.
     * @return The string type of the connector.
     */
    public String getType(){
        return this.type;
    }

    /**
     * Converts a string to its corresponding {@code Connector} enum constant.
     * The comparison is case-sensitive. If the string does not match any known connector type, it defaults to {@code Connector.FLAT}.
     *
     * @param t The string to convert (e.g., "single", "double", "triple", "flat").
     * @return The {@code Connector} enum constant.
     */
    public static Connector stringToConnector(String t){
        return switch (t) {
            case "flat" -> Connector.FLAT;
            case "single" -> Connector.SINGLE;
            case "double" -> Connector.DOUBLE;
            case "triple" -> Connector.TRIPLE;
            default -> Connector.FLAT;
        };
    }

    /**
     * Returns the string representation of this connector type.
     * This method provides a more explicit string conversion than `getType()`.
     *
     * @return The lowercase string name of the connector type, or "unknown" if the enum state is unexpected.
     */
    @Override
    public String toString() {
        switch(this){
            case SINGLE: return "single";
            case DOUBLE: return "double";
            case TRIPLE: return "triple";
            case FLAT: return "flat";
            default: return "unknown";
        }
    }

    /**
     * Checks if this connector is compatible with another connector.
     * Compatibility rules:
     * <ul>
     * <li>A TRIPLE connector is compatible with any non-FLAT connector.</li>
     * <li>Any non-FLAT connector is compatible with a TRIPLE connector.</li>
     * <li>Two connectors of the same type are compatible (e.g., SINGLE with SINGLE).</li>
     * </ul>
     *
     * @param other The other {@link Connector} to check compatibility with.
     * @return {@code true} if the connectors are compatible, {@code false} otherwise.
     */
    public Boolean isCompatible(Connector other){
        if ((this.equals(Connector.TRIPLE) && !other.equals(Connector.FLAT)) || (!this.equals(Connector.FLAT) && other.equals(Connector.TRIPLE)))
            return true;

        if (this.equals(other))
            return true;

        return false;
    }

    /**
     * Checks if this connector is "connected" (forms a valid connection) with another connector.
     * Connection rules:
     * <ul>
     * <li>A TRIPLE connector connects with any non-FLAT connector.</li>
     * <li>Any non-FLAT connector connects with a TRIPLE connector.</li>
     * <li>Two connectors of the same type (excluding FLAT) are connected (e.g., SINGLE with SINGLE, but not FLAT with FLAT).</li>
     * </ul>
     * This method is similar to `isCompatible` but explicitly excludes FLAT-to-FLAT connections.
     *
     * @param other The other {@link Connector} to check connection with.
     * @return {@code true} if the connectors form a valid connection, {@code false} otherwise.
     */
    public Boolean isConnected(Connector other){
        if ((this.equals(Connector.TRIPLE) && !other.equals(Connector.FLAT)) || (!this.equals(Connector.FLAT) && other.equals(Connector.TRIPLE)))
            return true;

        if (this.equals(other) && !this.equals(Connector.FLAT))
            return true;

        return false;
    }
}
