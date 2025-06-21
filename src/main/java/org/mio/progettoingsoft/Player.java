package org.mio.progettoingsoft;

import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.*;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.events.AddCreditsEvent;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.utils.Logger;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

public class Player {

    private final String nickname;
    private int credits;
    private HousingColor color;
    private ShipBoard shipBoard;
    private Component inHand;
    private boolean isRunning;

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    /**
     * create a new instance of {@link Player}, it is called by the constructor of {@link FlyBoard}
     * @param nickname : {@link String} chosen by input from the player
     * @param color : {@link HousingColor} : the color assainged by the {@link FlyBoard}
     * @param mode {@link GameMode} the difficulty of the game the {@link Player} has been added
     * @param flyboard : the {@link FlyBoard} he is being added
     */
    public Player(String nickname, HousingColor color, GameMode mode, FlyBoard flyboard) {
        this.nickname = nickname;
        credits = 0;
        this.color = color;

        shipBoard = ShipBoard.createShipBoard(mode, color, flyboard);

        inHand = null;
        this.isRunning = false;

//        view = new View(this);
    }


    /** GETTER */
    public String getNickname() {
        return nickname;
    }

    public ShipBoard getShipBoard() {
        return shipBoard;
    }

    public Integer getCredits() {
        return credits;
    }

    public HousingColor getColor() {
        return this.color;
    }

    public void setHousingColor(HousingColor color){
        this.color = color;
    }


    /**
     * add the amount of credits passed as parameter
     * @param quantity : the amount to add
     */
    public void addCredits(int quantity) {
        credits += quantity;
        Event event = new AddCreditsEvent(nickname, quantity);
        support.firePropertyChange("movePlayer", 0, event);
        Logger.debug("evento movePlayer lanciato");

    }

    /**
     * removes the amount of credits passed as parameter, setting 0 if not amout passed is greater the the available quantity
     * @param quantity : amount of credits to remove
     */
    public void removeCredits(int quantity) {
        credits = Integer.max(0, credits - quantity);
    }


    /**
     *
     * @param other : Object
     * @return whether the object passed is the same {@link Player}, checking the usernames, returs false it the Object passes is not a {@link Player} instance
     */
    public boolean equals(Object other) {
        if(other == null)
            return false;
        if( !(other instanceof Player))
            return false;
        Player tmp = (Player) other;
        return this.nickname.equals(tmp.getNickname());
    }

    /**
     *
     * @return whether the {@link Player} has been collected on the {@link FlyBoard} and he is playing
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**\
     * Indicates whether the {@link Player} has been collected on the {@link FlyBoard} and he is playing
     * @param running : boolean
     */
    public void setRunning(boolean running) {
        this.isRunning = running;
    }

    /**
     * Set the {@link ShipBoard} of the player to the {@link ShipBoard} passed as parameter
     *
     * It is only used when copying an already built {@link ShipBoard} by default, and for this reason does not check anything on it
     * @param shipBoard : the {@link ShipBoard} to set for the {@link Player}
     */
    public void setShipBoard(ShipBoard shipBoard){
        this.shipBoard = shipBoard;
    }
}
