package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;

import java.util.Optional;

public class EnergyDepot extends Component {

    private final Boolean isTriple;
    private Integer storedQuant;
    private Integer maxQuant;

    public EnergyDepot(int id, Boolean isTriple, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn) {
        super(id, ComponentType.ENERGY_DEPOT, topConn, bottomConn, rightConn, leftConn);

        this.isTriple = isTriple;
        storedQuant = isTriple ? 3 : 2;
        maxQuant = isTriple ? 3 : 2;
    }

    @Override
    public boolean getTriple(){
        return isTriple;
    }

    @Override
    public Integer getEnergyQuantity(){
        return storedQuant;
    }

    @Override
    /**
     * remove one energy from the depot
     * MUST BE check first if getEnergyQuanity() > 0
     */
    public void removeOneEnergy() throws IncorrectShipBoardException{
        if (storedQuant <= 0)
            throw new IncorrectShipBoardException("not enough batteries");

        storedQuant--;
    }

    @Override
    public boolean getTriple(){
        return isTriple;
    }
}
