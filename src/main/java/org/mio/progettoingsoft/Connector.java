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
}
