package org.mio.progettoingsoft;

import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.components.Depot;
import org.mio.progettoingsoft.components.EnergyDepot;

import static org.junit.jupiter.api.Assertions.*;

class FlyBoardTest {

    @Test
    void should_construct_153_components(){
        FlyBoard flyBoard = new FlyBoard();

        flyBoard.loadComponents();
        int loadedComponents = flyBoard.getCoveredComponents().size();

        assertEquals(153, loadedComponents);
    }

    @Test
    void should_load_11_double_batteries(){
        FlyBoard fly = new FlyBoard();

        fly.loadComponents();
        int count = 0;
        for (Component comp : fly.getCoveredComponents()){
            if (comp.getEnergyQuantity() == 2)
                count++;
        }

        assertEquals(11, count);
    }

    @Test
    void should_load_6_double_batteries(){
        FlyBoard fly = new FlyBoard();

        fly.loadComponents();
        int count = 0;
        for (Component comp : fly.getCoveredComponents()){
            if (comp.getEnergyQuantity() == 3)
                count++;
        }

        assertEquals(6, count);
    }

    @Test
    void should_load_9_simple_depot(){
        FlyBoard fly = new FlyBoard();

        fly.loadComponents();
        int count = 0;

        assertEquals(9, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.DEPOT))
                .map(component -> (Depot)component)
                .filter(depot -> !depot.getBig() && !depot.getHazard())
                .count()
        );
    }

    @Test
    void should_load_9_triple_depot(){
        FlyBoard fly = new FlyBoard();

        fly.loadComponents();
        int count = 0;

        assertEquals(6, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.DEPOT))
                .map(component -> (Depot)component)
                .filter(depot -> depot.getBig() && !depot.getHazard())
                .count()
        );
    }

    @Test
    void should_load_17_housing(){
        FlyBoard fly = new FlyBoard();

        fly.loadComponents();
        int count = 0;

        assertEquals(17, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.HOUSING))
                .count()
        );
    }

    @Test
    void should_load_8_housing(){
        FlyBoard fly = new FlyBoard();

        fly.loadComponents();
        int count = 0;

        assertEquals(8, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.HOUSING))
                .count()
        );
    }


}