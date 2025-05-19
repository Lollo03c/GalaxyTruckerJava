package org.mio.progettoingsoft.advCards.sealed;

import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.advCards.Meteor;

import java.util.List;

public final class SldMeteorSwarm extends SldAdvCard{
    private final List<Meteor> meteors;
    public SldMeteorSwarm(int id, int level, List<Meteor> meteors) {
        super(id, level);
        this.meteors = meteors;
    }

    @Override
    public String getCardName() {
        return "Meteor Swarm";
    }

    @Override
    public void init(FlyBoard board) {

    }

    @Override
    public void finish(FlyBoard board) {

    }
}
