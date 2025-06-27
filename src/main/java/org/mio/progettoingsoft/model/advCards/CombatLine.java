package org.mio.progettoingsoft.model.advCards;

import org.mio.progettoingsoft.model.Player;

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
