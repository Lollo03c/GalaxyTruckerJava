package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.*;

import java.util.ArrayList;
import java.util.List;

public class MeteorSwarm extends AdventureCard {
    private final List<Meteor> meteors;

    public MeteorSwarm(int id, int level, List<Meteor> meteors) {
        super(id, level, AdvCardType.METEOR_SWARM);
        this.meteors = meteors;
    }

    public static MeteorSwarm loadMeteorSwarm(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        List<Meteor> meteors = new ArrayList<>();
        JsonNode meteorsNode = node.path("meteors");
        for(JsonNode meteor : meteorsNode) {
            meteors.add(Meteor.stringToMeteor(meteor.get(1).asText(),meteor.get(0).asText()));
        }

        return new MeteorSwarm(id, level, meteors);
    }

    @Override
    public List<Meteor> getMeteors(){
        return this.meteors;
    }

}
