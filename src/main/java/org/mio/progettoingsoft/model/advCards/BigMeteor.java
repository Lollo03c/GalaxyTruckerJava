package org.mio.progettoingsoft.model.advCards;

import org.mio.progettoingsoft.model.advCards.sealed.CardState;
import org.mio.progettoingsoft.model.Cordinate;
import org.mio.progettoingsoft.model.enums.Direction;
import org.mio.progettoingsoft.model.Player;
import org.mio.progettoingsoft.model.ShipBoard;
import org.mio.progettoingsoft.model.enums.MeteorType;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.model.events.MetoriteEvent;
import org.mio.progettoingsoft.model.events.SetCardStateEvent;
import org.mio.progettoingsoft.model.interfaces.GameServer;

import java.util.Optional;

public class BigMeteor extends Meteor {

    public BigMeteor(Direction direction) {
        super(direction, MeteorType.BIG);
    }

    @Override
    public void hit(GameServer game, Player player, int value) {

        ShipBoard shipBoard = player.getShipBoard();
        Optional<Cordinate> optCord = findHit(shipBoard, value);

        if (optCord.isPresent()){
            Event meteoEvent = new MetoriteEvent(player.getNickname(), direction, number, type, optCord.get());
            game.addEvent(meteoEvent);
        }
        else{
            Event meteoEvent = new MetoriteEvent(player.getNickname(), direction, number, type, null);
            game.addEvent(meteoEvent);
        }

        if (optCord.isPresent()){
            nickHit.add(player.getNickname());

            Event event = new SetCardStateEvent(player.getNickname(), CardState.ASK_ONE_DOUBLE_DRILL);
            game.addEvent(event);
        }
    }

    @Override
    public String toString(){
        return "Big meteor";
    }
}
