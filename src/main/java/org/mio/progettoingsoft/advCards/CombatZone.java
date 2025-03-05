package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.AdventureCard;

public class CombatZone extends AdventureCard {
    private final CombatLine[] lines;

    protected CombatZone(int level, CombatLine[] lines) {
        super(level);
        this.lines = lines;
    }
}
