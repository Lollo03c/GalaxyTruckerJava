package org.mio.progettoingsoft.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.advCards.Meteor;
import org.mio.progettoingsoft.exceptions.IncorrectFlyBoardException;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.utils.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class SldMeteorSwarm extends SldAdvCard{
    private final List<Meteor> meteors;
    private Iterator<Meteor> meteorIterator;
    private Meteor actualMeteor;

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
    public void init(GameServer game) {
        this.game  = game;
        this.flyBoard = game.getFlyboard();

        allowedPlayers = new ArrayList<>(flyBoard.getScoreBoard());
        playerIterator = allowedPlayers.iterator();
        actualPlayer = playerIterator.next();

        meteorIterator = meteors.iterator();
    }

    @Override
    public List<Meteor> getMeteors() {
        return meteors;
    }


    public synchronized void setNextMeteor(){
        if (meteorIterator.hasNext()){
            actualMeteor = meteorIterator.next();
            setState(CardState.DICE_ROLL);
        }
        else{
            setState(CardState.FINALIZED);
        }
    }

    public Meteor getActualMeteor(){
        return actualMeteor;
    }

    public synchronized void setNextMeteor(String nick, boolean destroyed,  boolean energy){
        List<String> nicknames = flyBoard.getPlayers().stream().map(p -> p.getNickname()).toList();
        if (!nicknames.contains(nick)){
            Logger.error("eccezion");
            throw new IncorrectFlyBoardException("no player found");
        }


        if (actualMeteor.getPlayerResponses().contains(nick))
            throw new IncorrectFlyBoardException("player has already answered");

        if (energy){
            flyBoard.getPlayerByUsername(nick).getShipBoard().removeEnergy(1);
        }

        if (destroyed){
            actualMeteor.destroy(flyBoard.getPlayerByUsername(nick), game);
        }

        actualMeteor.getNickHit().remove(nick);
        if (actualMeteor.getNickHit().isEmpty())
            setNextMeteor();

    }
}
