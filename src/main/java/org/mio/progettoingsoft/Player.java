package org.mio.progettoingsoft;

import org.mio.progettoingsoft.components.GoodType;

import java.util.Map;

public class Player {

    private final String username;
    private Integer credits;
    private final ShipBoard shipBoard;
    private Integer discardedComponents;
    private Map<GoodType, Integer> goods;

    private Component inHand;

    public Player(String username) {
        this.username = username;
        shipBoard = null;
    }

    public String getUsername() {
        return username;
    }

    public ShipBoard getShipBoard() {
        return shipBoard;
    }

    public void takeCoveredComponent(Component component) {

    }

    public void refuseComponent(Component component) { //when not added it joins the heap as uncovered
    }

    public void discardComponent(Component comp) {

    }

    public void bookComponent() {

    }

    public Integer getCredits() {
        return credits;
    }

    public void addCredits(int quantity) {
        credits += quantity;
    }

    public Integer getGoods(GoodType type) {
        return goods.get(type);
    }

    public void addGoods(GoodType type, Integer quantity) {

    }

    public void removeGoods(GoodType type, Integer quantity) {

    }

    public Integer getPower(){
        return 0;
    }

    public Integer getSpeed(){
        return 0;
    }

    public void rotateHourGlass(){

    }
}
