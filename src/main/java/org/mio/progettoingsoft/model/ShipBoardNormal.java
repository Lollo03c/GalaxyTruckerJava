package org.mio.progettoingsoft.model;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.ShipBoard;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.InvalidCordinate;
import org.mio.progettoingsoft.views.tui.VisualShipboardNormal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public static ShipBoard BuildFirst(){
        Set<String> nicks = new HashSet<>();
        nicks.add("anto");
        nicks.add("lollo");

        FlyBoard flyBoard = new FlyBoardNormal(nicks);
        ShipBoard shipBoard = new ShipBoardNormal(HousingColor.GREEN, flyBoard);

        shipBoard.addComponentToPosition(101, new Cordinate(0, 2), 0);
        shipBoard.addComponentToPosition(109, new Cordinate(1, 1), 3);
        shipBoard.addComponentToPosition(31, new Cordinate(1, 2), 0);
        shipBoard.addComponentToPosition(11, new Cordinate(2, 1), 3);
        shipBoard.addComponentToPosition(42, new Cordinate(3, 1), 1);
        shipBoard.addComponentToPosition(96, new Cordinate(3, 2), 0);
        shipBoard.addComponentToPosition(39, new Cordinate(3, 3), 0);
        shipBoard.addComponentToPosition(21, new Cordinate(4, 0), 2);
        shipBoard.addComponentToPosition(97, new Cordinate(4, 1), 0);

        shipBoard.addComponentToPosition(155, new Cordinate(0,4), 3);
        shipBoard.addComponentToPosition(1, new Cordinate(1,4), 2);
        shipBoard.addComponentToPosition(64, new Cordinate(2,4), 1);
        shipBoard.addComponentToPosition(55, new Cordinate(3,4), 1);
        shipBoard.addComponentToPosition(76, new Cordinate(4,4), 3);
        shipBoard.addComponentToPosition(15, new Cordinate(1,5), 1);
        shipBoard.addComponentToPosition(49, new Cordinate(2,5), 0);
        shipBoard.addComponentToPosition(66, new Cordinate(3,5), 0);
        shipBoard.addComponentToPosition(146, new Cordinate(4,5), 2);
        shipBoard.addComponentToPosition(128, new Cordinate(2, 6), 0);
        shipBoard.addComponentToPosition(51, new Cordinate(3, 6), 3);
        shipBoard.addComponentToPosition(148, new Cordinate(4,6), 0);


        shipBoard.drawShipboard();

        return shipBoard;
    }

    public static void main(String[] args) {
        ShipBoard ship = BuildFirst();
    }
}
