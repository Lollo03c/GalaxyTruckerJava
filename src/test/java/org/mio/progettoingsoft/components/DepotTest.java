package org.mio.progettoingsoft.components;

import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.Connector;

import static org.junit.jupiter.api.Assertions.*;

class DepotTest {

    private Connector flat = Connector.FLAT;

    @Test
    void should_be_null_red_depot_if_not_hazard(){
        Component depot = new Depot(1, false, false, flat, flat, flat, flat);

        assertFalse(depot.canContainsGood(GoodType.RED));
    }

    @Test
    void shuold_load_1_good(){
        Component depot = new Depot(1, false, false, flat, flat, flat, flat);

        assertTrue(depot.addGood(GoodType.YELLOW));
        assertTrue(depot.canContainsGood(GoodType.YELLOW));
        assertEquals(0, depot.getStoredGoods().get(GoodType.BLUE));
    }

    @Test
    void should_not_load_3_good(){
        Component depot = new Depot(1, false, false, flat, flat, flat, flat);

        assertTrue(depot.addGood(GoodType.YELLOW));
        assertTrue(depot.addGood(GoodType.BLUE));
        assertFalse(depot.addGood(GoodType.GREEN));

        assertEquals(1, depot.getStoredGoods().get(GoodType.YELLOW));
        assertEquals(1, depot.getStoredGoods().get(GoodType.BLUE));
        assertEquals(0, depot.getStoredGoods().get(GoodType.GREEN));
    }

    @Test
    void should_not_load_4_good_if_triple(){
        Component depot = new Depot(1, true, false, flat, flat, flat, flat);

        assertTrue(depot.addGood(GoodType.YELLOW));
        assertTrue(depot.addGood(GoodType.BLUE));
        assertTrue(depot.addGood(GoodType.GREEN));
        assertFalse(depot.addGood(GoodType.GREEN));

        assertEquals(1, depot.getStoredGoods().get(GoodType.YELLOW));
        assertEquals(1, depot.getStoredGoods().get(GoodType.BLUE));
        assertEquals(1, depot.getStoredGoods().get(GoodType.GREEN));
    }

    @Test
    void should_load_red_if_only_hazard(){
        Component hazard = new Depot(1, false, true, flat, flat, flat, flat);
        Component simple = new Depot(1, false, false, flat, flat, flat, flat);

        assertTrue(hazard.addGood(GoodType.RED));
        assertEquals(1, hazard.getStoredGoods().get(GoodType.RED));

        assertFalse(simple.addGood(GoodType.RED));
        assertNull(simple.getStoredGoods().get(GoodType.RED));
    }

    @Test
    void should_remove_contained_good(){
        Component depot = new Depot(1, false, false, flat, flat, flat, flat);

        assertTrue(depot.addGood(GoodType.YELLOW));
        assertEquals(1, depot.getStoredGoods().get(GoodType.YELLOW));

        assertTrue(depot.removeGood(GoodType.YELLOW));
        assertEquals(0, depot.getStoredGoods().get(GoodType.YELLOW));
    }

    @Test
    void should_not_remove_not_contained_good() {
        Component depot = new Depot(1, false, false, flat, flat, flat, flat);

        assertFalse(depot.removeGood(GoodType.YELLOW));
    }

}