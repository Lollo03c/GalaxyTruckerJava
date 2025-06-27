package org.mio.progettoingsoft.model.components;

import org.mio.progettoingsoft.model.Component;
import org.mio.progettoingsoft.model.enums.ComponentType;
import org.mio.progettoingsoft.model.enums.Connector;
import org.mio.progettoingsoft.model.enums.Direction;

/**
 * Represents a "Drill" component on a spaceship.
 * Drills are used for combat or other actions requiring "fire power".
 * Their effectiveness (fire power) depends on their current facing direction.
 * This class extends the base {@link Component} class.
 */
public class Drill extends Component {
    private Direction direction;

    /**
     * Constructs a new Drill component with default front direction.
     *
     * @param id The unique identifier for this component.
     * @param topConn The connector on the top side of the component.
     * @param bottomConn The connector on the bottom side of the component.
     * @param rightConn The connector on the right side of the component.
     * @param leftConn The connector on the left side of the component.
     */
    public Drill(int id, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(id, ComponentType.DRILL, topConn, bottomConn, rightConn, leftConn);
        direction = Direction.FRONT;
    }

    /**
     * Protected constructor for subclasses that extend Drill (e.g., DoubleDrill).
     *
     * @param id The unique identifier for this component.
     * @param type The specific {@link ComponentType} of the drill (e.g., DRILL, DOUBLE_DRILL).
     * @param topConn The connector on the top side of the component.
     * @param bottomConn The connector on the bottom side of the component.
     * @param rightConn The connector on the right side of the component.
     * @param leftConn The connector on the left side of the component.
     */
    protected Drill(int id, ComponentType type, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(id, type, topConn, bottomConn, rightConn, leftConn);
        direction = Direction.FRONT;
    }

    /**
     * Overrides the base method to return the current facing direction of the drill.
     *
     * @return The {@link Direction} the drill is facing.
     */
    @Override
    public Direction getDirection(){
        return this.direction;
    }

    /**
     * Rotates the drill clockwise by 90 degrees.
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
     * Calculates and returns the fire power provided by the drill.
     *
     * @param actived A boolean indicating whether the drill is activated.
     * (Note: For a basic Drill, `actived` might not change the output,
     * but it's provided for consistency with subclasses like DoubleDrill).
     * @return The fire power: 1.0f if facing {@link Direction#FRONT}, otherwise 0.5f.
     */
    @Override
    public float getFirePower(boolean actived){
        if (direction.equals(Direction.FRONT))
            return 1.0f;

        return 0.5f;
    }

    /**
     * Provides a string representation of the Drill,
     * including its inherited properties and its current direction.
     *
     * @return A string containing component details and its direction.
     */
    public String toString(){
        return super.toString() + " Direction: " + getDirection();
    }
}
