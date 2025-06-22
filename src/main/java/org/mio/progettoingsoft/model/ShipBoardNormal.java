package org.mio.progettoingsoft.model;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.ShipBoard;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.GuestType;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.InvalidCordinate;
import org.mio.progettoingsoft.views.tui.VisualShipboardNormal;

import java.time.Year;
import java.util.*;

public class ShipBoardNormal extends ShipBoard {

    public ShipBoardNormal(HousingColor color, FlyBoard flyBoard){
        super(color, flyBoard);
    }

    @Override
    protected List<Cordinate> getBannedCoordinates() {
        List<Cordinate> banned = new ArrayList<>();

        try {
            banned.add(new Cordinate(0, 0));
            banned.add(new Cordinate(0, 1));
            banned.add(new Cordinate(0, 5));
            banned.add(new Cordinate(0, 6));
            banned.add(new Cordinate(1, 0));
            banned.add(new Cordinate(1, 6));
            banned.add(new Cordinate(4, 3));
        } catch (InvalidCordinate e) {

        }
        return banned;
    }


    @Override
    public void drawShipboard() {
        VisualShipboardNormal shipboard = new VisualShipboardNormal(this.getComponents());
        shipboard.drawShipboard();
    }

    public static ShipBoard buildFirst(FlyBoard flyBoard){
        ShipBoard shipBoard = new ShipBoardNormal(HousingColor.GREEN, flyBoard);

        shipBoard.addComponentToPosition(101, new Cordinate(0, 2), 0);
        shipBoard.addComponentToPosition(109, new Cordinate(1, 1), 3);
        shipBoard.addComponentToPosition(31, new Cordinate(1, 2), 0);
        shipBoard.addComponentToPosition(11, new Cordinate(2, 1), 3);
        shipBoard.addComponentToPosition(3, new Cordinate(2, 2), 0);

        shipBoard.addComponentToPosition(42, new Cordinate(3, 1), 1);
        shipBoard.addComponentToPosition(96, new Cordinate(3, 2), 0);
        shipBoard.addComponentToPosition(39, new Cordinate(3, 3), 0);
        shipBoard.addComponentToPosition(21, new Cordinate(4, 0), 2);
        shipBoard.addComponentToPosition(97, new Cordinate(4, 1), 0);

        shipBoard.addComponentToPosition(155, new Cordinate(0,4), 3);
        shipBoard.addComponentToPosition(1, new Cordinate(1,4), 2);
        shipBoard.addComponentToPosition(64, new Cordinate(2,4), 1);
        shipBoard.addComponentToPosition(55, new Cordinate(3,4), 1);
        shipBoard.addComponentToPosition(76, new Cordinate(4,4), 0);
        shipBoard.addComponentToPosition(15, new Cordinate(1,5), 1);
        shipBoard.addComponentToPosition(49, new Cordinate(2,5), 0);
        shipBoard.addComponentToPosition(66, new Cordinate(3,5), 0);
        shipBoard.addComponentToPosition(146, new Cordinate(4,5), 2);
        shipBoard.addComponentToPosition(128, new Cordinate(2, 6), 0);
        shipBoard.addComponentToPosition(51, new Cordinate(3, 6), 3);
        shipBoard.addComponentToPosition(148, new Cordinate(4,6), 0);

        shipBoard.getOptComponentByCord(new Cordinate(1,2)).get().addGood(GoodType.BLUE);
        shipBoard.getOptComponentByCord(new Cordinate(1,2)).get().addGood(GoodType.GREEN);
        shipBoard.getOptComponentByCord(new Cordinate(4,0)).get().addGood(GoodType.BLUE);
        shipBoard.getOptComponentByCord(new Cordinate(2,4)).get().addGood(GoodType.RED);
        shipBoard.getOptComponentByCord(new Cordinate(3,5)).get().addGood(GoodType.YELLOW);

        shipBoard.getOptComponentByCord(new Cordinate(3,1)).get().addGuest(GuestType.HUMAN);
        shipBoard.getOptComponentByCord(new Cordinate(3,1)).get().addGuest(GuestType.HUMAN);
        shipBoard.getOptComponentByCord(new Cordinate(2,3)).get().addGuest(GuestType.HUMAN);
        shipBoard.getOptComponentByCord(new Cordinate(2,3)).get().addGuest(GuestType.HUMAN);
        shipBoard.getOptComponentByCord(new Cordinate(3,3)).get().addGuest(GuestType.HUMAN);
        shipBoard.getOptComponentByCord(new Cordinate(2,5)).get().addGuest(GuestType.HUMAN);
        shipBoard.getOptComponentByCord(new Cordinate(3,6)).get().addAllowedGuest(GuestType.PURPLE);
        shipBoard.getOptComponentByCord(new Cordinate(3,6)).get().addGuest(GuestType.PURPLE);

        return shipBoard;
    }



    public static ShipBoard buildFourth(FlyBoard flyBoard){
        ShipBoard shipBoard = new ShipBoardNormal(HousingColor.YELLOW, flyBoard);
        shipBoard.addComponentToPosition(92, new Cordinate(4, 2), 0);
        shipBoard.addComponentToPosition(113, new Cordinate(1, 3), 0);
        shipBoard.addComponentToPosition(100, new Cordinate(4, 5), 0);
        shipBoard.addComponentToPosition(72, new Cordinate(4, 4), 0);
        shipBoard.addComponentToPosition(125, new Cordinate(1, 5), 1);
        shipBoard.addComponentToPosition(23, new Cordinate(3, 2), 1);
        shipBoard.addComponentToPosition(50, new Cordinate(3, 5), 0);
        shipBoard.addComponentToPosition(25, new Cordinate(2, 5), 2);
        shipBoard.addComponentToPosition(139, new Cordinate(4, 6), 1);
        shipBoard.addComponentToPosition(75, new Cordinate(4,0), 0);
        shipBoard.addComponentToPosition(107, new Cordinate(0,2), 0);
        shipBoard.addComponentToPosition(69, new Cordinate(3,0), 3);
        shipBoard.addComponentToPosition(27, new Cordinate(4,1), 0);
        shipBoard.addComponentToPosition(13, new Cordinate(1,2), 0);
        shipBoard.addComponentToPosition(144, new Cordinate(2,0), 1);
        shipBoard.addComponentToPosition(36, new Cordinate(2,2), 3);
        shipBoard.addComponentToPosition(152, new Cordinate(2,6), 0);
        shipBoard.addComponentToPosition(62, new Cordinate(3,6), 3);
        shipBoard.addComponentToPosition(109, new Cordinate(0,4), 0);
        shipBoard.addComponentToPosition(41, new Cordinate(2,1), 0);
        shipBoard.addComponentToPosition(65, new Cordinate(2,4), 0);
        shipBoard.addComponentToPosition(47, new Cordinate(1,4), 1);
        shipBoard.addComponentToPosition(10, new Cordinate(3,1), 1);
        shipBoard.addComponentToPosition(8, new Cordinate(3,4), 2);
        return shipBoard;

    }

    public static ShipBoard buildYellow(FlyBoard flyBoard){
        ShipBoard shipBoard = new ShipBoardNormal(HousingColor.YELLOW, flyBoard);

        shipBoard.addComponentToPosition(78, new Cordinate(4, 0), 0);
        shipBoard.addComponentToPosition(90, new Cordinate(4, 1), 0);
        shipBoard.addComponentToPosition(36, new Cordinate(4, 5), 1);
        shipBoard.addComponentToPosition(6, new Cordinate(2, 5), 0);
        shipBoard.addComponentToPosition(118, new Cordinate(2, 0), 3);
        shipBoard.addComponentToPosition(19, new Cordinate(2, 4), 0);
        shipBoard.addComponentToPosition(84, new Cordinate(4, 4), 0);
        shipBoard.addComponentToPosition(25, new Cordinate(3, 5), 1);
        shipBoard.addComponentToPosition(16, new Cordinate(3, 1), 3);
        shipBoard.addComponentToPosition(40, new Cordinate(3, 3), 1);
        shipBoard.addComponentToPosition(143, new Cordinate(3, 4), 0);
        shipBoard.addComponentToPosition(122, new Cordinate(1, 3), 0);
        shipBoard.addComponentToPosition(18, new Cordinate(1, 2), 1);
        shipBoard.addComponentToPosition(108, new Cordinate(1, 5), 1);
        shipBoard.addComponentToPosition(98, new Cordinate(4, 2), 0);
        shipBoard.addComponentToPosition(126, new Cordinate(0, 4), 0);
        shipBoard.addComponentToPosition(102, new Cordinate(4, 6), 1);
        shipBoard.addComponentToPosition(130, new Cordinate(1, 1), 0);
        shipBoard.addComponentToPosition(50, new Cordinate(3, 0), 2);
        shipBoard.addComponentToPosition(114, new Cordinate(2, 6), 0);
        shipBoard.addComponentToPosition(65, new Cordinate(3, 6), 0);
        shipBoard.addComponentToPosition(151, new Cordinate(2, 2), 1);
        shipBoard.addComponentToPosition(58, new Cordinate(3, 2), 3);
        shipBoard.addComponentToPosition(59, new Cordinate(2, 1), 1);
        shipBoard.addComponentToPosition(54, new Cordinate(1, 4), 3);

        shipBoard.validateShip();

        shipBoard.getOptComponentByCord(new Cordinate(3, 0)).get().addGuest(GuestType.HUMAN);
        shipBoard.getOptComponentByCord(new Cordinate(3, 0)).get().addGuest(GuestType.HUMAN);
        shipBoard.getOptComponentByCord(new Cordinate(2, 3)).get().addGuest(GuestType.HUMAN);
        shipBoard.getOptComponentByCord(new Cordinate(2, 3)).get().addGuest(GuestType.HUMAN);
        shipBoard.getOptComponentByCord(new Cordinate(3, 3)).get().addGuest(GuestType.PURPLE);
        shipBoard.getOptComponentByCord(new Cordinate(4, 5)).get().addGuest(GuestType.HUMAN);
        shipBoard.getOptComponentByCord(new Cordinate(4, 5)).get().addGuest(GuestType.HUMAN);

        shipBoard.getOptComponentByCord(new Cordinate(3, 6)).get().addGood(GoodType.RED);
        shipBoard.getOptComponentByCord(new Cordinate(3, 5)).get().addGood(GoodType.YELLOW);
        shipBoard.getOptComponentByCord(new Cordinate(3, 5)).get().addGood(GoodType.BLUE);
        shipBoard.getOptComponentByCord(new Cordinate(1, 2)).get().addGood(GoodType.BLUE);
        shipBoard.getOptComponentByCord(new Cordinate(1, 2)).get().addGood(GoodType.YELLOW);
        shipBoard.getOptComponentByCord(new Cordinate(2, 4)).get().addGood(GoodType.YELLOW);
        shipBoard.getOptComponentByCord(new Cordinate(2, 4)).get().addGood(GoodType.GREEN);

        return shipBoard;
    }

    public static ShipBoard buildBlue(FlyBoard flyBoard){
        ShipBoard shipBoard = new ShipBoardNormal(HousingColor.BLUE, flyBoard);

        shipBoard.addComponentToPosition(115, new Cordinate(1, 3), 0);
        shipBoard.addComponentToPosition(103, new Cordinate(0, 4), 0);
        shipBoard.addComponentToPosition(43, new Cordinate(2, 4), 0);
        shipBoard.addComponentToPosition(145, new Cordinate(3, 3), 1);
        shipBoard.addComponentToPosition(12, new Cordinate(3, 4), 2);

        shipBoard.addComponentToPosition(73, new Cordinate(4, 4), 0);
        shipBoard.addComponentToPosition(152, new Cordinate(1, 5), 0);
        shipBoard.addComponentToPosition(72, new Cordinate(4, 0), 0);
        shipBoard.addComponentToPosition(67, new Cordinate(4, 2), 0);
        shipBoard.addComponentToPosition(110, new Cordinate(2, 0), 0);
        shipBoard.addComponentToPosition(155, new Cordinate(1, 1), 2);
        shipBoard.addComponentToPosition(107, new Cordinate(3, 6), 1);
        shipBoard.addComponentToPosition(91, new Cordinate(4, 6), 0);
        shipBoard.addComponentToPosition(69, new Cordinate(2, 1), 2);
        shipBoard.addComponentToPosition(38, new Cordinate(3, 0), 1);
        shipBoard.addComponentToPosition(93, new Cordinate(4, 5), 0);
        shipBoard.addComponentToPosition(141, new Cordinate(3, 1), 2);
        shipBoard.addComponentToPosition(4, new Cordinate(1, 2), 0);
        shipBoard.addComponentToPosition(106, new Cordinate(0, 2), 0);
        shipBoard.addComponentToPosition(57, new Cordinate(3, 5), 1);
        shipBoard.addComponentToPosition(46, new Cordinate(3, 2), 1);
        shipBoard.addComponentToPosition(121, new Cordinate(4, 1), 2);
        shipBoard.addComponentToPosition(37, new Cordinate(2, 2), 3);
        shipBoard.addComponentToPosition(56, new Cordinate(1, 4), 3);
        shipBoard.addComponentToPosition(31, new Cordinate(2, 5), 0);

        shipBoard.validateShip();

        shipBoard.getOptComponentByCord(new Cordinate(2, 1)).get().addGood(GoodType.RED);
        shipBoard.getOptComponentByCord(new Cordinate(2, 1)).get().addGood(GoodType.YELLOW);

        shipBoard.getOptComponentByCord(new Cordinate(2, 3)).get().addGuest(GuestType.PURPLE);
        shipBoard.getOptComponentByCord(new Cordinate(2, 4)).get().addGuest(GuestType.HUMAN);
        shipBoard.getOptComponentByCord(new Cordinate(2, 4)).get().addGuest(GuestType.HUMAN);

        shipBoard.getOptComponentByCord(new Cordinate(2, 5)).get().addGood(GoodType.YELLOW);
        shipBoard.getOptComponentByCord(new Cordinate(2, 5)).get().addGood(GoodType.GREEN);
        shipBoard.getOptComponentByCord(new Cordinate(2, 5)).get().addGood(GoodType.BLUE);

        shipBoard.getOptComponentByCord(new Cordinate(3, 0)).get().addGuest(GuestType.BROWN);
        shipBoard.getOptComponentByCord(new Cordinate(3, 2)).get().addGuest(GuestType.HUMAN);
        shipBoard.getOptComponentByCord(new Cordinate(3, 2)).get().addGuest(GuestType.HUMAN);

        shipBoard.getOptComponentByCord(new Cordinate(4, 2)).get().addGood(GoodType.RED);


        return shipBoard;
    }



    public static ShipBoard buildRed(FlyBoard flyBoard){
        ShipBoard shipBoard = new ShipBoardNormal(HousingColor.RED, flyBoard);

        shipBoard.addComponentToPosition(150, new Cordinate(3, 6), 0);
        shipBoard.addComponentToPosition(117, new Cordinate(3, 0), 3);
        shipBoard.addComponentToPosition(149, new Cordinate(3, 3), 2);
        shipBoard.addComponentToPosition(128, new Cordinate(2, 6), 1);
        shipBoard.addComponentToPosition(39, new Cordinate(4, 5), 0);

        shipBoard.addComponentToPosition(82, new Cordinate(4, 6), 0);
        shipBoard.addComponentToPosition(123, new Cordinate(1, 3), 0);
        shipBoard.addComponentToPosition(1, new Cordinate(4, 0), 3);
        shipBoard.addComponentToPosition(26, new Cordinate(2, 0), 2);
        shipBoard.addComponentToPosition(77, new Cordinate(4, 2), 0);

        shipBoard.addComponentToPosition(29, new Cordinate(1, 5), 3);
        shipBoard.addComponentToPosition(101, new Cordinate(0, 4), 0);
        shipBoard.addComponentToPosition(142, new Cordinate(4, 4), 1);
        shipBoard.addComponentToPosition(86, new Cordinate(4, 1), 0);
        shipBoard.addComponentToPosition(47, new Cordinate(2, 2), 0);

        shipBoard.addComponentToPosition(24, new Cordinate(2, 4), 2);
        shipBoard.addComponentToPosition(14, new Cordinate(1, 1), 1);
        shipBoard.addComponentToPosition(111, new Cordinate(0, 2), 0);
        shipBoard.addComponentToPosition(2, new Cordinate(3, 1), 1);
        shipBoard.addComponentToPosition(147, new Cordinate(2, 1), 0);

        shipBoard.addComponentToPosition(49, new Cordinate(2, 5), 1);
        shipBoard.addComponentToPosition(51, new Cordinate(3, 2), 2);
        shipBoard.addComponentToPosition(3, new Cordinate(3, 4), 1);
        shipBoard.addComponentToPosition(44, new Cordinate(1, 4), 0);
        shipBoard.addComponentToPosition(62, new Cordinate(1, 2), 2);
        shipBoard.addComponentToPosition(53, new Cordinate(3, 5), 2);

        shipBoard.validateShip();

        shipBoard.getOptComponentByCord(new Cordinate(1, 2)).get().addGood(GoodType.RED);

        shipBoard.getOptComponentByCord(new Cordinate(1, 4)).get().addGuest(GuestType.HUMAN);
        shipBoard.getOptComponentByCord(new Cordinate(1, 4)).get().addGuest(GuestType.HUMAN);

        shipBoard.getOptComponentByCord(new Cordinate(1, 5)).get().addGood(GoodType.BLUE);
        shipBoard.getOptComponentByCord(new Cordinate(1, 5)).get().addGood(GoodType.GREEN);
        shipBoard.getOptComponentByCord(new Cordinate(1, 5)).get().addGood(GoodType.GREEN);

        shipBoard.getOptComponentByCord(new Cordinate(2, 0)).get().addGood(GoodType.GREEN);
        shipBoard.getOptComponentByCord(new Cordinate(2, 0)).get().addGood(GoodType.YELLOW);

        shipBoard.getOptComponentByCord(new Cordinate(2, 2)).get().addGuest(GuestType.PURPLE);
        shipBoard.getOptComponentByCord(new Cordinate(2, 3)).get().addGuest(GuestType.HUMAN);
        shipBoard.getOptComponentByCord(new Cordinate(2, 3)).get().addGuest(GuestType.HUMAN);

        shipBoard.getOptComponentByCord(new Cordinate(2, 5)).get().addGuest(GuestType.HUMAN);
        shipBoard.getOptComponentByCord(new Cordinate(2, 5)).get().addGuest(GuestType.HUMAN);

        shipBoard.getOptComponentByCord(new Cordinate(3, 2)).get().addGuest(GuestType.HUMAN);
        shipBoard.getOptComponentByCord(new Cordinate(3, 2)).get().addGuest(GuestType.HUMAN);
        shipBoard.getOptComponentByCord(new Cordinate(4, 5)).get().addGuest(GuestType.BROWN);
        return shipBoard;
    }

    public static void main(String[] args) {
        FlyBoard flyBoard = new FlyBoardNormal(Set.of("anto", "ste"));
        ShipBoard ship = buildBlue(flyBoard);


        ShipBoard other = buildYellow(flyBoard);


        ship.drawShipboard();
        other.drawShipboard();

    }
}
