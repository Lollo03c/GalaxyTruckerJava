package org.mio.progettoingsoft.advCards.sealed;

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

    public SldAdvCard(int id, int level){
        this.id = id;
        this.level = level;
    }
}

