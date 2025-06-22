package org.mio.progettoingsoft.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.advCards.AbandonedShip;
import org.mio.progettoingsoft.exceptions.BadParameterException;
import org.mio.progettoingsoft.exceptions.BadPlayerException;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.client.VirtualClient;

import java.util.List;

public final class SldAbandonedShip extends SldAdvCard {
    private final int daysLost;
    private final int credits;
    private final int crewLost;

    private boolean effectTaken = false;

    public String getCardName() {
        return "Abandoned Ship";
    }

    public SldAbandonedShip(int id, int level, int daysLost, int credits, int crewLost) {
        super(id, level);
        this.daysLost = daysLost;
        this.credits = credits;
        this.crewLost = crewLost;
    }

    @Override
    public int getCrewLost() {
        return crewLost;
    }

    @Override
    public int getDaysLost() {
        return daysLost;
    }

    @Override
    public int getCredits() {return credits;}

    public static SldAbandonedShip loadAbandonedShip(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        int daysLost = node.path("daysLost").asInt();
        int credits = node.path("credits").asInt();
        int crewLost = node.path("crewLost").asInt();

        return new SldAbandonedShip(id, level, daysLost, credits, crewLost);
    }

    // it initializes the list of players that can play the card (crew > crewLost) and set the card state CREW_REMOVE_CHOICE
    public void init(GameServer game) {
        FlyBoard board = game.getFlyboard();

        this.game = game;
        this.flyBoard = game.getFlyboard();

        this.allowedPlayers = board.getScoreBoard().stream()
                .filter(player -> player.getShipBoard().getQuantityGuests() >= this.crewLost)
                .toList();

        playerIterator = allowedPlayers.iterator();
        effectTaken = false;
    }

    // must be called after the init with the right player
    // if the player wants to apply the effect, it removes the crew, moves the player and adds credits, after that this method must not be called
    // else, it does nothing, and it's ready for another call with the next player
    public void applyEffect(String nickname, boolean wantsToActivate, List<Cordinate> housingCordinatesList) {
        if (! nickname.equals(actualPlayer.getNickname())) {
            throw new BadPlayerException("Not " + nickname + " turn to play");
        }


        this.state = CardState.APPLYING;
        Player player = flyBoard.getPlayerByUsername(nickname);

        if (player.equals(actualPlayer)) {
            if (wantsToActivate) {
                if (housingCordinatesList == null) {
                    throw new BadParameterException("List is null");
                }
                if (housingCordinatesList.isEmpty()) {
                    throw new BadParameterException("List is empty");
                }
                if (housingCordinatesList.size() != this.crewLost) {
                    throw new BadParameterException("List has wrong size");
                }

                for (Cordinate cord : housingCordinatesList) {
                    flyBoard.getPlayerByUsername(actualPlayer.getNickname()).getShipBoard().getOptComponentByCord(cord).get().removeGuest();
                }
                flyBoard.moveDays(flyBoard.getPlayerByUsername(actualPlayer.getNickname()), -this.daysLost);
                flyBoard.getPlayerByUsername(actualPlayer.getNickname()).addCredits(this.credits);

                effectTaken = true;

            } else {
                return;
            }
        } else {
            throw new BadPlayerException(this.getCardName());
        }
    }

    public void finish(FlyBoard board) {
        if (this.state != CardState.FINALIZED) {
            throw new IllegalStateException("Illegal state for 'finish': " + this.state);
        }
//        board.setState(GameState.DRAW_CARD);
    }

    @Override
    public void setNextPlayer() {
        if (playerIterator.hasNext() && !effectTaken) {
            this.actualPlayer = this.playerIterator.next();
            setState(CardState.CREW_REMOVE_CHOICE);
        } else {
            setState(CardState.FINALIZED);
        }
    }

}
