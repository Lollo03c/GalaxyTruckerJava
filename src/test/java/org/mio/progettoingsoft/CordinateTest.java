package org.mio.progettoingsoft;

import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.model.Cordinate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CordinateTest {

    @Test
    void should_compare_cordinates(){
        Cordinate first = new Cordinate(4, 4);
        Cordinate second = new Cordinate(4, 5);
        Cordinate third = new Cordinate(3, 4);
        Cordinate fourth = new Cordinate(4, 4);

        assertEquals(first, first);
        assertNotEquals(first, second);
        assertNotEquals(first, third);
        assertEquals(first, fourth);
    }

    @Test
    void should_get_adjacent(){
        Cordinate cord = new Cordinate(2, 2);
        List<Cordinate> adj = cord.getAdjacent();

        assertEquals(4, adj.size());
        List<Cordinate> valid = List.of(
                new Cordinate(1, 2),
                new Cordinate(3, 2),
                new Cordinate(2, 1),
                new Cordinate(2, 3)
        );
        assertTrue(adj.containsAll(valid));

        cord = new Cordinate(4, 6);
        adj = cord.getAdjacent();
        assertEquals(2, adj.size());
        valid = List.of(
                new Cordinate(3, 6),
                new Cordinate(4, 5)
        );
        assertTrue(adj.containsAll(valid));
    }

    @Test
    void should_create_the_right_iterator(){
        List<Cordinate> cords = new ArrayList<>();
        int count = 0;


        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 7; j++){
                cords.add(new Cordinate(i, j));
                count++;
            }
        }

        Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();
        while (cordinateIterator.hasNext()){
            Cordinate cord = cordinateIterator.next();
            count--;
            assertTrue(cords.contains(cord));
        }

        assertEquals(0, count);
    }
}