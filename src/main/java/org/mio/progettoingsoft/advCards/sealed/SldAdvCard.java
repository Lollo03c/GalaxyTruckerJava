package org.mio.progettoingsoft.advCards.sealed;

import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.exceptions.BadPlayerException;

import java.util.Iterator;
import java.util.List;

public abstract sealed class SldAdvCard permits SldAbandonedShip, SldEpidemic, SldOpenSpace, SldSlavers, SldAbandonedStation, SldCombatZone, SldStardust, SldSmugglers, SldPlanets, SldPirates, SldMeteorSwarm{
    private final int level;
    private final int id;
    protected List<Player> allowedPlayers;
    protected Player actualPlayer;
    protected Iterator<Player> playerIterator;
    protected CardState state;

    public abstract String getCardName();

    // this method must be called right after drawing the card, it sets the game state CARD_EFFECT
    public abstract void init(FlyBoard board);

    // this method must be called right after applying the effect of the card on ALL the available players, it sets the game state DRAW_CARD
    public abstract void finish(FlyBoard board);

    public CardState getState() {return state;}

    public Player getActualPlayer() {return actualPlayer;}

    public void keepShipPart(FlyBoard board, Player player, int row, int col) {
        if(this.state != CardState.PART_CHOICE){
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        if(actualPlayer.equals(player)){
            actualPlayer.getShipBoard().keepPart(row, col);
            this.state = CardState.PART_REMOVING_DONE;
        }else{
            throw new BadPlayerException("Illegal player: " + player);
        }
    }

    public SldAdvCard(int id, int level){
        this.id = id;
        this.level = level;
        this.state = CardState.IDLE;
    }
}

