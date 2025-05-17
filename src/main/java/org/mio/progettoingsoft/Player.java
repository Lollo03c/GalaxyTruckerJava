package org.mio.progettoingsoft;

import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.*;
import org.mio.progettoingsoft.model.enums.GameMode;

import java.util.List;

public class Player {

    private final String nickname;
    private int credits;
    private HousingColor color;
    private ShipBoard shipBoard;
    private int discardedComponents;
    private Component inHand;
    private boolean isRunning;
//    private final VirtualView view;
    private List<GoodType> tmpGoods;


    public Player(String nickname, HousingColor color, GameMode mode, FlyBoard flyboard) {
        this.nickname = nickname;
        credits = 0;
        this.color = color;

        shipBoard = ShipBoard.createShipBoard(mode, color, flyboard);

        discardedComponents = 0;
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

//    public View getView(){
//        return view;
//    }

    public void setHousingColor(HousingColor color){
        this.color = color;
    }

    public Component getInHand() {
        return inHand;
    }

    public void setInHand(Component inHand) {
        this.inHand = inHand;
    }

    public Component refuseComponentInHand() {
        if( inHand == null)
            throw new NoComponentInHandException("No component in hand!");

        Component temp = inHand;
        inHand = null;
        return temp;
    }

    public void bookComponentInHand() throws NotEnoughSpaceForBookedComponentException {
        if( inHand == null)
            throw new NoComponentInHandException("No component in hand!");
        shipBoard.addBookedComponent(inHand);
        inHand = null;
    }



    public void addDiscardComponent(int quantity) {
        discardedComponents += quantity;
    }



    public void addCredits(int quantity) {
        credits += quantity;
    }

    public void giveGoods(List<GoodType> goods) {
        this.tmpGoods = goods;
    }

    public void removeCredits(int quantity) {
        credits = Integer.max(0, credits - quantity);
    }

//    public Integer getGoodsQuantiy(GoodType type) {
//        return shipBoard.getStoredQuantityGoods(type);
//    }

//    public void addGoods(GoodType type, Integer quantity) throws FullGoodDepot {
//        for (int i = 0; i < quantity; i++)
//            shipBoard.addGood(type);
//    }

//    public void removeGoods(GoodType type, Integer quantity) throws NotEnoughGoods {
//        for (int i = 0; i < quantity; i++)
//            shipBoard.removeGood(type);
//    }

    public void rotateHourGlass(){

    }

    //rotazione orario
    public void addCompoment(Component comp, int row, int column, int rotations) throws IncorrectPlacementException {
// DOMANDA : Dove il giocatore sceglie quante volte girare il componente?
//        (Anto) credo sia compito del controller
        for (int i = 0; i < rotations; i++){
            comp.rotateClockwise();
        }
//secondo me potremmo mettere il metodo void e non fare il controllo con l'if
//        shipBoard.addComponentToPosition(comp, row, column);
//            System.out.println("Occupied cell");

    }

//    public Integer getQuantBatteries(){
//        return shipBoard.getQuantBatteries();
//    }
//
//    public void removeEnergy() throws NotEnoughBatteriesException {
//        shipBoard.removeEnergy();
//    }
//
//    public void addHumanGuest(int quantity) throws NotEnoughHousingException {
//        for (int i = 0; i < quantity; i++)
//            shipBoard.addHumanGuest();
//    }
//
//    public void addAlien() throws NotEnoughHousingException {
//
//    }

    public boolean equals(Object other) {
        if(other == null)
            return false;
        if( !(other instanceof Player))
            return false;
        Player tmp = (Player) other;
        return this.nickname.equals(tmp.getNickname());
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        this.isRunning = running;
    }
}
