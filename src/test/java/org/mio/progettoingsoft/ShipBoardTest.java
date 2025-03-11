package org.mio.progettoingsoft;

import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.components.*;
import org.mio.progettoingsoft.exceptions.EmptyComponent;
import org.mio.progettoingsoft.exceptions.IncorrectPlacement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShipBoardTest {

    private Connector flat = Connector.FLAT;
/*
    @Test
    void should_create_basic_ship_board(){
        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
        for(int i = 0; i < ship.getRows(); i++){
            for(int j = 0; j < ship.getColumns(); j++){
                if(i == 2 && j == 3) {
                    assertEquals(ComponentType.HOUSING, ship.getComponent(i, j).getType());
                    assertTrue(ship.getComponent(i, j).isFirstHousing());
                }
                else
                    if(ship.isValidPosition(i,j))
                        assertTrue(ship.isEmptyComponent(i,j));
            }
        }
    }
 */

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
    void should_add_some_components(){
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

    // starting section: shipboard-check test
    void init(ShipBoard ship){
        ship.addComponentToPosition(new Housing(1, true, HousingColor.BLUE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE), 2, 3);
        ship.addComponentToPosition(new Depot(2, false, false, Connector.TRIPLE, Connector.DOUBLE, flat, Connector.SINGLE), 2 ,4);
        ship.addComponentToPosition(new Pipe(3, Connector.TRIPLE, Connector.FLAT, Connector.DOUBLE, Connector.SINGLE), 3, 3);
        ship.addComponentToPosition(new EnergyDepot(4, false, Connector.FLAT, Connector.TRIPLE, Connector.SINGLE, Connector.DOUBLE), 3, 2);
    }
        // generic positioning tests
    @Test
    void should_pass_with_no_drills_or_engines(){
        ShipBoard ship = new ShipBoard();
        init(ship);
        assertEquals(0, ship.getIncorrectComponents().size());
    }

    @Test
    void should_fail_due_to_mismatching_connectors(){
        ShipBoard ship = new ShipBoard();
        ship.addComponentToPosition(new Housing(1, true, HousingColor.BLUE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE), 2, 3);
        ship.addComponentToPosition(new Depot(2, false, false, flat, flat, flat, flat), 2, 4);
        int size = 0;
        size += ship.getIncorrectComponents().size();
        assertEquals(2, size);
    }

    @Test
    void should_fail_due_to_flying_components(){
        ShipBoard ship = new ShipBoard();
        init(ship);
        assertEquals(0, ship.getIncorrectComponents().size());
        ship.addComponentToPosition(new Depot(3, false, false, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE), 0, 2);
        assertEquals(1, ship.getIncorrectComponents().size());
    }

    @Test
    void should_fail_due_to_flat_to_double(){
        ShipBoard ship = new ShipBoard();
        init(ship);
        assertEquals(0, ship.getIncorrectComponents().size());
        ship.addComponentToPosition(new Depot(5, false, false, flat, flat, flat, Connector.DOUBLE), 3, 4);
        assertEquals(2, ship.getIncorrectComponents().size());
    }

    @Test
    void should_pass_with_flat_to_flat(){
        ShipBoard ship = new ShipBoard();
        init(ship);
        assertEquals(0, ship.getIncorrectComponents().size());
        ship.addComponentToPosition(new Pipe(5, Connector.FLAT, Connector.TRIPLE, Connector.FLAT, Connector.FLAT), 1, 4);
        ship.addComponentToPosition(new Pipe(6, Connector.FLAT, Connector.TRIPLE, Connector.FLAT, Connector.FLAT), 1, 3);
        assertEquals(0, ship.getIncorrectComponents().size());
    }

        //alien housing positioning test
    @Test
    void should_fail_due_to_alien_housing_not_connected_to_housing(){
        ShipBoard ship = new ShipBoard();
        init(ship);
        assertEquals(0, ship.getIncorrectComponents().size());
        ship.addComponentToPosition(new AlienHousing(5, AlienType.BROWN, Connector.FLAT, Connector.FLAT, Connector.DOUBLE, Connector.FLAT), 3, 1);
        assertEquals(0, ship.getIncorrectComponents().size());
        assertEquals(1, ship.getIncorrectAlienHousings().size());
    }

    @Test
    void should_fail_due_to_alien_housing_connected_to_first_housing(){
        ShipBoard ship = new ShipBoard();
        init(ship);
        assertEquals(0, ship.getIncorrectComponents().size());
        ship.addComponentToPosition(new AlienHousing(5, AlienType.BROWN, Connector.FLAT, Connector.FLAT, Connector.DOUBLE, Connector.FLAT), 2, 2);
        assertEquals(0, ship.getIncorrectComponents().size());
        assertEquals(1, ship.getIncorrectAlienHousings().size());
    }

    @Test
    void should_pass_with_alien_housing_connected_to_housing(){
        ShipBoard ship = new ShipBoard();
        init(ship);
        assertEquals(0, ship.getIncorrectComponents().size());
        ship.addComponentToPosition(new AlienHousing(5, AlienType.BROWN, Connector.SINGLE, Connector.FLAT, Connector.DOUBLE, Connector.FLAT), 3, 1);
        ship.addComponentToPosition(new Housing(6, Connector.FLAT, Connector.SINGLE, Connector.FLAT, Connector.FLAT), 2, 1);
        assertEquals(0, ship.getIncorrectComponents().size());
        assertEquals(0, ship.getIncorrectAlienHousings().size());
    }

        // drill positioning tests
    @Test
    void should_pass_with_front_drill(){
        ShipBoard ship = new ShipBoard();
        init(ship);
        assertEquals(0, ship.getIncorrectComponents().size());
        ship.addComponentToPosition(new Drill(5, Connector.FLAT, Connector.TRIPLE, Connector.FLAT, Connector.FLAT), 1, 3);
        assertEquals(0, ship.getIncorrectComponents().size());
        assertEquals(0, ship.getIncorrectDrills().size());
    }

    @Test
    void should_pass_with_rotated_drill(){
        ShipBoard ship = new ShipBoard();
        init(ship);
        assertEquals(0, ship.getIncorrectComponents().size());
        ship.addRotatedComponentToPosition(new Drill(5, Connector.FLAT, Connector.FLAT, Connector.TRIPLE, Connector.FLAT), 1, 3, 1);
        assertEquals(0, ship.getIncorrectComponents().size());
        assertEquals(0, ship.getIncorrectDrills().size());
    }

    @Test
    void should_fail_due_to_rotated_drill_directed_to_comp(){
        ShipBoard ship = new ShipBoard();
        init(ship);
        assertEquals(0, ship.getIncorrectComponents().size());
        ship.addRotatedComponentToPosition(new Drill(5, Connector.FLAT, Connector.FLAT, Connector.FLAT, Connector.TRIPLE), 2, 2, 2);
        assertEquals(0, ship.getIncorrectComponents().size());
        assertEquals(1, ship.getIncorrectDrills().size());
    }

    @Test
    void should_pass_with_rotated_drill_directed_to_comp_and_space(){
        ShipBoard ship = new ShipBoard();
        init(ship);
        assertEquals(0, ship.getIncorrectComponents().size());
        ship.addComponentToPosition(new Pipe(5, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE), 3, 1);
        ship.addRotatedComponentToPosition(new Drill(6, flat, flat, Connector.TRIPLE, flat), 2, 1, 1);
        assertEquals(0, ship.getIncorrectComponents().size());
        assertEquals(0, ship.getIncorrectDrills().size());
    }

        // double drill positioning test
    @Test
    void should_pass_with_front_double_drill(){
        ShipBoard ship = new ShipBoard();
        init(ship);
        assertEquals(0, ship.getIncorrectComponents().size());
        ship.addComponentToPosition(new DoubleDrill(5, Connector.FLAT, Connector.TRIPLE, Connector.FLAT, Connector.FLAT), 1, 3);
        assertEquals(0, ship.getIncorrectComponents().size());
        assertEquals(0, ship.getIncorrectDrills().size());
    }

    @Test
    void should_pass_with_rotated_double_drill(){
        ShipBoard ship = new ShipBoard();
        init(ship);
        assertEquals(0, ship.getIncorrectComponents().size());
        ship.addRotatedComponentToPosition(new DoubleDrill(5, Connector.FLAT, Connector.FLAT, Connector.TRIPLE, Connector.FLAT), 1, 3, 1);
        assertEquals(0, ship.getIncorrectComponents().size());
        assertEquals(0, ship.getIncorrectDrills().size());
    }

    @Test
    void should_fail_due_to_rotated_double_drill_directed_to_comp(){
        ShipBoard ship = new ShipBoard();
        init(ship);
        assertEquals(0, ship.getIncorrectComponents().size());
        ship.addRotatedComponentToPosition(new DoubleDrill(5, Connector.FLAT, Connector.FLAT, Connector.FLAT, Connector.TRIPLE), 2, 2, 2);
        assertEquals(0, ship.getIncorrectComponents().size());
        assertEquals(1, ship.getIncorrectDrills().size());
    }

    @Test
    void should_pass_with_rotated_double_drill_directed_to_comp_and_space(){
        ShipBoard ship = new ShipBoard();
        init(ship);
        assertEquals(0, ship.getIncorrectComponents().size());
        ship.addComponentToPosition(new Pipe(5, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE), 3, 1);
        ship.addRotatedComponentToPosition(new DoubleDrill(6, flat, flat, Connector.TRIPLE, flat), 2, 1, 1);
        assertEquals(0, ship.getIncorrectComponents().size());
        assertEquals(0, ship.getIncorrectDrills().size());
    }
        // engine positioning test
    @Test
    void should_fail_due_to_rotated_engine(){
        ShipBoard ship = new ShipBoard();
        init(ship);
        assertEquals(0, ship.getIncorrectComponents().size());
        ship.addRotatedComponentToPosition(new Engine(5, Connector.DOUBLE, Connector.FLAT, Connector.DOUBLE, Connector.FLAT), 3, 4, 3);
        assertEquals(0, ship.getIncorrectComponents().size());
        assertEquals(1, ship.getIncorrectEngines().size());
    }

    @Test
    void should_fail_due_to_engine_directed_to_comp(){
        ShipBoard ship = new ShipBoard();
        init(ship);
        assertEquals(0, ship.getIncorrectComponents().size());
        ship.addComponentToPosition(new Engine(5, flat, flat, Connector.SINGLE, flat), 2, 2);
        assertEquals(0, ship.getIncorrectComponents().size());
        assertEquals(1, ship.getIncorrectEngines().size());
    }

    @Test
    void should_pass_with_engine_directed_to_comp_with_space(){
        ShipBoard ship = new ShipBoard();
        init(ship);
        assertEquals(0, ship.getIncorrectComponents().size());
        ship.addComponentToPosition(new Pipe(5, Connector.FLAT, Connector.SINGLE, Connector.FLAT, Connector.TRIPLE), 1, 3);
        ship.addComponentToPosition(new Engine(6, Connector.FLAT, Connector.FLAT, Connector.SINGLE, Connector.FLAT), 1, 2);
        assertEquals(0, ship.getIncorrectComponents().size());
        assertEquals(0, ship.getIncorrectEngines().size());
    }
    // double engine positioning test
    @Test
    void should_fail_due_to_rotated_double_engine(){
        ShipBoard ship = new ShipBoard();
        init(ship);
        assertEquals(0, ship.getIncorrectComponents().size());
        ship.addRotatedComponentToPosition(new DoubleEngine(5, Connector.DOUBLE, Connector.FLAT, Connector.DOUBLE, Connector.FLAT), 3, 4, 3);
        assertEquals(0, ship.getIncorrectComponents().size());
        assertEquals(1, ship.getIncorrectEngines().size());
    }

    @Test
    void should_fail_due_to_double_engine_directed_to_comp(){
        ShipBoard ship = new ShipBoard();
        init(ship);
        assertEquals(0, ship.getIncorrectComponents().size());
        ship.addComponentToPosition(new DoubleEngine(5, flat, flat, Connector.SINGLE, flat), 2, 2);
        assertEquals(0, ship.getIncorrectComponents().size());
        assertEquals(1, ship.getIncorrectEngines().size());
    }

    @Test
    void should_pass_with_double_engine_directed_to_comp_with_space(){
        ShipBoard ship = new ShipBoard();
        init(ship);
        assertEquals(0, ship.getIncorrectComponents().size());
        ship.addComponentToPosition(new Pipe(5, Connector.FLAT, Connector.SINGLE, Connector.FLAT, Connector.TRIPLE), 1, 3);
        ship.addComponentToPosition(new DoubleEngine(6, Connector.FLAT, Connector.FLAT, Connector.SINGLE, Connector.FLAT), 1, 2);
        assertEquals(0, ship.getIncorrectComponents().size());
        assertEquals(0, ship.getIncorrectEngines().size());
    }

}