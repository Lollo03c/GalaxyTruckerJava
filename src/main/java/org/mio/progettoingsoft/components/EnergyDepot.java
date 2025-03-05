package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;

public class EnergyDepot extends Component {

    private final Boolean isTriple;
    private Integer storedQuant;

    public EnergyDepot(Boolean isTriple, Connector topConn, Connector bottomConn, org.mio.progettoingsoft.Connector rightConn, Connector leftConn) {
        super(ComponentType.ENERGY_DEPOT, topConn, bottomConn, rightConn, leftConn);

        this.isTriple = isTriple;
        storedQuant = isTriple ? 3 : 2;
    }

    public Integer getStoredQuantity(){
        return storedQuant;
    }

    //returns true if the energy depot contains some enery to remove
    public Boolean removeOneEnergy(){
        if (storedQuant > 1){
            storedQuant--;
            return true;
        }

        return false;
    }
}
