package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.AdvCardType;
import org.mio.progettoingsoft.AdventureCard;

import java.util.ArrayList;
import java.util.List;

public class MeteorSwarm extends AdventureCard {
    private final List<Meteor> meteors;

    public MeteorSwarm(int id, int level, List<Meteor> meteors) {
        super(id, level, AdvCardType.METEOR_SWARM);
        this.meteors = meteors;
    }

    public List<Meteor> getMeteors(){
        return this.meteors;
    }
}
