package org.mio.progettoingsoft;

import org.mio.progettoingsoft.components.AlienType;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class Component {
    private final int id;
    private Connector topConnector, bottomConnector, rightConnector, leftConnector;
    private final ComponentType type;
    private int row;
    private int column;

    public Component(int id, ComponentType type, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        this.id = id;
        topConnector = topConn;
        bottomConnector = bottomConn;
        rightConnector = rightConn;
        leftConnector = leftConn;
        this.type = type;
        this.row = -1;
        this.column = -1;
    }


    public int getRow() {
        return row;
    }
    public int getColumn() {
        return column;
    }
    public void setRow(int row){
        this.row = row;
    }
    public void setColumn(int column){
        this.column = column;
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

    /**
     * rotates the component, changes its {@link Connector} and the direction base of the component type
     * @param nRotationsClockWise
     */
    public void rotate(int nRotationsClockWise){
        for (int i = 0; i < nRotationsClockWise; i++)
            this.rotateClockwise();
    }

    // the two "rotate" methods move the components in the selected order (clockwise or no)
    // then it should change the "pointing direction" of the component
    protected void rotateClockwise(){
        Connector tmp = this.topConnector;
        this.topConnector = this.leftConnector;
        this.leftConnector = this.bottomConnector;
        this.bottomConnector = this.rightConnector;
        this.rightConnector = tmp;
    }

    protected void rotateCounterClockwise(){
        Connector tmp = this.topConnector;
        this.topConnector = this.rightConnector;
        this.rightConnector = this.bottomConnector;
        this.bottomConnector = this.leftConnector;
        this.leftConnector = tmp;
    }

    public Integer getEnergyQuantity(){
        return 0;
    }

    /**
     * remove one energy from the {@link org.mio.progettoingsoft.components.EnergyDepot}
     * MUST BE check first if getEnergyQuanity() > 0
     */
    public void removeOneEnergy(){

    }

    /**
     * add the {@link GoodType} given to the {@link org.mio.progettoingsoft.components.Depot}
     * @param type the {@link GoodType} to add
     * @return
     * @throws IncorrectShipBoardException throws the exception if the {@link org.mio.progettoingsoft.components.Depot} is already full
     *      or trying to add {@link org.mio.progettoingsoft.components.Depot RED} in not a hazrd {@link org.mio.progettoingsoft.components.Depot}
     */
    public void addGood(GoodType type) throws IncorrectShipBoardException {
        throw new IncorrectShipBoardException("not a depot");
    }

    /**
     *
     * @param type the {@link GoodType} to remove
     * @throws IncorrectShipBoardException if the component is not a {@link org.mio.progettoingsoft.components.Depot} or
     *  the depot does not contain the giver {@link GoodType}
     */
    public void removeGood(GoodType type) throws IncorrectShipBoardException{
        throw new IncorrectShipBoardException("not a depot");
    }

    /**
     *
     * @return the list of {@link GoodType} contained in the {@link org.mio.progettoingsoft.components.Depot}, an empty list if the component is not a {@link org.mio.progettoingsoft.components.Depot}
     */
    public List<GoodType> getStoredGoods(){
        return Collections.emptyList();
    }

    public void addAlienType(AlienType color){
        return ;
    }

    public AlienType getColorAlien(){
        return AlienType.NOALIEAN;
    }
     
    public Boolean isCompatible(Component other, Direction direction){
        return switch (direction){
            case FRONT -> topConnector.isCompatible(other.bottomConnector);
            case BACK -> bottomConnector.isCompatible(other.topConnector);
            case RIGHT -> rightConnector.isCompatible(other.leftConnector);
            case LEFT -> leftConnector.isCompatible(other.rightConnector);
        };
    }

    public Boolean isConnected(Component other, Direction direction){
        return switch (direction){
            case FRONT -> topConnector.isConnected(other.bottomConnector);
            case BACK -> bottomConnector.isConnected(other.topConnector);
            case RIGHT -> rightConnector.isConnected(other.leftConnector);
            case LEFT -> leftConnector.isConnected(other.rightConnector);
        };
    }

    public Boolean addHumanMember(){
        return false;
    }

//    public Boolean removeHumanMember(){
//        return false;
//    }

    public Integer getNumHumanMembers(){
        return 0;
    }

    public Boolean addAlien(AlienType type){
        return false;
    }

//    public Boolean removeAlien(AlienType type){
//        return false;
//    }

    public Boolean containsAlien(AlienType type) {
        return false;
    }

    public float getFirePower(){
        return 0f;
    }

    public int getEnginePower(){
        return 0;
    }

    public Direction getDirection(){
        return null;
    }

    public List<Direction> getShieldDirections(){
        return null;
    }

    public Connector getConnector(Direction dir){
        return switch (dir){
            case FRONT -> topConnector;
            case RIGHT -> rightConnector;
            case BACK -> bottomConnector;
            case LEFT -> leftConnector;
        };
    }

    public Boolean canContainsAlien(AlienType type){
        return false;
    }

    public Boolean canContainsGood(GoodType type){
        return false;
    }

    public Boolean canContainsHumanGuest(){
        return false;
    }

    public Boolean containsGuest(){
        return false;
    }


    public Boolean isFirstHousing(){return false;}

    public int getQuantityGuests(){
        return 0;
    }

    public boolean removeGuest(){
        return false;
    }

    public int getId() {
        return id;
    }

    public String toString(){
        return this.type +
                " Top: " + topConnector.toString() +
                " Bottom: " + bottomConnector.toString() +
                " Right: " + rightConnector.toString() +
                " Left: " + leftConnector.toString() +
                " Row: " + row + " Column: " + column;
    }

    public void setGoodsDepot(Map<GoodType, Integer> goods){
        return ;
    }

    public void setGoodsDepot(GoodType type, int quantity){
        return ;
    }
}