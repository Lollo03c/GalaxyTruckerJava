package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.AdventureCard;

import java.util.ArrayList;
import java.util.List;

public class MeteorSwarm extends AdventureCard {
    private final List<Meteor> meteors;

    protected MeteorSwarm(int id, int level, List<Meteor> meteors) {
        super(id, level);
        this.meteors = meteors;
    }
}
