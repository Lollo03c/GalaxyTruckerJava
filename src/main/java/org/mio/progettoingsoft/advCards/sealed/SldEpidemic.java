package org.mio.progettoingsoft.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.advCards.Epidemic;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.model.events.RemoveGuestEvent;
import org.mio.progettoingsoft.model.events.SetStateEvent;
import org.mio.progettoingsoft.model.interfaces.GameServer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class SldEpidemic extends SldAdvCard {
    public SldEpidemic(int id, int level) {
        super(id, level);
    }

    public String getCardName(){
        return "Epidemic";
    }

    public static SldEpidemic loadEpidemic(JsonNode node) {
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();

        return new SldEpidemic(id, level);
    }

    // all players have to play the card, so all of them are added to the list
    public void init(GameServer game){
        this.game = game;
        this.flyBoard = game.getFlyboard();

        actualPlayer = flyBoard.getScoreBoard().getFirst();

        for (Player player : flyBoard.getScoreBoard()){
            ShipBoard ship = player.getShipBoard();

            Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();
            Set<Component> toRemove = new HashSet<>();

            while (cordinateIterator.hasNext()){
                Cordinate cord = cordinateIterator.next();
                if (ship.getOptComponentByCord(cord).isEmpty())
                    continue;
                Component comp = ship.getOptComponentByCord(cord).get();

                Map<Direction, Component> ajdacents = ship.getAdjacent(cord);
                for (Direction dir : ajdacents.keySet()){
                    if (!comp.getGuests().isEmpty() && !ajdacents.get(dir).getGuests().isEmpty()){
                        toRemove.add(comp);
                        toRemove.add(ajdacents.get(dir));
                    }
                }
            }

            for (Component c : toRemove){
                c.removeGuest();

                Event removeGuestEvent = new RemoveGuestEvent(null, c.getId());
                game.addEvent(removeGuestEvent);
            }
        }

        setState(CardState.EPIDEMIC_END);
    }

    public void setNextPlayer(){
        setState(CardState.FINALIZED);
    }

}
