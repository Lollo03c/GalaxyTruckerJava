package org.mio.progettoingsoft.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;

import java.text.CollationElementIterator;
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

        house.removeGuest(GuestType.HUMAN);
        house.removeGuest(GuestType.HUMAN);
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
            assertThrows(IncorrectShipBoardException.class, () -> house.removeGuest(type));
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
}