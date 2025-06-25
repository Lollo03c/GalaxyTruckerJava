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

    public Boolean isCompatible(Connector other){
        if ((this.equals(Connector.TRIPLE) && !other.equals(Connector.FLAT)) || (!this.equals(Connector.FLAT) && other.equals(Connector.TRIPLE)))
            return true;

        if (this.equals(other))
            return true;

        return false;
    }

    public Boolean isConnected(Connector other){
        if ((this.equals(Connector.TRIPLE) && !other.equals(Connector.FLAT)) || (!this.equals(Connector.FLAT) && other.equals(Connector.TRIPLE)))
            return true;

        if (this.equals(other) && !this.equals(Connector.FLAT))
            return true;

        return false;
    }
}
