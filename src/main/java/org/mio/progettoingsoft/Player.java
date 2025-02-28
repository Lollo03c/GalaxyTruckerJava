package org.mio.progettoingsoft;

public class Player {

    private final String username;
    private Integer credits;
    private final ShipBoard shipBoard;
    private Integer discardedComponents;

    public Player(String username){
        this.username = username;
        shipBoard = null;
    }
}
