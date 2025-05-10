package org.mio.progettoingsoft.components;

import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;

import java.net.CookieHandler;
import java.util.Collections;

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

        depot.addGood(GoodType.YELLOW);
        for (GoodType type : GoodType.values()){
            switch (type){
                case YELLOW -> {
                    assertTrue(depot.getStoredGoods().contains(GoodType.YELLOW));
                    assertEquals(1, depot.getStoredGoods().stream().filter(t -> t.equals(GoodType.YELLOW)).count());
                }
                default -> assertFalse(depot.getStoredGoods().contains(type));
            }
        }

        assertTrue(depot.canContainsGood(GoodType.YELLOW));
    }

    @Test
    void should_not_load_3_good(){
        Component depot = new Depot(1, false, false, flat, flat, flat, flat);

        depot.addGood(GoodType.YELLOW);
        depot.addGood(GoodType.BLUE);

        assertThrows(IncorrectShipBoardException.class, () -> depot.addGood(GoodType.GREEN));

        assertEquals(1, Collections.frequency(depot.getStoredGoods(), GoodType.YELLOW));
        assertEquals(1, Collections.frequency(depot.getStoredGoods(), GoodType.BLUE));
        assertEquals(0, Collections.frequency(depot.getStoredGoods(), GoodType.GREEN));
        assertEquals(0, Collections.frequency(depot.getStoredGoods(), GoodType.RED));
    }

    @Test
    void should_not_load_4_good_if_triple(){
        Component depot = new Depot(1, true, false, flat, flat, flat, flat);

        depot.addGood(GoodType.YELLOW);
        depot.addGood(GoodType.BLUE);
        depot.addGood(GoodType.GREEN);

        assertThrows(IncorrectShipBoardException.class, () -> depot.addGood(GoodType.RED));

        for (GoodType type : GoodType.values()){
            assertEquals( switch (type){
                case RED -> 0;
                default -> 1;
            }, Collections.frequency(depot.getStoredGoods(), type));
        }
    }

    @Test
    void should_load_red_if_only_hazard(){
        Component hazard = new Depot(1, false, true, flat, flat, flat, flat);
        Component simple = new Depot(1, false, false, flat, flat, flat, flat);

        hazard.addGood(GoodType.RED);
        for (GoodType type : GoodType.values()){
            assertEquals(switch (type) {
                case RED -> 1;
                default -> 0;
            }, Collections.frequency(hazard.getStoredGoods(), type));
        }

        assertThrows(IncorrectShipBoardException.class, () -> simple.addGood(GoodType.RED));
        for (GoodType type : GoodType.values()){
            assertEquals(switch (type) {
                case RED -> 0;
                default -> 0;
            }, Collections.frequency(simple.getStoredGoods(), type));
        }
    }

    @Test
    void should_remove_contained_good(){
        Component depot = new Depot(1, false, false, flat, flat, flat, flat);

        depot.addGood(GoodType.YELLOW);
        for (GoodType type : GoodType.values()){
            assertEquals(switch (type) {
                case YELLOW -> 1;
                default -> 0;
            }, Collections.frequency(depot.getStoredGoods(), type));
        }

        for (GoodType type : GoodType.values()){
            switch (type){
                case YELLOW -> depot.removeGood(type);
                default -> assertThrows(IncorrectShipBoardException.class, () -> depot.removeGood(type));
            }
        }
        for (GoodType type : GoodType.values()){
            assertEquals(switch (type) {
                case YELLOW -> 0;
                default -> 0;
            }, Collections.frequency(depot.getStoredGoods(), type));
        }
    }

    @Test
    void should_not_remove_not_contained_good() {
        Component depot = new Depot(1, false, false, flat, flat, flat, flat);

        for (GoodType type : GoodType.values()){

            assertThrows(IncorrectShipBoardException.class, () -> depot.removeGood(type));
        }

    }

}