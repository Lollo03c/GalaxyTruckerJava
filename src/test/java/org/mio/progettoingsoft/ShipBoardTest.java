package org.mio.progettoingsoft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.GuestType;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;
import org.mio.progettoingsoft.model.enums.GameMode;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ShipBoardTest {

/*
    QUESTO TEST NON GIRA: devo ancora capire se ho scritto male il test o se bisogna sistemare le classi
    @Test
    void should_create_basic_ship_board(){
        ShipBoard ship = new ShipBoard(HousingColor.BLUE);;
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

    private FlyBoard flyBoard;
    private ShipBoard first, second, third, fourth;
    @BeforeEach
    void setup(){
        flyBoard = FlyBoard.createFlyBoard(GameMode.NORMAL, Set.of("Antonio", "Andrea", "Lorenzo", "Stefano"));
        first = flyBoard.getPlayerByUsername("Antonio").getShipBoard();
        second = flyBoard.getPlayerByUsername("Andrea").getShipBoard();
        third = flyBoard.getPlayerByUsername("Lorenzo").getShipBoard();
        fourth = flyBoard.getPlayerByUsername("Stefano").getShipBoard();
    }

    @Test
    void should_add_a_component(){
//        Component comp = flyBoard.getComponentById(4);

        first.addComponentToPosition(5, new Cordinate(2, 4), 0);

        assertEquals(flyBoard.getComponentById(5), first.getOptComponentByCord(new Cordinate(2, 4)).get());
    }

    @Test
    void should_remove_component(){
        first.addComponentToPosition(5, new Cordinate(2, 2), 0);
        first.removeComponent(new Cordinate(2, 2));

        assertTrue(first.getOptComponentByCord(new Cordinate(2, 2)).isEmpty());
    }

    @Test
    void shuold_not_remove_component_if_not_present(){
        assertThrows(IncorrectShipBoardException.class, () -> first.removeComponent(new Cordinate(2, 2)));
    }

    @Test
    void shoud_not_add_two_component_in_the_same_place(){
        Cordinate position = new Cordinate(2, 2);

        first.addComponentToPosition(20, position, 0);
        assertThrows(IncorrectShipBoardException.class, () -> first.addComponentToPosition(30, position, 0));

        assertEquals(flyBoard.getComponentById(20), first.getOptComponentByCord(position).get());

    }

    @Test
    void should_add_some_batteries(){
        first.addComponentToPosition(1, new Cordinate(2, 4), 0);
        first.addComponentToPosition(2, new Cordinate(2, 2), 0);
        first.addComponentToPosition(3, new Cordinate(2, 1), 0);

        assertEquals(6, first.getQuantBatteries());
        assertEquals(0, first.getBaseEnginePower());
        assertEquals(0, first.getBaseFirePower());
    }

    @Test
    void should_add_some_drills(){
        first.addComponentToPosition(101, new Cordinate(2, 2), 0);
        first.addComponentToPosition(102, new Cordinate(2, 1), 0);
        first.addComponentToPosition(126, new Cordinate(2, 0), 0);

        assertEquals(0, first.getQuantBatteries());
        assertEquals(0, first.getBaseEnginePower());
        assertEquals(2, first.getBaseFirePower());
    }

    @Test
    void should_add_some_rotated_drills(){

        first.addComponentToPosition(101, new Cordinate(2, 2), 0);
        first.addComponentToPosition(102, new Cordinate(2, 1), 1);
        first.addComponentToPosition(126, new Cordinate(2, 0), 0);

        assertEquals(0, first.getQuantBatteries());
        assertEquals(0, first.getBaseEnginePower());
        assertEquals(1.5f, first.getBaseFirePower());
    }

    @Test
    void should_add_some_engine(){
        first.addComponentToPosition(71, new Cordinate(3, 3), 0);
        first.addComponentToPosition(72, new Cordinate(2, 2), 0);
        first.addComponentToPosition(92, new Cordinate(2, 1), 0);

        assertEquals(0, first.getQuantBatteries());
        assertEquals(2, first.getBaseEnginePower());
        assertEquals(0, first.getBaseFirePower());
    }

    @Test
    void should_add_some_components(){
        first.addComponentToPosition(71, new Cordinate(2, 2), 0);   //engine
        first.addComponentToPosition(92, new Cordinate(2, 1), 0);   //double engine
        first.addComponentToPosition(1, new Cordinate(2, 0), 1);    //double battery
        first.addComponentToPosition(12, new Cordinate(2, 4), 0);   //triple battery
        first.addComponentToPosition(101, new Cordinate(2, 5),  3); //drill
        first.addComponentToPosition(126, new Cordinate(3, 3), 0);  //double dirll

        assertEquals(5, first.getQuantBatteries());
        assertEquals(1, first.getBaseEnginePower());
        assertEquals(0.5f, first.getBaseFirePower());
    }

    @Test
    void should_stole_only_red(){
        first.addComponentToPosition(62, new Cordinate(2, 2), 0);
        first.addComponentToPosition(63, new Cordinate(2, 1), 0);

        flyBoard.getComponentById(62).addGood(GoodType.RED);
        flyBoard.getComponentById(63).addGood(GoodType.YELLOW);

        for (GoodType type : GoodType.values()){
            assertEquals(switch (type){
                case RED, YELLOW -> 1;
                default -> 0;
            }, first.getStoredQuantity(type));
        }

        first.stoleGood(1);
        for (GoodType type : GoodType.values()){
            assertEquals(switch (type){
                case YELLOW -> 1;
                default -> 0;
            }, first.getStoredQuantity(type));
        }

        first.stoleGood(1);
        for (GoodType type : GoodType.values()){
            assertEquals(switch (type){
                case YELLOW -> 0;
                default -> 0;
            }, first.getStoredQuantity(type));
        }
    }

    @Test
    void stole_battery(){
        first.addComponentToPosition(62, new Cordinate(2, 2), 0);
        first.addComponentToPosition(1, new Cordinate(2, 1), 0);

        flyBoard.getComponentById(62).addGood(GoodType.BLUE);
        assertEquals(2, first.getQuantBatteries());

        first.stoleGood(2);
        assertEquals(0, first.getStoredQuantity(GoodType.BLUE));
        assertEquals(1, first.getQuantBatteries());

        first.stoleGood(1);
        assertEquals(0, first.getQuantBatteries());
    }
//
    @Test
    void should_add_housing_with_top_alien(){

        first.addComponentToPosition(137, new Cordinate(2, 2), 0);
        first.addComponentToPosition(46, new Cordinate(2,1), 0);
        first.validateShip();
    }
//
//    @Test
//    void should_add_housing_with_right_alien(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//
//        Component housing = new Housing(1, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE);
//        Component alienHousing = new AlienHousing(1, GuestType.PURPLE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE);
//
//        ship.addComponentToPosition(housing, 2, 2);
//        ship.addComponentToPosition(alienHousing, 1, 2);
//
//        assertTrue(housing.canContainsAlien(GuestType.PURPLE));
//        assertFalse(housing.canContainsAlien(GuestType.BROWN));
//    }
//
//    @Test
//    void should_add_housing_with_left_alien(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//
//        Component housing = new Housing(1, flat, flat, flat, flat);
//        Component alienHousing = new AlienHousing(1, GuestType.PURPLE, flat, flat, flat, flat);
//
//        ship.addComponentToPosition(housing, 2, 2);
//        ship.addComponentToPosition(alienHousing, 2, 1);
//
//        assertTrue(housing.canContainsAlien(GuestType.PURPLE));
//        assertFalse(housing.canContainsAlien(GuestType.BROWN));
//    }
//
//    @Test
//    void shoudl_add_alien_first(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//
//        Component housing = new Housing(1, flat, flat, flat, flat);
//        Component alienHousing = new AlienHousing(1, GuestType.PURPLE, flat, flat, flat, flat);
//
//        ship.addComponentToPosition(alienHousing, 2, 2);
//        ship.addComponentToPosition(housing, 2, 1);
//
//        assertTrue(housing.canContainsAlien(GuestType.PURPLE));
//        assertFalse(housing.canContainsAlien(GuestType.BROWN));
//    }
//
//    @Test
//    void should_not_alien_if_not_adjacent(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//
//        Component housing = new Housing(1, flat, flat, flat, flat);
//        Component alienHousing = new AlienHousing(1, GuestType.PURPLE, flat, flat, flat, flat);
//
//        ship.addComponentToPosition(alienHousing, 2, 2);
//        ship.addComponentToPosition(housing, 2, 0);
//
//        assertFalse(housing.canContainsAlien(GuestType.PURPLE));
//        assertFalse(housing.canContainsAlien(GuestType.BROWN));
//    }
//
//    @Test
//    void should_be_not_able_to_load_red(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//
//        Component depot = new Depot(1, false, false, flat, flat, flat, flat);
//
//        assertFalse(depot.canContainsGood(GoodType.RED));
//        assertTrue(depot.canContainsGood(GoodType.YELLOW));
//
//        depot.addGood(GoodType.YELLOW);
//        assertFalse(depot.addGood(GoodType.RED));
//        depot.addGood(GoodType.YELLOW);
//
//        assertFalse(depot.canContainsGood(GoodType.YELLOW));
//        assertEquals(2, depot.getStoredGoods().get(GoodType.YELLOW));
//    }
//
//    @Test
//    void should_find_not_empty_depot(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//
//        Component empty = new Depot(1, false, false, flat, flat, flat, flat);
//        Component notEmpty = new Depot(2, false, false, flat, flat, flat, flat);
//
//        ship.addComponentToPosition(empty, 1, 1);
//        ship.addComponentToPosition(notEmpty, 2, 2);
//
//        notEmpty.addGood(GoodType.YELLOW);
//
//        List<Component> notEmptyList = ship.canRemoveGoods(GoodType.YELLOW);
//
//        assertTrue(notEmptyList.contains(notEmpty));
//        assertFalse(notEmptyList.contains(empty));
//
//    }
//
//    @Test
//    void should_find_not_empty_housing(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//
//        Component empty = new Housing(1, flat, flat, flat, flat);
//        Component notEmpty = new Housing(2, flat, flat, flat, flat);
//
//        ship.addComponentToPosition(empty, 1, 1);
//        ship.addComponentToPosition(notEmpty, 2, 2);
//
//        notEmpty.addHumanMember();
//
//        List<Component> notEmptyList = ship.canRemoveGuest();
//
//        assertTrue(notEmptyList.contains(notEmpty));
//        assertFalse(notEmptyList.contains(empty));
//
//    }
//
//    // starting section: shipboard-check test
//    void init(ShipBoard ship){
//        ship.addComponentToPosition(new Depot(2, false, false, Connector.TRIPLE, Connector.DOUBLE, flat, Connector.SINGLE), 2 ,4);
//        ship.addComponentToPosition(new Pipe(3, Connector.TRIPLE, Connector.FLAT, Connector.DOUBLE, Connector.SINGLE), 3, 3);
//        ship.addComponentToPosition(new EnergyDepot(4, false, Connector.FLAT, Connector.TRIPLE, Connector.SINGLE, Connector.DOUBLE), 3, 2);
//    }
//        // generic positioning tests
//    @Test
//    void should_pass_with_no_drills_or_engines(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(0, ship.getIncorrectComponents().size());
//    }
//
//    @Test
//    void should_fail_due_to_mismatching_connectors(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        ship.addComponentToPosition(new Housing(1, true, HousingColor.BLUE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE), 2, 3);
//        ship.addComponentToPosition(new Depot(2, false, false, flat, flat, flat, flat), 2, 4);
//        int size = 0;
//        size += ship.getIncorrectComponents().size();
//        assertEquals(2, size);
//    }
//
//    @Test
//    void should_find_two_blocks(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(1, ship.getMultiplePieces().size());
//        ship.addComponentToPosition(new Depot(3, false, false, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE), 0, 2);
//        assertEquals(2, ship.getMultiplePieces().size());
//        for(Set<Component> comps : ship.getMultiplePieces()){
//            System.out.println(ship.getMultiplePieces().indexOf(comps));
//            for(Component comp : comps){
//                System.out.println(comp);
//            }
//        }
//    }
//
//    @Test
//    void should_find_three_blocks_then_only_two(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(1, ship.getMultiplePieces().size());
//        assertEquals(0, ship.getIncorrectComponents().size());
//        ship.addComponentToPosition(new Depot(5, false, false, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE), 0, 2);
//        ship.addComponentToPosition(new Depot(6, false, false, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE), 1, 2);
//        ship.addComponentToPosition(new Depot(7, false, false, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE), 4, 0);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        assertEquals(3, ship.getMultiplePieces().size());
//        for(Set<Component> comps : ship.getMultiplePieces()){
//            System.out.println(ship.getMultiplePieces().indexOf(comps));
//            for(Component comp : comps){
//                System.out.println(comp);
//            }
//        }
//        ship.addComponentToPosition(new Depot(8, false, false, Connector.TRIPLE, Connector.FLAT, Connector.TRIPLE, Connector.TRIPLE), 2, 2);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        assertEquals(2, ship.getMultiplePieces().size());
//        for(Set<Component> comps : ship.getMultiplePieces()){
//            System.out.println(ship.getMultiplePieces().indexOf(comps));
//            for(Component comp : comps){
//                System.out.println(comp);
//            }
//        }
//    }
//
//    @Test
//    public void should_find_two_blocks_but_adjacent(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        ship.addComponentToPosition(new Pipe(1, Connector.TRIPLE, Connector.SINGLE, Connector.FLAT, Connector.FLAT), 3, 3);
//        ship.addComponentToPosition(new Pipe(2, Connector.FLAT, Connector.FLAT, Connector.FLAT, Connector.TRIPLE), 2, 4);
//        assertEquals(1, ship.getMultiplePieces().size());
//        assertEquals(0, ship.getIncorrectComponents().size());
//        ship.addComponentToPosition(new Pipe(3, Connector.FLAT, Connector.SINGLE, Connector.FLAT, Connector.FLAT), 3, 4);
//        ship.addComponentToPosition(new Pipe(3, Connector.SINGLE, Connector.FLAT, Connector.FLAT, Connector.FLAT), 4, 4);
//        assertEquals(2, ship.getMultiplePieces().size());
//        assertEquals(0, ship.getIncorrectComponents().size());
//    }
//
//    @Test
//    void should_fail_due_to_flat_to_double(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        ship.addComponentToPosition(new Depot(5, false, false, flat, flat, flat, Connector.DOUBLE), 3, 4);
//        assertEquals(2, ship.getIncorrectComponents().size());
//    }
//
//    @Test
//    void should_pass_with_flat_to_flat(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        ship.addComponentToPosition(new Pipe(5, Connector.FLAT, Connector.TRIPLE, Connector.FLAT, Connector.FLAT), 1, 4);
//        ship.addComponentToPosition(new Pipe(6, Connector.FLAT, Connector.TRIPLE, Connector.FLAT, Connector.FLAT), 1, 3);
//        assertEquals(0, ship.getIncorrectComponents().size());
//    }
//
//    /* Useless: in the rule book is not specified what to do with alien supports not connected to housings (they are like pipes)
//        //alien housing positioning test
//    @Test
//    void should_fail_due_to_alien_housing_not_connected_to_housing(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        ship.addComponentToPosition(new AlienHousing(5, AlienType.BROWN, Connector.FLAT, Connector.FLAT, Connector.DOUBLE, Connector.FLAT), 3, 1);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        assertEquals(1, ship.getIncorrectAlienHousings().size());
//    }
//
//    @Test
//    void should_fail_due_to_alien_housing_connected_to_first_housing(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        ship.addComponentToPosition(new AlienHousing(5, AlienType.BROWN, Connector.FLAT, Connector.FLAT, Connector.DOUBLE, Connector.FLAT), 2, 2);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        assertEquals(1, ship.getIncorrectAlienHousings().size());
//    }
//
//    @Test
//    void should_pass_with_alien_housing_connected_to_housing(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        ship.addComponentToPosition(new AlienHousing(5, AlienType.BROWN, Connector.SINGLE, Connector.FLAT, Connector.DOUBLE, Connector.FLAT), 3, 1);
//        ship.addComponentToPosition(new Housing(6, Connector.FLAT, Connector.SINGLE, Connector.FLAT, Connector.FLAT), 2, 1);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        assertEquals(0, ship.getIncorrectAlienHousings().size());
//    }
//     */
//
//        // drill positioning tests
//    @Test
//    void should_pass_with_front_drill(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        ship.addComponentToPosition(new Drill(5, Connector.FLAT, Connector.TRIPLE, Connector.FLAT, Connector.FLAT), 1, 3);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        assertEquals(0, ship.getIncorrectDrills().size());
//    }
//
//    @Test
//    void should_pass_with_rotated_drill(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        ship.addRotatedComponentToPosition(new Drill(5, Connector.FLAT, Connector.FLAT, Connector.TRIPLE, Connector.FLAT), 1, 3, 1);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        assertEquals(0, ship.getIncorrectDrills().size());
//    }
//
//    @Test
//    void should_fail_due_to_rotated_drill_directed_to_comp(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        ship.addRotatedComponentToPosition(new Drill(5, Connector.FLAT, Connector.FLAT, Connector.FLAT, Connector.TRIPLE), 2, 2, 2);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        assertEquals(1, ship.getIncorrectDrills().size());
//    }
//
//    @Test
//    void should_pass_with_rotated_drill_directed_to_comp_and_space(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        ship.addComponentToPosition(new Pipe(5, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE), 3, 1);
//        ship.addRotatedComponentToPosition(new Drill(6, flat, flat, Connector.TRIPLE, flat), 2, 1, 1);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        assertEquals(0, ship.getIncorrectDrills().size());
//    }
//
//        // double drill positioning test
//    @Test
//    void should_pass_with_front_double_drill(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        ship.addComponentToPosition(new DoubleDrill(5, Connector.FLAT, Connector.TRIPLE, Connector.FLAT, Connector.FLAT), 1, 3);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        assertEquals(0, ship.getIncorrectDrills().size());
//    }
//
//    @Test
//    void should_pass_with_rotated_double_drill(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        ship.addRotatedComponentToPosition(new DoubleDrill(5, Connector.FLAT, Connector.FLAT, Connector.TRIPLE, Connector.FLAT), 1, 3, 1);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        assertEquals(0, ship.getIncorrectDrills().size());
//    }
//
//    @Test
//    void should_fail_due_to_rotated_double_drill_directed_to_comp(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        ship.addRotatedComponentToPosition(new DoubleDrill(5, Connector.FLAT, Connector.FLAT, Connector.FLAT, Connector.TRIPLE), 2, 2, 2);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        assertEquals(1, ship.getIncorrectDrills().size());
//    }
//
//    @Test
//    void should_pass_with_rotated_double_drill_directed_to_comp_and_space(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        ship.addComponentToPosition(new Pipe(5, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE), 3, 1);
//        ship.addRotatedComponentToPosition(new DoubleDrill(6, flat, flat, Connector.TRIPLE, flat), 2, 1, 1);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        assertEquals(0, ship.getIncorrectDrills().size());
//    }
//        // engine positioning test
//    @Test
//    void should_fail_due_to_rotated_engine(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        ship.addRotatedComponentToPosition(new Engine(5, Connector.DOUBLE, Connector.FLAT, Connector.DOUBLE, Connector.FLAT), 3, 4, 3);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        assertEquals(1, ship.getIncorrectEngines().size());
//    }
//
//    @Test
//    void should_fail_due_to_engine_directed_to_comp(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        ship.addComponentToPosition(new Engine(5, flat, flat, Connector.SINGLE, flat), 2, 2);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        assertEquals(1, ship.getIncorrectEngines().size());
//    }
//
//    @Test
//    void should_pass_with_engine_directed_to_comp_with_space(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        ship.addComponentToPosition(new Pipe(5, Connector.FLAT, Connector.SINGLE, Connector.FLAT, Connector.TRIPLE), 1, 3);
//        ship.addComponentToPosition(new Engine(6, Connector.FLAT, Connector.FLAT, Connector.SINGLE, Connector.FLAT), 1, 2);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        assertEquals(0, ship.getIncorrectEngines().size());
//    }
//    // double engine positioning test
//    @Test
//    void should_fail_due_to_rotated_double_engine(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        ship.addRotatedComponentToPosition(new DoubleEngine(5, Connector.DOUBLE, Connector.FLAT, Connector.DOUBLE, Connector.FLAT), 3, 4, 3);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        assertEquals(1, ship.getIncorrectEngines().size());
//    }
//
//    @Test
//    void should_fail_due_to_double_engine_directed_to_comp(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        ship.addComponentToPosition(new DoubleEngine(5, flat, flat, Connector.SINGLE, flat), 2, 2);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        assertEquals(1, ship.getIncorrectEngines().size());
//    }
//
//    @Test
//    void should_pass_with_double_engine_directed_to_comp_with_space(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        ship.addComponentToPosition(new Pipe(5, Connector.FLAT, Connector.SINGLE, Connector.FLAT, Connector.TRIPLE), 1, 3);
//        ship.addComponentToPosition(new DoubleEngine(6, Connector.FLAT, Connector.FLAT, Connector.SINGLE, Connector.FLAT), 1, 2);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        assertEquals(0, ship.getIncorrectEngines().size());
//    }
//
//    // exposed connector tests
//    @Test
//    void should_init_ship_and_count_seven_exposed_connectors(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        assertEquals(7, ship.getExposedConnectors());
//    }
//
//    @Test
//    void should_init_ship_and_count_eight_exposed_connectors_with_border_components(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        ship.addComponentToPosition(new Pipe(5, Connector.SINGLE, Connector.SINGLE, Connector.SINGLE, Connector.FLAT), 4, 2);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        assertEquals(8, ship.getExposedConnectors());
//    }
//
//    @Test
//    void should_init_ship_and_count_seven_exposed_connectors_with_spaces(){
//        ShipBoard ship = new ShipBoard(HousingColor.BLUE);
//        init(ship);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        ship.addComponentToPosition(new Pipe(5, Connector.FLAT, Connector.TRIPLE, Connector.TRIPLE, Connector.FLAT), 2, 1);
//        ship.addComponentToPosition(new Pipe(6, Connector.SINGLE, Connector.FLAT, Connector.DOUBLE, Connector.FLAT), 3, 1);
//        assertEquals(0, ship.getIncorrectComponents().size());
//        assertEquals(7, ship.getExposedConnectors());
//    }

}