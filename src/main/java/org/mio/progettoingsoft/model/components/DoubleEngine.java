package org.mio.progettoingsoft.model.components;

import org.mio.progettoingsoft.model.enums.ComponentType;
import org.mio.progettoingsoft.model.enums.Connector;

/**
 * Represents a "Double Engine" component on a spaceship.
 * This type of engine provides a fixed amount of engine power when activated.
 * It extends the {@link Engine} class.
 */
public class DoubleEngine extends Engine {
    private final int enginePower = 2;

    /**
     * Constructs a new DoubleEngine component.
     *
     * @param id The unique identifier for this component.
     * @param topConn The connector on the top side of the component.
     * @param bottomConn The connector on the bottom side of the component.
     * @param rightConn The connector on the right side of the component.
     * @param leftConn The connector on the left side of the component.
     */
    public DoubleEngine(int id, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(id, ComponentType.DOUBLE_ENGINE, topConn, bottomConn, rightConn, leftConn);
    }

    /**
     * Overrides the base {@link Engine} method to return the engine power of this double engine.
     * If the engine is activated, it returns its fixed {@code enginePower} (which is 2).
     * If not activated, it returns 0.
     *
     * @param activated A boolean indicating whether the engine is activated.
     * @return The engine power provided by the component.
     */
    @Override
    public int getEnginePower(boolean activated){
        if (activated)
            return enginePower;

        return 0;
    }

    /**
     * Provides a string representation of the DoubleEngine,
     * including its inherited properties and its current direction.
     *
     * @return A string containing component details and its direction.
     */
    public String toString(){
        return super.toString() + " Direction: " + getDirection();
    }
}
