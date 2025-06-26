package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.Direction;

/**
 * Represents a "Double Drill" component on a spaceship.
 * This type of drill provides varying amounts of fire power depending on
 * whether it's activated and its facing direction (front or other).
 * It extends the {@link Drill} class.
 */
public class DoubleDrill extends Drill{
    /**
     * Constructs a new DoubleDrill component.
     *
     * @param id The unique identifier for this component.
     * @param topConn The connector on the top side of the component.
     * @param bottomConn The connector on the bottom side of the component.
     * @param rightConn The connector on the right side of the component.
     * @param leftConn The connector on the left side of the component.
     */
    public DoubleDrill(int id, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(id, ComponentType.DOUBLE_DRILL, topConn, bottomConn, rightConn, leftConn);
    }

    /**
     * Overrides the base {@link Drill} method to calculate the fire power of this double drill.
     * If the drill is not activated, it returns 0.0f.
     * If activated:
     * - Returns 2.0f if the drill is facing {@link Direction#FRONT}.
     * - Returns 1.0f if the drill is facing any other direction.
     *
     * @param actived A boolean indicating whether the drill is activated.
     * @return The fire power provided by the drill.
     */
    @Override
    public float getFirePower(boolean actived){
        if (!actived)
            return 0.0f;

        if(getDirection() == Direction.FRONT)
            return 2f;
        return 1f;
    }

    /**
     * Provides a string representation of the DoubleDrill,
     * including its inherited properties and its current direction.
     *
     * @return A string containing component details and its direction.
     */
    public String toString(){
        return super.toString() + " Direction: " + getDirection();
    }
}