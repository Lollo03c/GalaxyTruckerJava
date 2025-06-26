package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Game;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.advCards.sealed.CardState;
import org.mio.progettoingsoft.advCards.sealed.SldCombatZone;
import org.mio.progettoingsoft.components.Drill;
import org.mio.progettoingsoft.model.events.CannonHitEvent;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.model.events.SetCardStateEvent;
import org.mio.progettoingsoft.model.interfaces.GameServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CombatLine {
    private final Criterion criterion;
    private final List<Penalty> penalties;

    private Map<Player, Double> toSelectDouble;
    private List<Player> toAsk;
    private Iterator<Player> playerIterator;
    private Player actualPlayer;

    private Iterator<Penalty> penaltyIterator;
    private Penalty actualPenalty;

    public CombatLine(Criterion criterion, List<Penalty> penalties) {
        this.criterion = criterion;
        this.penalties = penalties;
    }

    public Criterion getCriterion() {
        return criterion;
    }

    public List<Penalty> getPenalties() {
        return penalties;
    }
}
