package org.mio.progettoingsoft.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.advCards.Epidemic;

import java.util.HashSet;
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
    public void init(Game game){
        FlyBoard board = game.getFlyboard();
//        if(board.getState() != GameState.DRAW_CARD){
//            throw new IllegalStateException("Illegal state: " + board.getState());
//        }
        this.allowedPlayers = board.getScoreBoard();
        this.state = CardState.APPLYING;
    }

    // apply the effect of the card on all players (this card doesn't need player interaction)
    public void applyEffect(FlyBoard board){
//        for (Player player : this.allowedPlayers) {
//            Set<Component> toDoRemove = new HashSet<>();
//            // for each housing directly connected to another housing, verifies if they all contain at least a member
//            // (human/alien) and adds them to those from which one member will be removed
//            player.getShipBoard().getComponentsStream()
//                    .filter(c -> c.getType().equals(ComponentType.HOUSING))
//                    .forEach(c -> {
//                        Map<Direction, Component> adj = player.getShipBoard().getAdjacentConnected(c.getRow(), c.getColumn());
//                        adj.forEach((direction, component) -> {
//                            if (component.getType().equals(ComponentType.HOUSING) && c.getQuantityGuests() > 0 && component.getQuantityGuests() > 0) {
//                                toDoRemove.add(component);
//                            }
//                        });
//                    });
//            // removes a crew member from each selected housing
//            for (Component c : toDoRemove) {
//                c.removeGuest();
//            }
//        }
        this.state = CardState.FINALIZED;
    }

    public void finish(FlyBoard board){
        if(this.state != CardState.FINALIZED){
            throw new IllegalStateException("Illegal state for 'finish': " + this.state);
        }
//        board.setState(GameState.DRAW_CARD);
    }
}
