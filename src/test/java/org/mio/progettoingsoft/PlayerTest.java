package org.mio.progettoingsoft;

import javafx.scene.effect.BlurType;
import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.components.Depot;
import org.mio.progettoingsoft.components.EnergyDepot;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.Housing;
import org.mio.progettoingsoft.exceptions.FullGoodDepot;
import org.mio.progettoingsoft.exceptions.NotEnoughBatteries;

import javax.swing.*;
import java.lang.reflect.ParameterizedType;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    Connector top = Connector.FLAT;
    Connector bottom = Connector.FLAT;
    Connector left = Connector.FLAT;
    Connector right = Connector.FLAT;

    @Test
    public void should_add_batteries(){
        Component c1 = new EnergyDepot(1, false, top, bottom, right, left);
        Component c2 = new EnergyDepot(1, false, top, bottom, right, left);
        Component c3 = new Housing(1, top, bottom, right, left);

        Player player = new Player("test");
        player.addCompoment(c1, 1, 1, 0);
        player.addCompoment(c2, 2, 2, 0);
        player.addCompoment(c3, 3, 3, 0);

        assertEquals(4, player.getShipBoard().getQuantBatteries());
    }

    @Test
    public void should_remove_one_battery(){
        Component c1 = new EnergyDepot(1, false, top, bottom, right, left);
        Component c3 = new Housing(1, top, bottom, right, left);

        Player player = new Player("test");
        player.addCompoment(c1, 1, 1, 0);
        player.addCompoment(c3, 2, 2, 0);

        player.removeEnergy();
        assertEquals(1, player.getQuantBatteries());

        player.removeEnergy();
        assertEquals(0, player.getQuantBatteries());

        assertThrows(NotEnoughBatteries.class, () -> player.removeEnergy());
    }

    @Test
    public void should_manage_two_batteries(){
        Component c1 = new EnergyDepot(1, false, top, bottom, right, left);
        Component c2 = new EnergyDepot(1, true, top, bottom, right, left);
        Component c3 = new Housing(1, top, bottom, right, left);

        Player player = new Player("test");
        player.addCompoment(c1, 1, 1, 0);
        player.addCompoment(c3, 2, 2, 0);
        player.addCompoment(c2, 3, 3, 0);

        for (int i = 4; i >= 0; i--){
            player.removeEnergy();
            assertEquals(i, player.getQuantBatteries());
        }

        assertThrows(NotEnoughBatteries.class, () -> player.removeEnergy());

    }

    @Test
    public void should_add_some_goods(){
        Player player = new Player("test");

        Component c1 = new Depot(1, false, false, top, bottom, right, left);
        Component c2 = new Depot(1, false, true, top, bottom, right, left);


        player.addCompoment(c1, 1, 1, 0);
        assertThrows(FullGoodDepot.class, () -> player.addGoods(GoodType.RED, 1));

        player.addCompoment(c2, 2, 2, 0);
        player.addGoods(GoodType.RED, 1);
        assertEquals(1, player.getGoodsQuantiy(GoodType.RED));
        assertEquals(0, player.getGoodsQuantiy(GoodType.YELLOW));

        player.addGoods(GoodType.YELLOW, 1);
        assertEquals(1, player.getGoodsQuantiy(GoodType.YELLOW));

        player.addGoods(GoodType.YELLOW, 1);
        assertEquals(2, player.getGoodsQuantiy(GoodType.YELLOW));

        assertThrows(FullGoodDepot.class, () -> player.addGoods(GoodType.BLUE, 1));
        assertEquals(0, player.getGoodsQuantiy(GoodType.BLUE));


    }
}