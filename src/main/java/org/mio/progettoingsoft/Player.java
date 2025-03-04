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

    public String getUsername(){
        return username;
    }

    public ShipBoard getShipBoard(){
        return shipBoard;
    }

    public void takeCoveredComponent(Component component){

    }

    public void refuseComponent(Component component){ //when not added it joins the heap as uncovered
    }

    public void discardComponent(Component comp){

    }

    public void bookComponent(){

    }
}
