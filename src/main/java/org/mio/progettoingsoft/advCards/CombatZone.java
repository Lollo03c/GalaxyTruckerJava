package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.AdvCardType;
import org.mio.progettoingsoft.AdventureCard;

import java.util.List;

public class CombatZone extends AdventureCard {
    private final List<CombatLine> lines;

    public CombatZone(int id, int level, List<CombatLine> lines) {
        super(id, level, AdvCardType.COMBAT_ZONE);
        this.lines = lines;
    }
}
