package org.mio.progettoingsoft;

import org.mio.progettoingsoft.components.AlienType;
import org.mio.progettoingsoft.components.GoodType;

import java.util.Collections;
import java.util.Map;

public abstract class Component {
    private Connector topConnector, bottomConnector, rightConnector, leftConnector;
    private final ComponentType type;
    
    private final int id;

    protected Component(int id, ComponentType type, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        topConnector = topConn;
        bottomConnector = bottomConn;
        rightConnector = rightConn;
        leftConnector = leftConn;
        this.id = id;
//togliamo il type, lo usiamo solo per fare lo switch per leggere il json, dopo non ci interessa tenerlo
        this.type = type;
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

    // the two "rotate" methods move the components in the selected order (clockwise or no)
    // then it should change the "pointing direction" of the component
    public void rotateClockwise(){
        Connector tmp = this.topConnector;
        this.topConnector = this.leftConnector;
        this.leftConnector = this.bottomConnector;
        this.bottomConnector = this.rightConnector;
        this.rightConnector = tmp;
    }

    public void rotateCounterClockwise(){
        Connector tmp = this.topConnector;
        this.topConnector = this.rightConnector;
        this.rightConnector = this.bottomConnector;
        this.bottomConnector = this.leftConnector;
        this.leftConnector = tmp;
    }

    public Integer getEnergyQuantity(){
        return 0;
    }

    public Boolean removeOneEnergy(){
        return false;
    }

    public Boolean addGood(GoodType type){
        return false;
    }

    public Boolean removeGood(GoodType type){
        return false;
    }

    public Map<GoodType, Integer> getStoredGoods(){
        return Collections.emptyMap();
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

    public Boolean addHumanMember(){
        return false;
    }

    public Boolean removeHumanMember(){
        return false;
    }

    public Integer getQuantityMembers(){
        return 0;
    }

    public Boolean addAlien(AlienType type){
        return false;
    }

    public Boolean removeAlien(AlienType type){
        return false;
    }

    public Boolean containsAlien(AlienType type){
        return false;
    }



}