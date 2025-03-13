package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.Direction;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.ShipBoard;

import java.util.Map;

public class SmallMeteor extends Meteor {

    public SmallMeteor(Direction direction) {
        super(direction);
    }

    @Override
    public void hit(Player player, int row, int col) {
        ShipBoard board = player.getShipBoard();
        Map<Direction, Component> adjacent = board.getAdjacent(row, col);
        if(board.isExposed(player.getShipBoard().getComponent(row, col), adjacent, this.direction)){
            //da capire come chiedere al player se vuole utilizzare lo scudo
        }

    }
}