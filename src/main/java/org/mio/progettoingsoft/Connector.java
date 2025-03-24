package org.mio.progettoingsoft;

public enum Connector {
    SINGLE("single"), DOUBLE("double"), TRIPLE("triple"), FLAT("flat");

    private String type;

    Connector(String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }

    public static Connector stringToConnector(String t){
        return switch (t) {
            case "flat" -> Connector.FLAT;
            case "single" -> Connector.SINGLE;
            case "double" -> Connector.DOUBLE;
            case "triple" -> Connector.TRIPLE;
            default -> Connector.FLAT;
        };
    }

    public Boolean isCompatible(Connector other){
        // Modified by Stefano: two flat are compatible, but the components have to be connected to another one (checked in other methods)
        if (this.equals(Connector.FLAT) && other.equals(Connector.FLAT))
            return true;

        if (this.equals(Connector.FLAT) || other.equals(Connector.FLAT))
            return false;

        if (this.equals(Connector.TRIPLE) || other.equals(Connector.TRIPLE))
            return true;

        return this.equals(other);
    }

    public Boolean isConnected(Connector other){
        if (this.equals(Connector.FLAT) || other.equals(Connector.FLAT))
            return false;

        if (this.equals(Connector.TRIPLE) || other.equals(Connector.TRIPLE))
            return true;

        return this.equals(other);
    }
}
