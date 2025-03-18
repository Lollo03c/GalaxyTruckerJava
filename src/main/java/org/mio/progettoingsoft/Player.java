package org.mio.progettoingsoft;

import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.*;

public class Player {

    private final String username;
    private int credits;
    private HousingColor color;
    private ShipBoard shipBoard;
    private int discardedComponents;
    private Component inHand;

    private final View view;


    public Player(String username, HousingColor color) {
        this.username = username;
        credits = 0;
        this.color = color;
        shipBoard = new ShipBoard(color);
        discardedComponents = 0;
        inHand = null;

        view = new View(this);
    }


    /** GETTER */
    public String getUsername() {
        return username;
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

    public View getView(){
        return view;
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
        shipBoard.addComponentToPosition(comp, row, column);
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
        return this.username.equals(tmp.getUsername());
    }
}
