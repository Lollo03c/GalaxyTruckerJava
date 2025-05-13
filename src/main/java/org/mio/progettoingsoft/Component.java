package org.mio.progettoingsoft;

import org.mio.progettoingsoft.components.GuestType;
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
    public Boolean getHazard() {
        return false;
    }
    public Boolean getBig() {
        return false;
    }
    public boolean getTriple(){        return false;    }
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
     * @throws if the component is not a {@link org.mio.progettoingsoft.components.EnergyDepot} or the energy depot is emptyw
     */
    public void removeOneEnergy() throws IncorrectShipBoardException{
        throw new IncorrectShipBoardException("not an energy depot");
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


    /**
     * add the given {@link GuestType} to the allowed guest of the housing
     * @param type the {@link GuestType} to add
     * @throws IncorrectShipBoardException if the component is not a {@link org.mio.progettoingsoft.components.Housing}
     */
    public void addAllowedGuest(GuestType type) throws IncorrectShipBoardException {
        throw new IncorrectShipBoardException("not a housing");
    }

    /**
     * add the given {@link GuestType} to the {@link org.mio.progettoingsoft.components.Housing}
     * @param guestType the {@link GuestType} to add
     * @throws IncorrectShipBoardException if the guestType cannot be added to the housing or the comoponent is not a {@link org.mio.progettoingsoft.components.Housing}
     */
    public void addGuest(GuestType guestType) throws IncorrectShipBoardException{
        throw new IncorrectShipBoardException("not a housing");
    }


    /**
     *
     * @param type the {@link GuestType} to add
     * @return true if the given guestType can be added to the housing, false otherwise
     * @throws IncorrectShipBoardException if the component is not a {@link org.mio.progettoingsoft.components.Housing}
     */
    public boolean canAddGuest(GuestType type) throws IncorrectShipBoardException{
        throw new IncorrectShipBoardException("not a housing");
    }
    /**
     *
     * @return the {@link GuestType} of the {@link org.mio.progettoingsoft.components.AlienHousing}
     * @throws IncorrectShipBoardException if the component is not an {@link org.mio.progettoingsoft.components.AlienHousing}
     */
    public GuestType getColorAlien() throws IncorrectShipBoardException{
        throw new IncorrectShipBoardException("not a alien housing");
    }

    /**
     *
     * @return the list of {@link GuestType} hosted in the {@link org.mio.progettoingsoft.components.Housing},
     *  an empty list if the component is not a {@link org.mio.progettoingsoft.components.Housing}
     *
     */
    public List<GuestType> getGuests() {
        return Collections.emptyList();
    }

    /**
     * remove the {@link GuestType} given from the housing, throws the exception if the operation cannot be done
     * @param type {@link GuestType} to remove
     * @throws IncorrectShipBoardException if the component is not an {@link org.mio.progettoingsoft.components.Housing}
     *  or the given {@link GuestType} is not contained as a guest
     */
    public void removeGuest(GuestType type) throws IncorrectShipBoardException{
        throw new IncorrectShipBoardException("not a housing");
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

    public int getEnginePower(boolean activated){
        return 0;
    }

    public float getFirePower(){
        return 0f;
    }


    public float getFirePower(boolean actived){
        return 0f;
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

    /**
     *
     * @param type the {@link GoodType} to be checked
     * @return whether the given {@link GoodType} can be added in the {@link  org.mio.progettoingsoft.components.Depot}
     * @throws IncorrectShipBoardException if the component is not a {@link org.mio.progettoingsoft.components.Depot}
     */
    public boolean canContainsGood(GoodType type) throws IncorrectShipBoardException{
        throw new IncorrectShipBoardException("not a depot");
    }


    /**
     *
     * @return the id of the component, it refers to the id given to the JSON file
     */
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

}