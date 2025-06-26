package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;

/**
 * Represents an "Energy Depot" component on a spaceship, used for storing energy (batteries).
 * Energy Depots can be "triple" (storing 3 units of energy) or standard (storing 2 units).
 * It extends the base {@link Component} class.
 */
public class EnergyDepot extends Component {
    private final Boolean isTriple;
    private Integer storedQuant;
    private Integer maxQuant;

    /**
     * Constructs a new EnergyDepot component.
     * The initial {@code storedQuant} and {@code maxQuant} are set based on the {@code isTriple} flag:
     * - If {@code isTriple} is true, max and stored quantity are 3.
     * - If {@code isTriple} is false, max and stored quantity are 2.
     *
     * @param id The unique identifier for this component.
     * @param isTriple A boolean indicating if this is a triple energy depot.
     * @param topConn The connector on the top side of the component.
     * @param bottomConn The connector on the bottom side of the component.
     * @param rightConn The connector on the right side of the component.
     * @param leftConn The connector on the left side of the component.
     */
    public EnergyDepot(int id, Boolean isTriple, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn) {
        super(id, ComponentType.ENERGY_DEPOT, topConn, bottomConn, rightConn, leftConn);

        this.isTriple = isTriple;
        storedQuant = isTriple ? 3 : 2;
        maxQuant = isTriple ? 3 : 2;
    }

    /**
     * Indicates if this energy depot is a triple depot.
     *
     * @return {@code true} if it's a triple energy depot, {@code false} otherwise.
     */
    @Override
    public boolean getTriple(){
        return isTriple;
    }

    /**
     * Retrieves the current quantity of energy stored in the depot.
     *
     * @return The number of energy units currently stored.
     */
    @Override
    public Integer getEnergyQuantity(){
        return storedQuant;
    }

    /**
     * Removes one unit of energy from the depot.
     *
     * @throws IncorrectShipBoardException if there is no energy left to remove ({@code storedQuant} is 0 or less).
     */
    @Override
    public void removeOneEnergy() throws IncorrectShipBoardException{
        if (storedQuant <= 0)
            throw new IncorrectShipBoardException("not enough batteries");

        storedQuant--;
    }
}