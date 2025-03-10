package org.mio.progettoingsoft;

import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.components.*;
import org.mio.progettoingsoft.exceptions.EmptyComponent;
import org.mio.progettoingsoft.exceptions.FullGoodDepot;
import org.mio.progettoingsoft.exceptions.IncorrectPlacement;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ShipBoardTest {

    private Connector flat = Connector.FLAT;

    @Test
    void should_add_a_component(){
        ShipBoard ship = new ShipBoard();
        Component comp = new Housing(1, flat, flat, flat, flat);

        ship.addComponentToPosition(comp, 2, 2);

        assertEquals(comp, ship.getComponent(2, 2));
        assertThrows(EmptyComponent.class, () -> ship.getComponent(1, 1));
    }

    @Test
    void should_remove_component(){
        ShipBoard ship = new ShipBoard();
        Component comp = new Housing(1, flat, flat, flat, flat);

        ship.addComponentToPosition(comp, 2, 2);
        assertEquals(comp, ship.getComponent(2, 2));

        ship.removeComponent(2, 2);
        assertThrows(EmptyComponent.class, () -> ship.getComponent(2, 2));
    }

    @Test
    void shoud_not_add_two_component_in_the_same_place(){
        ShipBoard ship = new ShipBoard();
        Component comp = new Housing(1, flat, flat, flat, flat);
        Component comp2 = new Housing(1, flat, flat, flat, flat);

        ship.addComponentToPosition(comp, 2, 2);
        assertEquals(comp, ship.getComponent(2, 2));

        assertThrows(IncorrectPlacement.class, () -> ship.addComponentToPosition(comp2, 2, 2));
    }

    @Test
    void should_add_some_batteries(){
        ShipBoard ship = new ShipBoard();
        Component energy1 = new EnergyDepot(1, false, flat, flat, flat, flat);
        Component energy2 = new EnergyDepot(2, true, flat, flat, flat, flat);

        ship.addComponentToPosition(energy1, 1, 1);
        ship.addComponentToPosition(energy2, 2, 2);

        assertEquals(5, ship.getQuantBatteries());
        assertEquals(0, ship.getBaseEnginePower());
        assertEquals(0, ship.getBaseFirePower());
    }

    @Test
    void should_add_some_drills(){
        ShipBoard ship = new ShipBoard();
        Component drill1 = new Drill(1, flat, flat, flat, flat);
        Component drill2 = new Drill(2,  flat, flat, flat, flat);
        Component drill3 = new DoubleDrill(3,  flat, flat, flat, flat);

        ship.addComponentToPosition(drill1, 1, 1);
        ship.addComponentToPosition(drill2, 2, 2);
        ship.addComponentToPosition(drill3, 3, 3);

        assertEquals(0, ship.getQuantBatteries());
        assertEquals(0, ship.getBaseEnginePower());
        assertEquals(2, ship.getBaseFirePower());
    }

    @Test
    void should_add_some_rotated_drills(){
        ShipBoard ship = new ShipBoard();
        Component drill1 = new Drill(1, flat, flat, flat, flat);
        Component drill2 = new Drill(2,  flat, flat, flat, flat);
        Component drill3 = new DoubleDrill(3,  flat, flat, flat, flat);

        ship.addComponentToPosition(drill1, 1, 1);
        ship.addRotatedComponentToPosition(drill2, 2, 2, 1);
        ship.addComponentToPosition(drill3, 3, 3);

        assertEquals(0, ship.getQuantBatteries());
        assertEquals(0, ship.getBaseEnginePower());
        assertEquals(1.5f, ship.getBaseFirePower());
    }

    @Test
    void should_add_some_engine(){
        ShipBoard ship = new ShipBoard();
        Component engine1 = new Engine(1, flat, flat, flat, flat);
        Component engine2 = new Engine(1, flat, flat, flat, flat);
        Component engine3 = new DoubleEngine(1, flat, flat, flat, flat);

        ship.addComponentToPosition(engine1, 1, 1);
        ship.addRotatedComponentToPosition(engine2, 2, 2, 1);
        ship.addComponentToPosition(engine3, 3, 3);

        assertEquals(0, ship.getQuantBatteries());
        assertEquals(2, ship.getBaseEnginePower());
        assertEquals(0, ship.getBaseFirePower());
    }

    @Test
    void shuold_add_some_components(){
        ShipBoard ship = new ShipBoard();

        Component engine = new Engine(1, flat, flat, flat, flat);
        Component doubleEngine = new DoubleEngine(1, flat, flat, flat, flat);
        Component battery = new EnergyDepot(1, false, flat, flat, flat, flat);
        Component tripleBattery = new EnergyDepot(1, true, flat, flat, flat, flat);
        Component drill = new Drill(1, flat, flat, flat, flat);
        Component doubleDrill = new DoubleDrill(1, flat, flat, flat, flat);

        ship.addComponentToPosition(engine, 1, 2);
        ship.addComponentToPosition(doubleEngine, 1, 3);
        ship.addComponentToPosition(battery, 1, 4);
        ship.addComponentToPosition(tripleBattery, 2, 1);
        ship.addRotatedComponentToPosition(drill, 2, 2,  2);
        ship.addComponentToPosition(doubleDrill, 2, 3);

        assertEquals(5, ship.getQuantBatteries());
        assertEquals(1, ship.getBaseEnginePower());
        assertEquals(0.5f, ship.getBaseFirePower());
    }

    @Test
    void should_add_housing_with_bottom_alien(){
        ShipBoard ship = new ShipBoard();

        Component housing = new Housing(1, flat, flat, flat, flat);
        Component alienHousing = new AlienHousing(1, AlienType.PURPLE, flat, flat, flat, flat);

        ship.addComponentToPosition(housing, 2, 2);
        ship.addComponentToPosition(alienHousing, 3, 2);

        assertTrue(housing.canContainsAlien(AlienType.PURPLE));
        assertFalse(housing.canContainsAlien(AlienType.BROWN));
    }

    @Test
    void should_add_housing_with_top_alien(){
        ShipBoard ship = new ShipBoard();

        Component housing = new Housing(1, flat, flat, flat, flat);
        Component alienHousing = new AlienHousing(1, AlienType.PURPLE, flat, flat, flat, flat);

        ship.addComponentToPosition(housing, 2, 2);
        ship.addComponentToPosition(alienHousing, 1, 2);

        assertTrue(housing.canContainsAlien(AlienType.PURPLE));
        assertFalse(housing.canContainsAlien(AlienType.BROWN));
    }

    @Test
    void should_add_housing_with_right_alien(){
        ShipBoard ship = new ShipBoard();

        Component housing = new Housing(1, flat, flat, flat, flat);
        Component alienHousing = new AlienHousing(1, AlienType.PURPLE, flat, flat, flat, flat);

        ship.addComponentToPosition(housing, 2, 2);
        ship.addComponentToPosition(alienHousing, 2, 3);

        assertTrue(housing.canContainsAlien(AlienType.PURPLE));
        assertFalse(housing.canContainsAlien(AlienType.BROWN));
    }

    @Test
    void should_add_housing_with_left_alien(){
        ShipBoard ship = new ShipBoard();

        Component housing = new Housing(1, flat, flat, flat, flat);
        Component alienHousing = new AlienHousing(1, AlienType.PURPLE, flat, flat, flat, flat);

        ship.addComponentToPosition(housing, 2, 2);
        ship.addComponentToPosition(alienHousing, 2, 1);

        assertTrue(housing.canContainsAlien(AlienType.PURPLE));
        assertFalse(housing.canContainsAlien(AlienType.BROWN));
    }

    @Test
    void shoudl_add_alien_first(){
        ShipBoard ship = new ShipBoard();

        Component housing = new Housing(1, flat, flat, flat, flat);
        Component alienHousing = new AlienHousing(1, AlienType.PURPLE, flat, flat, flat, flat);

        ship.addComponentToPosition(alienHousing, 2, 2);
        ship.addComponentToPosition(housing, 2, 1);

        assertTrue(housing.canContainsAlien(AlienType.PURPLE));
        assertFalse(housing.canContainsAlien(AlienType.BROWN));
    }

    @Test
    void should_not_alien_if_not_adjacent(){
        ShipBoard ship = new ShipBoard();

        Component housing = new Housing(1, flat, flat, flat, flat);
        Component alienHousing = new AlienHousing(1, AlienType.PURPLE, flat, flat, flat, flat);

        ship.addComponentToPosition(alienHousing, 2, 2);
        ship.addComponentToPosition(housing, 2, 0);

        assertFalse(housing.canContainsAlien(AlienType.PURPLE));
        assertFalse(housing.canContainsAlien(AlienType.BROWN));
    }

    @Test
    void should_be_not_able_to_load_red(){
        ShipBoard ship = new ShipBoard();

        Component depot = new Depot(1, false, false, flat, flat, flat, flat);

        assertFalse(depot.canContainsGood(GoodType.RED));
        assertTrue(depot.canContainsGood(GoodType.YELLOW));

        depot.addGood(GoodType.YELLOW);
        assertFalse(depot.addGood(GoodType.RED));
        depot.addGood(GoodType.YELLOW);

        assertFalse(depot.canContainsGood(GoodType.YELLOW));
        assertEquals(2, depot.getStoredGoods().get(GoodType.YELLOW));
    }

    @Test
    void should_find_not_empty_depot(){
        ShipBoard ship = new ShipBoard();

        Component empty = new Depot(1, false, false, flat, flat, flat, flat);
        Component notEmpty = new Depot(2, false, false, flat, flat, flat, flat);

        ship.addComponentToPosition(empty, 1, 1);
        ship.addComponentToPosition(notEmpty, 2, 2);

        notEmpty.addGood(GoodType.YELLOW);

        List<Component> notEmptyList = ship.canRemoveGoods();

        assertTrue(notEmptyList.contains(notEmpty));
        assertFalse(notEmptyList.contains(empty));

    }

    @Test
    void should_find_not_empty_housing(){
        ShipBoard ship = new ShipBoard();

        Component empty = new Housing(1, flat, flat, flat, flat);
        Component notEmpty = new Housing(2, flat, flat, flat, flat);

        ship.addComponentToPosition(empty, 1, 1);
        ship.addComponentToPosition(notEmpty, 2, 2);

        notEmpty.addHumanMember();

        List<Component> notEmptyList = ship.canRemoveGuest();

        assertTrue(notEmptyList.contains(notEmpty));
        assertFalse(notEmptyList.contains(empty));

    }

}