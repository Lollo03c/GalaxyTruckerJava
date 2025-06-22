package org.mio.progettoingsoft.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class HousingTest {

    private Connector flat = Connector.FLAT;
    private Component house;

    private int count(GuestType type){
        return Collections.frequency(house.getGuests(), type);
    }

    @BeforeEach
    void setup(){
        house = new Housing(1, flat, flat, flat, flat);
    }

    @Test
    void shuold_add_1_member(){

        house.addGuest(GuestType.HUMAN);

        for (GuestType type : GuestType.values()){
            switch (type){
                case HUMAN -> assertEquals(1, count(type));
                default -> assertEquals(0, count(type));
            }
        }
    }

    @Test
    void shuold_add_2_member(){
        house.addGuest(GuestType.HUMAN);
        house.addGuest(GuestType.HUMAN);

        for (GuestType type : GuestType.values()){
            switch (type){
                case HUMAN -> assertEquals(2, count(type));
                default -> assertEquals(0, count(type));
            }
        }
    }

    @Test
    void shuold_not_add_third_member(){

        house.addGuest(GuestType.HUMAN);
        house.addGuest(GuestType.HUMAN);

        assertThrows(IncorrectShipBoardException.class, () -> house.addGuest(GuestType.HUMAN));

        for (GuestType type : GuestType.values()){
            switch (type){
                case HUMAN -> assertEquals(2, count(type));
                default -> assertEquals(0, count(type));
            }
        }
    }

    @Test
    void shuold_remove_two_member(){

        house.addGuest(GuestType.HUMAN);
        house.addGuest(GuestType.HUMAN);


        for (GuestType type : GuestType.values()){
            switch (type){
                case HUMAN -> assertEquals(2, count(type));
                default -> assertEquals(0, count(type));
            }
        }

        house.removeGuest();
        house.removeGuest();
        for (GuestType type : GuestType.values()){
            switch (type){
                case HUMAN -> assertEquals(0, count(type));
                default -> assertEquals(0, count(type));
            }
        }


    }

    @Test
    void shuold_not_remove_member_if_empty(){
        for (GuestType type : GuestType.values()){
            assertThrows(IncorrectShipBoardException.class, () -> house.removeGuest());
        }
    }

    @Test
    void should_add_alien(){
        house.addAllowedGuest(GuestType.BROWN);

        house.addGuest(GuestType.BROWN);
        for (GuestType type : GuestType.values()){
            switch (type){
                case BROWN -> assertEquals(1, count(type));
                default -> assertEquals(0, count(type));
            }

            assertThrows(IncorrectShipBoardException.class, () -> house.addGuest(type));
        }

        assertTrue(house.getGuests().contains(GuestType.BROWN));
        assertFalse(house.getGuests().contains(GuestType.PURPLE));
        assertFalse(house.getGuests().contains(GuestType.HUMAN));
    }

    @Test
    void should_reject_alien_if_not_allowed(){
        assertThrows(IncorrectShipBoardException.class, () -> house.addGuest(GuestType.BROWN));
        assertThrows(IncorrectShipBoardException.class, () -> house.addGuest(GuestType.PURPLE));
    }

    @Test
    void should_reject_human_if_alien_alredy_in(){
        house.addAllowedGuest(GuestType.BROWN);

        house.addGuest(GuestType.BROWN);
        for (GuestType type : GuestType.values()){
            switch (type){
                case BROWN -> assertEquals(1, count(type));
                default -> assertEquals(0, count(type));
            }
        }

        for (GuestType type : GuestType.values())
            assertThrows(IncorrectShipBoardException.class, () -> house.addGuest(type));

    }

    @Test
    void should_reject_alien_if_human_alredy_in(){
        house.addAllowedGuest(GuestType.BROWN);

        house.addGuest(GuestType.HUMAN);
        for (GuestType type : GuestType.values()){
            switch (type){
                case HUMAN -> assertEquals(1, count(type));
                default -> assertEquals(0, count(type));
            }
        }


        assertThrows(IncorrectShipBoardException.class, () -> house.addGuest(GuestType.BROWN));
        assertThrows(IncorrectShipBoardException.class, () -> house.addGuest(GuestType.PURPLE));
        house.addGuest(GuestType.HUMAN);
    }

    @Test
    void should_test_string_conversion_with_alien_type(){
        assertEquals(GuestType.PURPLE, GuestType.stringToAlienType("purple"));
        assertEquals(GuestType.BROWN, GuestType.stringToAlienType("brown"));
        assertEquals(GuestType.HUMAN, GuestType.stringToAlienType("asddsd"));

        assertEquals("PURPLE", GuestType.PURPLE.toString());
        assertEquals("BROWN", GuestType.BROWN.toString());
        assertEquals("HUMAN", GuestType.HUMAN.toString());

        assertEquals("\u001B[35m", GuestType.PURPLE.guestToColor());
        assertEquals("\u001B[33m", GuestType.BROWN.guestToColor());
        assertEquals("non importante", GuestType.HUMAN.guestToColor());
    }

    @Test
    void should_test_housing_color(){
        assertEquals(HousingColor.BLUE, HousingColor.stringToColor("blue"));
        assertEquals(HousingColor.GREEN, HousingColor.stringToColor("green"));
        assertEquals(HousingColor.YELLOW, HousingColor.stringToColor("yellow"));
        assertEquals(HousingColor.RED, HousingColor.stringToColor("red"));
        assertEquals(HousingColor.BLUE, HousingColor.stringToColor("fa"));

        assertEquals(HousingColor.BLUE, HousingColor.getHousingColorById(33));
        assertEquals(HousingColor.GREEN, HousingColor.getHousingColorById(34));
        assertEquals(HousingColor.YELLOW, HousingColor.getHousingColorById(61));
        assertEquals(HousingColor.RED, HousingColor.getHousingColorById(52));
        assertEquals(HousingColor.BLUE, HousingColor.getHousingColorById(1));

        for (HousingColor color : HousingColor.values()){
            switch (color){
                case BLUE -> {
                    assertEquals(33, color.getIdByColor());
                    assertEquals("\u001B[34m", color.colorToString());
                }

                case GREEN -> {
                    assertEquals(34, color.getIdByColor());
                    assertEquals("\u001B[32m", color.colorToString());
                }

                case YELLOW -> {
                    assertEquals(61, color.getIdByColor());
                    assertEquals("\u001B[33m", color.colorToString());
                }

                case RED-> {
                    assertEquals(52, color.getIdByColor());
                    assertEquals("\u001B[31m", color.colorToString());
                }
            }
        }

    }
}