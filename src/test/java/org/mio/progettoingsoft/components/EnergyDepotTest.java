package org.mio.progettoingsoft.components;

import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.model.Component;
import org.mio.progettoingsoft.model.components.EnergyDepot;
import org.mio.progettoingsoft.model.enums.Connector;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;

import static org.junit.jupiter.api.Assertions.*;

class EnergyDepotTest {

    private Connector flat = Connector.FLAT;

    @Test
    void should_build_2_batteries(){
        Component energy = new EnergyDepot(1, false, flat, flat, flat, flat);

        assertFalse(energy.getTriple());
        assertEquals(2, energy.getEnergyQuantity());
    }

    @Test
    void should_build_3_batteries(){
        Component energy = new EnergyDepot(1, true, flat, flat, flat, flat);

        assertEquals(3, energy.getEnergyQuantity());
    }

    @Test
    void should_remove_two_batteries(){
        Component energy = new EnergyDepot(1, false, flat, flat, flat, flat);

        energy.removeOneEnergy();
        assertEquals(1, energy.getEnergyQuantity());

        energy.removeOneEnergy();
        assertEquals(0, energy.getEnergyQuantity());

        assertThrows(IncorrectShipBoardException.class, () -> energy.removeOneEnergy());
        assertEquals(0, energy.getEnergyQuantity());
    }
}