package org.mio.progettoingsoft.model.components;

import org.mio.progettoingsoft.model.Component;
import org.mio.progettoingsoft.model.enums.ComponentType;
import org.mio.progettoingsoft.model.enums.Connector;

/**
 * Represents an "Alien Housing" component on a spaceship.
 * This component extends the base {@link Component} class and specifically
 * holds an alien guest of a particular color.
 */
public class AlienHousing extends Component {
    private final GuestType color;

    /**
     * Constructs a new AlienHousing component.
     *
     * @param id The unique identifier for this component.
     * @param color The {@link GuestType} representing the color of the alien guest.
     * @param topConn The connector on the top side of the component.
     * @param bottomConn The connector on the bottom side of the component.
     * @param rightConn The connector on the right side of the component.
     * @param leftConn The connector on the left side of the component.
     */
    public AlienHousing(int id, GuestType color, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn) {
        super(id, ComponentType.ALIEN_HOUSING,topConn, bottomConn, rightConn, leftConn);
        this.color = color;
    }

    /**
     * Overrides the base method to return the color/type of the alien guest housed in this component.
     *
     * @return The {@link GuestType} representing the alien's color.
     */
    @Override
    public GuestType getColorAlien() {
        return color;
    }
}
