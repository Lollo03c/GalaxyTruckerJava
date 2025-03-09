package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;

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
    public Integer getEnergyQuantity(){
        return storedQuant;
    }

    @Override
    //returns true if the energy depot contains some enery to remove
    public Boolean removeOneEnergy(){
        if (storedQuant >= 1){
            storedQuant--;
            return true;
        }

        return false;
    }
}
