package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.Direction;

/**
 * Represents an "Engine" component on a spaceship.
 * Engines provide movement power and have a facing direction.
 * This class serves as a base for different types of engines (e.g., single, double).
 * It extends the base {@link Component} class.
 */
public class Engine extends Component {
    private Direction direction;
    private final int enginePower = 1;

    /**
     * Constructs a new Engine component with a default backward direction.
     *
     * @param id The unique identifier for this component.
     * @param topConn The connector on the top side of the component.
     * @param bottomConn The connector on the bottom side of the component.
     * @param rightConn The connector on the right side of the component.
     * @param leftConn The connector on the left side of the component.
     */
    public Engine(int id, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(id, ComponentType.ENGINE, topConn, bottomConn, rightConn, leftConn);

        this.direction = Direction.BACK;
    }

    /**
     * Protected constructor for subclasses that extend Engine (e.g., DoubleEngine).
     *
     * @param id The unique identifier for this component.
     * @param type The specific {@link ComponentType} of the engine (e.g., ENGINE, DOUBLE_ENGINE).
     * @param topConn The connector on the top side of the component.
     * @param bottomConn The connector on the bottom side of the component.
     * @param rightConn The connector on the right side of the component.
     * @param leftConn The connector on the left side of the component.
     */
    protected Engine(int id, ComponentType type,Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(id, type, topConn, bottomConn, rightConn, leftConn);

        this.direction = Direction.BACK;
    }

    /**
     * Overrides the base method to return the current facing direction of the engine.
     *
     * @return The {@link Direction} the engine is facing.
     */
    @Override
    public Direction getDirection(){
        return direction;
    }

    /**
     * Calculates and returns the engine power provided by this component.
     * For a standard engine, the power is constant regardless of activation status,
     * but the `activated` parameter is kept for consistency with other components
     * or potential future variations.
     *
     * @param activated A boolean indicating whether the engine is activated (may not affect output for this class).
     * @return The engine power, which is {@code 1} for a standard engine.
     */
    @Override
    public int getEnginePower(boolean activated){
        return enginePower;
    }

    /**
     * Rotates the engine clockwise by 90 degrees.
     * This method also calls the superclass's {@code rotateClockwise} method
     * to handle any general component rotation logic.
     * The direction cycles through FRONT -> RIGHT -> BACK -> LEFT -> FRONT.
     */
    @Override
    public void rotateClockwise(){
        super.rotateClockwise();
        this.direction = switch(direction){
            case FRONT -> Direction.RIGHT;
            case RIGHT -> Direction.BACK;
            case BACK -> Direction.LEFT;
            case LEFT -> Direction.FRONT;
        };
    }

    /**
     * Provides a string representation of the Engine,
     * including its inherited properties and its current direction.
     *
     * @return A string containing component details and its direction.
     */
    public String toString(){
        return super.toString() + " Direction: " + getDirection();
    }
}
