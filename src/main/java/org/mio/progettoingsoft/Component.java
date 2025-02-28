package org.mio.progettoingsoft;

public abstract class Component {
    private final Connector topConnector, bottomConnector, rightConnector, leftConnector;
    private final ComponentType type;

    protected Component(ComponentType type, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        topConnector = topConn;
        bottomConnector = bottomConn;
        rightConnector = rightConn;
        leftConnector = leftConn;

        this.type = type;
    }

    public ComponentType getType(){
        return type;
    }

    public Boolean isExposedSide(Direction direction){
        Connector comp = switch (direction) {
            case Direction.BACK -> bottomConnector;
            case Direction.FRONT -> topConnector;
            case Direction.RIGHT -> rightConnector;
            case Direction.LEFT -> leftConnector;
        };

        return !comp.equals(Connector.FLAT);
    }

    public void position(Direction direzione){
        // da ruotare i connettori
    }




}