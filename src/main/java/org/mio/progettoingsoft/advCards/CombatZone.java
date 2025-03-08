package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.AdventureCard;

public class CombatZone extends AdventureCard {
    private final CombatLine[] lines;

    protected CombatZone(int id, int level, CombatLine[] lines) {
        super(id, level);
        this.lines = lines;
    }
}
