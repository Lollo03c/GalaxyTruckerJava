package org.mio.progettoingsoft.advCards;

import java.util.List;

public class CombatLine {
    private final Criterion criterion;
    private final List<Penalty> penalties;

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
