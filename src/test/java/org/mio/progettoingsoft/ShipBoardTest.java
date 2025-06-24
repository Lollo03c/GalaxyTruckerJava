package org.mio.progettoingsoft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.GuestType;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;
import org.mio.progettoingsoft.model.ShipBoardEasy;
import org.mio.progettoingsoft.model.ShipBoardNormal;
import org.mio.progettoingsoft.model.enums.GameMode;

import java.util.Collections;
import java.util.List;
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
    private ShipBoard yellow, validationTestShipBoard;
    @BeforeEach
    void setup(){
        flyBoard = FlyBoard.createFlyBoard(GameMode.NORMAL, Set.of("Antonio", "Andrea", "Lorenzo", "Stefano"));
        first = ShipBoardNormal.buildFirst(flyBoard);
        second = flyBoard.getPlayerByUsername("Andrea").getShipBoard();
        third = flyBoard.getPlayerByUsername("Lorenzo").getShipBoard();
        fourth = flyBoard.getPlayerByUsername("Stefano").getShipBoard();

        yellow = ShipBoardNormal.buildYellow(flyBoard);

        validationTestShipBoard = new ShipBoardNormal(HousingColor.RED, flyBoard);
    }

    @Test
    void should_count_exposed_connector(){
        assertEquals(4, yellow.getExposedConnectors());
        assertEquals(8, first.getExposedConnectors());
    }

    @Test
    void shoud_get_base_fire_power(){
        assertEquals(5.5, yellow.getBaseFirePower());
        assertEquals(3.5, first.getBaseFirePower());


    }

    @Test
    void should_get_base_engine_power(){
        assertEquals(3, yellow.getBaseEnginePower());
        assertEquals(1, first.getBaseEnginePower());
    }

    @Test
    void should_book_a_component(){
        int idComp = flyBoard.drawComponent();
        yellow.addBookedComponent(idComp);

        assertFalse(flyBoard.getCoveredComponents().contains(idComp));
        assertTrue(yellow.getBookedComponents().contains(Optional.of(idComp)));
    }

    @Test
    void should_swap_bookedComponents() {
        int first = flyBoard.drawComponent();
        yellow.addBookedComponent(first);

        int second = flyBoard.drawComponent();
        yellow.addBookedComponent(second);

        int third = flyBoard.drawComponent();
        yellow.swapBookComponent(third, 0);

        assertEquals(third, yellow.getBookedComponents().get(0).get());
        assertEquals(second, yellow.getBookedComponents().get(1).get());
    }

    @Test
    void should_remove_booked_component(){
        int comp = flyBoard.drawComponent();
        yellow.addBookedComponent(comp);
        assertTrue(yellow.getBookedComponents().contains(Optional.of(comp)));

        yellow.removedBookedComponent(0);
        assertFalse(yellow.getBookedComponents().contains(Optional.of(comp)));
    }

    @Test
    void should_test_shipboard_dimensions(){
        assertEquals(5, yellow.getOffsetRow());
        assertEquals(4, yellow.getOffsetCol());
    }

    @Test
    void should_test_stored_quantity(){
        for (GoodType type : GoodType.values()){
            switch (type){
                case BLUE -> {
                    assertEquals(2, yellow.getStoredQuantity(type));
                    assertEquals(2, first.getStoredQuantity(type));
                }

                case GREEN -> {
                    assertEquals(1, yellow.getStoredQuantity(type));
                    assertEquals(1, first.getStoredQuantity(type));
                }

                case YELLOW -> {
                    assertEquals(3, yellow.getStoredQuantity(type));
                    assertEquals(1, first.getStoredQuantity(type));
                }

                case RED -> {
                    assertEquals(1, yellow.getStoredQuantity(type));
                    assertEquals(1, first.getStoredQuantity(type));
                }
            }
        }
    }

    //todo bisogna farlo ricorsivo
    @Test
    void should_remove_component(){
        yellow.removeComponent(new Cordinate(0, 4));

        assertEquals(Optional.empty(), yellow.getOptComponentByCord(new Cordinate(0, 4)));
    }

    @Test
    void should_remove_energy(){
        final int initial = yellow.getQuantBatteries();
        assertEquals(5, yellow.getQuantBatteries());

        final int quantityRemoved = 2;
        List<Integer> removed = yellow.removeEnergy(quantityRemoved);

        assertEquals(initial - quantityRemoved, yellow.getQuantBatteries());
        assertEquals(quantityRemoved, removed.size());

        assertThrows(IncorrectShipBoardException.class, () -> yellow.removeEnergy(4));
    }

    @Test
    void should_test_matrix_id_creation(){
        Optional<Integer>[][] matrix = yellow.getComponentIdsMatrix();
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 7; j++){
                if (matrix[i][j].isEmpty())
                    assertEquals(yellow.getOptComponentByCord(new Cordinate(i, j)), matrix[i][j]);
                else
                    assertEquals(yellow.getOptComponentByCord(new Cordinate(i, j)).get().getId(), matrix[i][j].get());
            }
        }
    }

    @Test
    void should_count_double_engine(){
        assertEquals(1, yellow.getDoubleEngine().size());
        assertTrue(yellow.getDoubleEngine().contains(98));

        assertEquals(2, first.getDoubleEngine().size());
        assertTrue(first.getDoubleEngine().contains(97));
        assertTrue(first.getDoubleEngine().contains(96));
    }

    @Test
    void should_count_guests(){
        assertEquals(7, yellow.getQuantityGuests());
        assertEquals(7, first.getQuantityGuests());
    }

    @Test
    void should_stole_goods(){
        yellow.stoleGood(3);

        assertEquals(0, yellow.getStoredQuantity(GoodType.RED));
        assertEquals(1, yellow.getStoredQuantity(GoodType.YELLOW));
        assertEquals(1, yellow.getStoredQuantity(GoodType.GREEN));
        assertEquals(2, yellow.getStoredQuantity(GoodType.BLUE));

        yellow.stoleGood(100);
        for (GoodType type : GoodType.values())
            assertEquals(0, yellow.getStoredQuantity(type));

        assertEquals(0, yellow.getQuantBatteries());
    }

    @Test
    void should_get_double_drills(){
        List<Cordinate> cordinates = yellow.getDoubleDrills();

        assertEquals(2, cordinates.size());
        assertTrue(cordinates.contains(new Cordinate(1, 1)));
        assertTrue(cordinates.contains(new Cordinate(0, 4)));

        cordinates = first.getDoubleDrills();

        assertEquals(1, cordinates.size());
        assertTrue(cordinates.contains(new Cordinate(2, 6)));
    }

    @Test
    void should_get_covered_direction_from_shield(){
        for (Direction dir : Direction.values()){
            switch (dir){
                case FRONT -> {
                    assertFalse(yellow.coveredByShield(dir));
                    assertTrue(first.coveredByShield(dir));
                }

                case RIGHT -> {
                    assertTrue(yellow.coveredByShield(dir));
                    assertFalse(first.coveredByShield(dir));
                }

                case BACK -> {
                    assertTrue(yellow.coveredByShield(dir));
                    assertFalse(first.coveredByShield(dir));
                }

                case LEFT -> {
                    assertFalse(yellow.coveredByShield(dir));
                    assertTrue(first.coveredByShield(dir));
                }
            }
        }
    }

    @Test
    void should_find_adjacent_components(){
        validationTestShipBoard.addComponentToPosition(62, new Cordinate(2, 2), 0);
        assertEquals(62, validationTestShipBoard.getAdjacent(new Cordinate(2, 3)).get(Direction.LEFT).getId());

        validationTestShipBoard.addComponentToPosition(63, new Cordinate(1, 3), 0);
        assertEquals(63, validationTestShipBoard.getAdjacent(new Cordinate(2, 3)).get(Direction.FRONT).getId());

        validationTestShipBoard.addComponentToPosition(64, new Cordinate(2, 4), 0);
        assertEquals(64, validationTestShipBoard.getAdjacent(new Cordinate(2, 3)).get(Direction.RIGHT).getId());

        validationTestShipBoard.addComponentToPosition(65, new Cordinate(3, 3), 0);
        assertEquals(65, validationTestShipBoard.getAdjacent(new Cordinate(2, 3)).get(Direction.BACK).getId());

        validationTestShipBoard.addComponentToPosition(66, new Cordinate(1, 2), 0);
        assertEquals(66, validationTestShipBoard.getAdjacent(new Cordinate(2, 2)).get(Direction.FRONT).getId());
        assertEquals(66, validationTestShipBoard.getAdjacent(new Cordinate(1, 3)).get(Direction.LEFT).getId());
        assertEquals(63, validationTestShipBoard.getAdjacent(new Cordinate(1, 2)).get(Direction.RIGHT).getId());
    }

    @Test
    void should_find_incorrect_engines(){
        validationTestShipBoard.addComponentToPosition(71, new Cordinate(1, 3), 0);
        validationTestShipBoard.addComponentToPosition(72, new Cordinate(2, 5), 3); // upside
        validationTestShipBoard.addComponentToPosition(73, new Cordinate(2, 0), 3);

        validationTestShipBoard.addComponentToPosition(65, new Cordinate(4, 2), 0);
        validationTestShipBoard.addComponentToPosition(74, new Cordinate(3, 2), 0);

        validationTestShipBoard.addComponentToPosition(75, new Cordinate(4, 1), 0); // correct

        assertEquals(4, validationTestShipBoard.getIncorrectEngines().size());
    }

    @Test
    void should_find_incorrect_drills(){
        // Around central housing
        validationTestShipBoard.addComponentToPosition(101, new Cordinate(3, 3), 0);
        validationTestShipBoard.addComponentToPosition(102, new Cordinate(2, 4), 1);
        validationTestShipBoard.addComponentToPosition(103, new Cordinate(1, 3), 2);
        validationTestShipBoard.addComponentToPosition(104, new Cordinate(2, 2), 3);

        // Around ship
        validationTestShipBoard.addComponentToPosition(105, new Cordinate(4, 4), 0);
        validationTestShipBoard.addComponentToPosition(129, new Cordinate(2, 0), 3);
        validationTestShipBoard.addComponentToPosition(127, new Cordinate(0, 3), 0); // correct

        validationTestShipBoard.addComponentToPosition(65, new Cordinate(1, 5), 0); // depot
        validationTestShipBoard.addComponentToPosition(106, new Cordinate(2, 5), 0);
        assertEquals(6, validationTestShipBoard.getIncorrectDrills().size());
    }

    @Test
    void should_find_incorrect_connected_components() {
        validationTestShipBoard.addComponentToPosition(10, new Cordinate(2, 2), 0);
        validationTestShipBoard.addComponentToPosition(2, new Cordinate(1, 3), 0);
        validationTestShipBoard.addComponentToPosition(3, new Cordinate(1, 4), 0);
        validationTestShipBoard.addComponentToPosition(4, new Cordinate(2, 4), 0);
        validationTestShipBoard.addComponentToPosition(5, new Cordinate(3, 3), 0);
        validationTestShipBoard.addComponentToPosition(12, new Cordinate(3, 4), 0);

        assertEquals(5, validationTestShipBoard.getIncorrectConnectedComponents().size());
    }

    @Test
    void should_find_pieces_not_connected(){
        validationTestShipBoard.addComponentToPosition(2, new Cordinate(1, 3), 0);
        assertEquals(2, validationTestShipBoard.getMultiplePieces().size());

        validationTestShipBoard.addComponentToPosition(3, new Cordinate(0, 3), 0);
        validationTestShipBoard.addComponentToPosition(4, new Cordinate(3, 2), 0);
        validationTestShipBoard.addComponentToPosition(1, new Cordinate(2, 2), 0);
        validationTestShipBoard.addComponentToPosition(5, new Cordinate(3, 3), 0);
        assertEquals(2, validationTestShipBoard.getMultiplePieces().size());

        validationTestShipBoard.addComponentToPosition(6, new Cordinate(2, 5), 0);
        assertEquals(3, validationTestShipBoard.getMultiplePieces().size());
    }

    @Test
    void should_get_drills(){
        yellow.drawShipboard();

        for (Direction dir : Direction.values()){
            switch (dir){
                case FRONT -> {
                    List<Cordinate> drill = yellow.getDrills(dir);
                    assertEquals(4, drill.size());
                }

                case RIGHT -> {
                    List<Cordinate> drill = yellow.getDrills(dir);
                    assertEquals(2, drill.size());
                }

                case BACK -> {
                    List<Cordinate> drill = yellow.getDrills(dir);
                    assertEquals(0, drill.size());
                }

                case LEFT -> {
                    List<Cordinate> drill = yellow.getDrills(dir);
                    assertEquals(1, drill.size());
                }
            }
        }


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