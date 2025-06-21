package org.mio.progettoingsoft;

import org.mio.progettoingsoft.components.*;
import org.mio.progettoingsoft.components.GuestType;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;
import org.mio.progettoingsoft.utils.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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


    public ComponentType getType(){
        return type;
    }

//    public Boolean isExposedSide(Direction direction){
//        Connector comp = switch (direction) {
//            case Direction.BACK -> bottomConnector;
//            case Direction.FRONT -> topConnector;
//            case Direction.RIGHT -> rightConnector;
//            case Direction.LEFT -> leftConnector;
//        };
//
//        return !comp.equals(Connector.FLAT);
//    }

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

        rotations++;
    }

//    protected void rotateCounterClockwise(){
//        Connector tmp = this.topConnector;
//        this.topConnector = this.rightConnector;
//        this.rightConnector = this.bottomConnector;
//        this.bottomConnector = this.leftConnector;
//        this.leftConnector = tmp;
//    }

    public Integer getEnergyQuantity(){
        return 0;
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
     * add the {@link GoodType} given to the {@link org.mio.progettoingsoft.components.Depot}
     * @param type the {@link GoodType} to add
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
     * add the given {@link GuestType} to the {@link Housing}
     * @param guestType the {@link GuestType} to add
     * @throws IncorrectShipBoardException if the guestType cannot be added to the housing or the comoponent is not a {@link Housing}
     */
    public void addGuest(GuestType guestType) throws IncorrectShipBoardException{
        throw new IncorrectShipBoardException("not a housing");
    }


//    /**
//     *
//     * @param type the {@link GuestType} to add
//     * @return true if the given guestType can be added to the housing, false otherwise
//     * @throws IncorrectShipBoardException if the component is not a {@link Housing}
//     */
//    public boolean canAddGuest(GuestType type) throws IncorrectShipBoardException{
//        throw new IncorrectShipBoardException("not a housing");
//    }
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
     * @return whether the given {@link GoodType} can be added in the {@link  org.mio.progettoingsoft.components.Depot}
     * @throws IncorrectShipBoardException if the component is not a {@link org.mio.progettoingsoft.components.Depot}
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

    public String toString(){
        return this.type +
                " Top: " + topConnector.toString() +
                " Bottom: " + bottomConnector.toString() +
                " Right: " + rightConnector.toString() +
                " Left: " + leftConnector.toString() +
                " Row: " + row + " Column: " + column;
    }

    public void reinitilizeRotations(){

        Logger.debug("initial rotations " + rotations);
        rotations = rotations % 4;

        rotate(4 - rotations);
    }

}