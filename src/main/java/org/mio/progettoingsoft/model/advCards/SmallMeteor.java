package org.mio.progettoingsoft.model.advCards;

import org.mio.progettoingsoft.model.advCards.sealed.CardState;
import org.mio.progettoingsoft.model.*;
import org.mio.progettoingsoft.model.enums.Connector;
import org.mio.progettoingsoft.model.enums.Direction;
import org.mio.progettoingsoft.model.enums.MeteorType;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.model.events.MetoriteEvent;
import org.mio.progettoingsoft.model.events.SetCardStateEvent;
import org.mio.progettoingsoft.model.interfaces.GameServer;

import java.util.Optional;

public class SmallMeteor extends Meteor {

    public SmallMeteor(Direction direction) {
        super(direction, MeteorType.SMALL);
    }
    @Override
    public String toString(){
        return "Small meteor";
    }

    @Override
    public void hit(GameServer game, Player player, int value) {


        ShipBoard shipBoard = player.getShipBoard();
        Optional<Cordinate> optCord = findHit(shipBoard, value);

        if (optCord.isEmpty()){
            Event meteoEvent = new MetoriteEvent(player.getNickname(), direction, number, type, null);
            game.addEvent(meteoEvent);
        }



        if (optCord.isPresent()){
            Event meteoEvent = new MetoriteEvent(player.getNickname(), direction, number, type, optCord.get());
            game.addEvent(meteoEvent);

            Optional<Component> optComp = player.getShipBoard().getOptComponentByCord(optCord.get());
            int idComp = optComp.get().getId();

            Component comp = game.getFlyboard().getComponentById(idComp);

            if (!comp.getConnector(direction).equals(Connector.FLAT)) {

                nickHit.add(player.getNickname());
                Event event = new SetCardStateEvent(player.getNickname(), CardState.SHIELD_SELECTION);
                game.addEvent(event);
            }
        }

    }
}