package org.mio.progettoingsoft.components;

import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.exceptions.NotEnoughHousing;

import static org.junit.jupiter.api.Assertions.*;

class HousingTest {

    private Connector flat = Connector.FLAT;

    @Test
    void shuold_add_1_member(){
        Component house = new Housing(1, flat, flat, flat, flat);
        house.addHumanMember();

        assertEquals(1, house.getQuantityMembers());
    }

    @Test
    void shuold_add_2_member(){
        Component house = new Housing(1, flat, flat, flat, flat);
        house.addHumanMember();
        house.addHumanMember();

        assertEquals(2, house.getQuantityMembers());
    }

    @Test
    void shuold_not_add_third_member(){
        Component house = new Housing(1, flat, flat, flat, flat);
        house.addHumanMember();
        house.addHumanMember();

        assertEquals(2, house.getQuantityMembers());
        assertFalse(house.addHumanMember());
        assertEquals(2, house.getQuantityMembers());
    }

    @Test
    void shuold_remove_two_member(){
        Component house = new Housing(1, flat, flat, flat, flat);
        house.addHumanMember();
        house.addHumanMember();

        assertEquals(2, house.getQuantityMembers());

        house.removeHumanMember();
        assertEquals(1, house.getQuantityMembers());

        house.removeHumanMember();
        assertEquals(0, house.getQuantityMembers());
    }

    @Test
    void shuold_not_remove_member_if_empty(){
        Component house = new Housing(1, flat, flat, flat, flat);

        assertFalse(house.removeHumanMember());
    }

    @Test
    void should_add_alien(){
        Component house = new Housing(1, flat, flat, flat, flat);
        house.addAlienType(AlienType.BROWN);

        assertTrue(house.addAlien(AlienType.BROWN));

        assertTrue(house.containsAlien(AlienType.BROWN));
        assertFalse(house.addAlien(AlienType.BROWN));
    }

    @Test
    void should_reject_alien_if_not_allowed(){
        Component house = new Housing(1, flat, flat, flat, flat);

        assertFalse(house.addAlien(AlienType.BROWN));
        assertFalse(house.containsAlien(AlienType.BROWN));
    }

    @Test
    void should_reject_human_if_alien_alredy_in(){
        Component house = new Housing(1, flat, flat, flat, flat);
        house.addAlienType(AlienType.BROWN);

        house.addAlien(AlienType.BROWN);
        assertTrue(house.containsAlien(AlienType.BROWN));

        assertFalse(house.addHumanMember());
        assertEquals(0, house.getQuantityMembers());
    }

    @Test
    void should_reject_alien_if_human_alredy_in(){
        Component house = new Housing(1, flat, flat, flat, flat);
        house.addAlienType(AlienType.BROWN);

        assertTrue(house.addHumanMember());
        assertEquals(1, house.getQuantityMembers());

        assertFalse(house.addAlien(AlienType.BROWN));
        assertFalse(house.containsAlien(AlienType.BROWN));
    }

    @Test
    void should_reject_other_alien(){
        Component house = new Housing(1, flat, flat, flat, flat);
        house.addAlienType(AlienType.BROWN);
        house.addAlienType(AlienType.PURPLE);

        assertTrue(house.addAlien(AlienType.PURPLE));
        assertFalse(house.addAlien(AlienType.BROWN));

        assertTrue(house.containsAlien(AlienType.PURPLE));
        assertFalse(house.containsAlien(AlienType.BROWN));
    }


}