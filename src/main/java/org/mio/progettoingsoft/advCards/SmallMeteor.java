package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SmallMeteor extends Meteor {

    public SmallMeteor(Direction direction) {
        super(direction);
    }

    @Override
    public void hit(Player player, int value) {
//        ShipBoard board = player.getShipBoard();
//        Map<Direction, Component> adjacent = board.getAdjacent(row, col);
//        if(board.isExposed(player.getShipBoard().getComponent(row, col), adjacent, this.direction)){
//            //da capire come chiedere al player se vuole utilizzare lo scudo
//        }
        Optional<Component> hitComponent = findHit(player, value);
        ShipBoard shipBoard = player.getShipBoard();

        if (hitComponent.isEmpty())
            return;

        Component comp = hitComponent.get();
        if (comp.getConnector(direction).equals(Connector.FLAT))
            return;


        boolean activateShield = player.getView().askShield(direction);

        if (activateShield){
            shipBoard.removeEnergy();
        }
        else{
            shipBoard.removeComponent(comp);
        }

    }
}