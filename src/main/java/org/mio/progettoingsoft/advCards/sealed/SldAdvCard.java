package org.mio.progettoingsoft.advCards.sealed;

import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;

import java.util.Iterator;
import java.util.List;

public abstract sealed class SldAdvCard permits SldAbandonedShip, SldEpidemic, SldOpenSpace, SldSlavers{
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

    public SldAdvCard(int id, int level){
        this.id = id;
        this.level = level;
    }
}

