//package org.mio.progettoingsoft;
//
//import org.junit.jupiter.api.Test;
//import org.mio.progettoingsoft.components.*;
//import org.mio.progettoingsoft.exceptions.FullGoodDepotException;
//import org.mio.progettoingsoft.exceptions.NotEnoughBatteriesException;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class PlayerTest {
//
//    Connector top = Connector.FLAT;
//    Connector bottom = Connector.FLAT;
//    Connector left = Connector.FLAT;
//    Connector right = Connector.FLAT;
//
//    @Test
//    public void should_add_batteries(){
//        Component c1 = new EnergyDepot(1, false, top, bottom, right, left);
//        Component c2 = new EnergyDepot(1, false, top, bottom, right, left);
//        Component c3 = new Housing(1, top, bottom, right, left);
//
//        Player player = new Player("test", HousingColor.BLUE);
//        player.addCompoment(c1, 1, 1, 0);
//        player.addCompoment(c2, 2, 2, 0);
//        player.addCompoment(c3, 3, 3, 0);
//
//        assertEquals(4, player.getShipBoard().getQuantBatteries());
//    }
//
//    @Test
//    public void should_remove_one_battery(){
//        Component c1 = new EnergyDepot(1, false, top, bottom, right, left);
//        Component c3 = new Housing(1, top, bottom, right, left);
//
//        Player player = new Player("test", HousingColor.BLUE);
//        player.addCompoment(c1, 1, 1, 0);
//        player.addCompoment(c3, 2, 2, 0);
//
//        player.getShipBoard().removeEnergy();
//        assertEquals(1, player.getShipBoard().getQuantBatteries());
//
//        player.getShipBoard().removeEnergy();
//        assertEquals(0, player.getShipBoard().getQuantBatteries());
//
//        assertThrows(NotEnoughBatteriesException.class, () -> player.getShipBoard().removeEnergy());
//    }
//
//    @Test
//    public void should_manage_two_batteries(){
//        Component c1 = new EnergyDepot(1, false, top, bottom, right, left);
//        Component c2 = new EnergyDepot(1, true, top, bottom, right, left);
//        Component c3 = new Housing(1, top, bottom, right, left);
//
//        Player player = new Player("test", HousingColor.BLUE);
//        player.addCompoment(c1, 1, 1, 0);
//        player.addCompoment(c3, 2, 2, 0);
//        player.addCompoment(c2, 3, 3, 0);
//
//        for (int i = 4; i >= 0; i--){
//            player.getShipBoard().removeEnergy();
//            assertEquals(i, player.getShipBoard().getQuantBatteries());
//        }
//
//        assertThrows(NotEnoughBatteriesException.class, () -> player.getShipBoard().removeEnergy());
//
//    }
//
////    @Test
////    public void should_add_some_goods(){
////        Player player = new Player("test");
////
////        Component c1 = new Depot(1, false, false, top, bottom, right, left);
////        Component c2 = new Depot(1, false, true, top, bottom, right, left);
////
////
////        player.addCompoment(c1, 1, 1, 0);
////        assertThrows(FullGoodDepotException.class, () -> player.getShipBoard().addGoods(GoodType.RED, 1));
////
////        player.addCompoment(c2, 2, 2, 0);
////        player.getShipBoard().addGoods(GoodType.RED, 1);
////        assertEquals(1, player.getShipBoard().getGoodsQuantiy(GoodType.RED));
////        assertEquals(0, player.getShipBoard().getGoodsQuantiy(GoodType.YELLOW));
////
////        player.addGoods(GoodType.YELLOW, 1);
////        assertEquals(1, player.getGoodsQuantiy(GoodType.YELLOW));
////
////        player.addGoods(GoodType.YELLOW, 1);
////        assertEquals(2, player.getGoodsQuantiy(GoodType.YELLOW));
////
////        assertThrows(FullGoodDepot.class, () -> player.addGoods(GoodType.BLUE, 1));
////        assertEquals(0, player.getGoodsQuantiy(GoodType.BLUE));
////    }
////
////    @Test
////    void should_remove_goods(){
////        Player player = new Player("test");
////
////        Component c1 = new Depot(1, false, false, top, bottom, right, left);
////
////        player.addCompoment(c1, 1, 1, 0);
////        player.addGoods(GoodType.YELLOW, 2);
////
////        player.removeGoods(GoodType.YELLOW, 1);
////        assertEquals(1, player.getGoodsQuantiy(GoodType.YELLOW));
////        assertEquals(0, player.getGoodsQuantiy(GoodType.BLUE));
////
////        assertThrows(NotEnoughGoods.class, () -> player.removeGoods(GoodType.BLUE, 1));
////
////        player.addGoods(GoodType.YELLOW, 1);
////        player.removeGoods(GoodType.YELLOW, 2);
////        assertEquals(0, player.getGoodsQuantiy(GoodType.YELLOW));
////
////
////    }
////
////    @Test
////    void should_add_some_aliens(){
////        Player player = new Player("test");
////
////        Component house = new Housing(1, Connector.TRIPLE, Connector.SINGLE, right, left);
////        Component brown = new AlienHousing(1, AlienType.BROWN, top, Connector.DOUBLE, right, left);
////        Component purple = new AlienHousing(1, AlienType.PURPLE, Connector.SINGLE, bottom, right, Connector.DOUBLE);
////
////        Component secondHouse = new Housing(1, top, bottom, Connector.DOUBLE, left);
////
////        player.addCompoment(house, 2, 3, 0);
////        player.addCompoment(brown, 1, 3, 0);
////        player.addCompoment(purple, 3, 3, 0);
////
////
////        assertTrue(((Housing)house).getGuestedAlien().containsKey(AlienType.BROWN));
////        assertTrue(((Housing)house).getGuestedAlien().containsKey(AlienType.PURPLE));
////        assertFalse(((Housing)house).getGuestedAlien().containsKey(AlienType.NOALIEAN));
////
////        player.addCompoment(secondHouse, 3, 2, 0);
////        assertTrue(((Housing)secondHouse).getGuestedAlien().containsKey(AlienType.PURPLE));
////        assertFalse(((Housing)secondHouse).getGuestedAlien().containsKey(AlienType.NOALIEAN));
////    }
//}