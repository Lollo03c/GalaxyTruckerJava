package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.Direction;

import java.util.*;

/**
 * Represents a "Shield" component on a spaceship.
 * Shields provide defensive capabilities to the ship by protecting specific directions.
 * A shield component inherently protects two adjacent directions, which rotate together.
 * It extends the base {@link Component} class.
 */
public class Shield extends Component {
    private List<Direction> directions;

    /**
     * Constructs a new Shield component.
     * Initially, the shield protects the {@link Direction#FRONT} and {@link Direction#RIGHT} directions.
     *
     * @param id The unique identifier for this component.
     * @param topConn The connector on the top side of the component.
     * @param bottomConn The connector on the bottom side of the component.
     * @param rightConn The connector on the right side of the component.
     * @param leftConn The connector on the left side of the component.
     */
    public Shield(int id, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(id, ComponentType.SHIELD, topConn, bottomConn, rightConn, leftConn);

        directions = new ArrayList<>(2);
        directions.add(Direction.FRONT);
        directions.add(Direction.RIGHT);
    }

    /**
     * Rotates the shield clockwise by 90 degrees.
     * This method also calls the superclass's {@code rotateClockwise} method
     * to handle any general component rotation logic.
     * Both protected directions are rotated together:
     * FRONT becomes RIGHT, RIGHT becomes BACK, BACK becomes LEFT, LEFT becomes FRONT.
     */
    @Override
    public void rotateClockwise(){
        super.rotateClockwise();
        directions.set(0, switch (directions.get(0)) {
            case FRONT -> Direction.RIGHT;
            case RIGHT -> Direction.BACK;
            case BACK -> Direction.LEFT;
            case LEFT -> Direction.FRONT;
        });
        directions.set(1, switch (directions.get(1)) {
            case FRONT -> Direction.RIGHT;
            case RIGHT -> Direction.BACK;
            case BACK -> Direction.LEFT;
            case LEFT -> Direction.FRONT;
        });
    }

    /**
     * Retrieves the list of directions currently protected by this shield.
     *
     * @return A {@link List} of {@link Direction} objects that the shield is covering.
     */
    @Override
    public List<Direction> getShieldDirections(){
        return directions;
    }

    /**
     * Provides a string representation of the Shield,
     * including its inherited properties and the directions it currently protects.
     *
     * @return A string containing component details and its protected directions.
     */
    public String toString(){
        return super.toString() + " Direction: " + getShieldDirections();
    }
}
