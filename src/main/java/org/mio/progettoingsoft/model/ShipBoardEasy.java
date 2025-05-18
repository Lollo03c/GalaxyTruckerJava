package org.mio.progettoingsoft.model;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.ShipBoard;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.InvalidCordinate;

import java.util.ArrayList;
import java.util.List;

public class ShipBoardEasy extends ShipBoard {

    public ShipBoardEasy(HousingColor color, FlyBoard flyBoard){
        super(color, flyBoard);
    }

    @Override
    protected List<Cordinate> getBannedCoordinates(){
        List<Cordinate> banned = new ArrayList<>();

        try {
            banned.add(new Cordinate(0, 0));
            banned.add(new Cordinate(0, 1));
            banned.add(new Cordinate(0, 2));
            banned.add(new Cordinate(0, 4));
            banned.add(new Cordinate(0, 5));
            banned.add(new Cordinate(0, 6));
            banned.add(new Cordinate(1, 0));
            banned.add(new Cordinate(1, 1));
            banned.add(new Cordinate(1, 5));
            banned.add(new Cordinate(1, 6));
            banned.add(new Cordinate(2, 0));
            banned.add(new Cordinate(2, 6));
            banned.add(new Cordinate(3, 0));
            banned.add(new Cordinate(3, 6));
            banned.add(new Cordinate(4, 0));
            banned.add(new Cordinate(4, 3));
            banned.add(new Cordinate(4, 6));
        }
        catch (InvalidCordinate e) {

        }

        return banned;
    }

    @Override
    public void drawShipboard() {
        //TODO : da completare
    }
}
