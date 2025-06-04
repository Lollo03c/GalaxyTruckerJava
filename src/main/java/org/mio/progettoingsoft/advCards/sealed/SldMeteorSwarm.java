package org.mio.progettoingsoft.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Game;
import org.mio.progettoingsoft.advCards.Meteor;
import org.mio.progettoingsoft.advCards.MeteorSwarm;

import java.util.ArrayList;
import java.util.List;

public final class SldMeteorSwarm extends SldAdvCard{
    private final List<Meteor> meteors;
    public SldMeteorSwarm(int id, int level, List<Meteor> meteors) {
        super(id, level);
        this.meteors = meteors;
    }

    public static SldMeteorSwarm loadMeteorSwarm(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        List<Meteor> meteors = new ArrayList<>();
        JsonNode meteorsNode = node.path("meteors");
        for(JsonNode meteor : meteorsNode) {
            meteors.add(Meteor.stringToMeteor(meteor.get(1).asText(),meteor.get(0).asText()));
        }

        return new SldMeteorSwarm(id, level, meteors);
    }

    @Override
    public String getCardName() {
        return "Meteor Swarm";
    }

    @Override
    public void init(Game game) {
        FlyBoard board = game.getFlyboard();

    }

    @Override
    public List<Meteor> getMeteors() {
        return meteors;
    }

    @Override
    public void finish(FlyBoard board) {

    }
}
