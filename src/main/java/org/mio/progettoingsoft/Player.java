package org.mio.progettoingsoft;

import org.mio.progettoingsoft.exceptions.*;

public class Player {

    private final String username;
    private Integer credits;

    private ShipBoard shipBoard;

    private Integer discardedComponents;

    private Component inHand;

    public Player(String username) {
        this.username = username;
        shipBoard = new ShipBoard();
// bisogna aggiungere i colori della cabina principale: avevamo deciso di rimuoverli dal json dato che
//non sono carte che si possono pescare e posizionare. Mettiamo un attr in ShipBoard?
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

    public void discardComponent(int row, int column) {
//        shipBoard.get(row, column) = null;
        discardedComponents++;
    }

    public void bookComponent() {

    }

    public Integer getCredits() {
        return credits;
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

    public Integer getPower(){
        return 0;
    }

    public Integer getSpeed(){
        return 0;
    }

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

    public Integer getQuantBatteries(){
        return shipBoard.getQuantBatteries();
    }

    public void removeEnergy() throws NotEnoughBatteriesException {
        shipBoard.removeEnergy();
    }

    public void addHumanGuest(int quantity) throws NotEnoughHousingException {
        for (int i = 0; i < quantity; i++)
            shipBoard.addHumanGuest();
    }

    public void addAlien() throws NotEnoughHousingException {

    }
}
