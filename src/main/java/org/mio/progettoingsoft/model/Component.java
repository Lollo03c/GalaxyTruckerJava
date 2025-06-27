package org.mio.progettoingsoft.model;

import org.mio.progettoingsoft.model.components.*;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;
import org.mio.progettoingsoft.model.enums.ComponentType;
import org.mio.progettoingsoft.model.enums.Connector;
import org.mio.progettoingsoft.model.enums.Direction;
import org.mio.progettoingsoft.utils.Logger;

import java.util.Collections;
import java.util.List;

public abstract class Component {
    private final int id;
    private Connector topConnector, bottomConnector, rightConnector, leftConnector;
    private final ComponentType type;
    private int row;
    private int column;

    private int rotations = 0;

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
    public Cordinate getCordinate(){
        return new Cordinate(row,column);
    }
    public ComponentType getType(){
        return type;
    }
    public Integer getEnergyQuantity(){
        return 0;
    }


    /**
     * rotates the component, changes its {@link Connector} and the direction base of the component type
     * @param nRotationsClockWise
     */
    public void rotate(int nRotationsClockWise){
        for (int i = 0; i < nRotationsClockWise; i++)
            this.rotateClockwise();
    }

    /**
     * Rotates the component 90 degrees clockwise by shifting its connectors.
     * Updates the pointing direction accordingly and increments the rotation count.
     */
    protected void rotateClockwise(){
        Connector tmp = this.topConnector;
        this.topConnector = this.leftConnector;
        this.leftConnector = this.bottomConnector;
        this.bottomConnector = this.rightConnector;
        this.rightConnector = tmp;

        rotations++;
    }

    /**
     * remove one energy from the {@link EnergyDepot}
     * @throws {@link IncorrectShipBoardException} if the component is not a {@link EnergyDepot} or the energy depot is emptyw
     */
    public void removeOneEnergy() throws IncorrectShipBoardException{
        throw new IncorrectShipBoardException("not an energy depot");
    }

    /**
     * @ if the {@link EnergyDepot} is triple
     * @throws {@link IncorrectShipBoardException}if the component is not a {@link EnergyDepot} or the energy depot is emptyw
     */
    public boolean getTriple() throws IncorrectShipBoardException{
        throw new IncorrectShipBoardException("not an energy depot");
    }

    /**
     * add the {@link GoodType} given to the {@link Depot}
     * @param type the {@link GoodType} to add
     * @throws IncorrectShipBoardException throws the exception if the {@link Depot} is already full
     *      or trying to add {@link Depot RED} in not a hazrd {@link Depot}
     */
    public void addGood(GoodType type) throws IncorrectShipBoardException {
        throw new IncorrectShipBoardException("not a depot");
    }

    /**
     *
     * @param type the {@link GoodType} to remove
     * @throws IncorrectShipBoardException if the component is not a {@link Depot} or
     *  the depot does not contain the giver {@link GoodType}
     */
    public void removeGood(GoodType type) throws IncorrectShipBoardException{
        throw new IncorrectShipBoardException("not a depot");
    }

    /**
     *
     * @return the list of {@link GoodType} contained in the {@link Depot}, an empty list if the component is not a {@link Depot}
     */
    public List<GoodType> getStoredGoods(){
        return Collections.emptyList();
    }


    /**
     * add the given {@link GuestType} to the allowed guest of the housing
     * @param type the {@link GuestType} to add
     * @throws IncorrectShipBoardException if the component is not a {@link Housing}
     */
    public void addAllowedGuest(GuestType type) throws IncorrectShipBoardException {
        throw new IncorrectShipBoardException("not a housing");
    }

    /**
     * add the given {@link GuestType} to the {@link Housing}
     * @param guestType the {@link GuestType} to add
     * @throws IncorrectShipBoardException if the guestType cannot be added to the housing or the comoponent is not a {@link Housing}
     */
    public void addGuest(GuestType guestType) throws IncorrectShipBoardException{
        throw new IncorrectShipBoardException("not a housing");
    }

    /**
     *
     * @return the {@link GuestType} of the {@link AlienHousing}
     * @throws IncorrectShipBoardException if the component is not an {@link AlienHousing}
     */
    public GuestType getColorAlien() throws IncorrectShipBoardException{
        throw new IncorrectShipBoardException("not a alien housing");
    }

    /**
     *
     * @return the list of {@link GuestType} hosted in the {@link Housing},
     *  an empty list if the component is not a {@link Housing}
     *
     */
    public List<GuestType> getGuests() {
        return Collections.emptyList();
    }

    /**
     * remove a {@link GuestType} from the {@link Housing}
     * @throws IncorrectShipBoardException if is not a {@link Housing} or the {@link Housing} is empty
     */
    public void removeGuest() throws IncorrectShipBoardException{
        throw new IncorrectShipBoardException("not a housing");
    }


    /**
     *
     * @param activated : boolean expressing if the {@link DoubleEngine} has been activated, not important for {@link Engine}
     * @return 0 if is not a {@link Engine} or {@link DoubleEngine}, or it is a {@link DoubleEngine} but it is not activated, otherwise returns the engine power of the {@link Component}
     * */
    public int getEnginePower(boolean activated){
        return 0;
    }


    /**
     *
     * @param actived : boolean expressing if the {@link DoubleDrill} has been activated, not important for {@link Drill}
     * @return 0 if is not a {@link Drill} or {@link DoubleDrill}, or it is a {@link DoubleDrill} but it is not activated, otherwise returns the fire power of the {@link Component}
     * */
    public float getFirePower(boolean actived){
        return 0f;
    }

    /**
     * valid only on {@link Drill}, {@link DoubleDrill}, {@link Engine}, {@link DoubleEngine}
     * @return the direction toward which the {@link Component} ponts, null if the {@link Component} has no {@link Direction}
     */
    public Direction getDirection(){
        return null;
    }

    /**
     *
     * @return a {@link List} of {@link Direction} of the {@link Shield}, an emptyList if is not a {@link Shield}
     */
    public List<Direction> getShieldDirections(){
        return Collections.emptyList();
    }

    /**
     *
     * @param dir : {@link Direction} to look for
     * @return : the relative {@link Connector}
     */
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
     * @return whether the given {@link GoodType} can be added in the {@link  Depot}
     * @throws IncorrectShipBoardException if the component is not a {@link Depot}
     */
    public boolean canContainsGood(GoodType type) throws IncorrectShipBoardException{
        return false;
    }

    /**
     *
     * @return whether the {@link Depot} can contain RED {@link GoodType}
     * @throws IncorrectShipBoardException if is not a {@link Depot}
     */
    public boolean getHazard() throws IncorrectShipBoardException{
        throw new IncorrectShipBoardException("not a depot");
    }

    /**
     *
     * @return whether the {@link Depot} is big, following the rules of the game
     * @throws IncorrectShipBoardException if is not a {@link Depot}
     */
    public boolean getBig() throws IncorrectShipBoardException{
        throw new IncorrectShipBoardException("not a depot");
    }

    /**
     *
     * @return the id of the component, it refers to the id given to the JSON file
     */
    public int getId() {
        return id;
    }

    /**
     * Returns a string representation of the component, including its type,
     * connectors (top, bottom, right, left), and position (row and column).
     *
     * @return a formatted string describing the component's state
     */
    public String toString(){
        return this.type +
                " Top: " + topConnector.toString() +
                " Bottom: " + bottomConnector.toString() +
                " Right: " + rightConnector.toString() +
                " Left: " + leftConnector.toString() +
                " Row: " + row + " Column: " + column;
    }

    /**
     * Resets the component's rotation to its original orientation.
     * Applies the minimal number of clockwise rotations needed to return to the initial state.
     * Ensures the rotation count stays within the [0, 3] range.
     */
    public void reinitializeRotations(){
        Logger.debug("initial rotations " + rotations);
        rotations = rotations % 4;

        rotate(4 - rotations);
    }

    public boolean canAddGuest(GuestType type){
        return false;
    }

}