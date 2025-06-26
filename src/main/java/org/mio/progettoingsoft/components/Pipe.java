package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;

/**
 * Represents a "Pipe" component on a spaceship.
 * Pipes are fundamental structural components used to connect other parts of the ship.
 * They typically do not have special abilities like providing power, housing guests, or storing goods,
 * but are essential for the layout and connectivity of a ship's components.
 * It extends the base {@link Component} class.
 */
public class Pipe extends Component {
    /**
     * Constructs a new Pipe component.
     *
     * @param id The unique identifier for this component.
     * @param topConn The connector on the top side of the component.
     * @param bottomConn The connector on the bottom side of the component.
     * @param rightConn The connector on the right side of the component.
     * @param leftConn The connector on the left side of the component.
     */
    public Pipe(int id, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(id, ComponentType.PIPE, topConn, bottomConn, rightConn, leftConn);
    }
}
