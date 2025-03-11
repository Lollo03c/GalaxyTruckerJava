package org.mio.progettoingsoft.advCards;

import java.util.List;

public class CombatLine {
    private Criterion criterion;
    private List<Penalty> penalties;

    public CombatLine(Criterion criterion, List<Penalty> penalties) {
        this.criterion = criterion;
        this.penalties = penalties;
    }
}
