package org.mio.progettoingsoft.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.advCards.CannonPenalty;
import org.mio.progettoingsoft.advCards.Pirate;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;
import org.mio.progettoingsoft.model.interfaces.GameServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class SldPirates extends SldAdvCard{
    private final int strength;
    private final int credits;
    private final List<CannonPenalty> cannons;
    private final int daysLost;

    private List<Player> penaltyPlayers = new ArrayList<>();
    private Iterator<CannonPenalty> cannonIterator;
    private CannonPenalty actualCannon;

    public SldPirates(int id, int level, int daysLost, int strength, int credits, List<CannonPenalty> cannons) {
        super(id, level);
        this.strength = strength;
        this.credits = credits;
        this.daysLost = daysLost;
        this.cannons = cannons;
    }

    public static SldPirates loadPirate(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        int strength = node.path("strength").asInt();
        int daysLost = node.path("daysLost").asInt();
        List<CannonPenalty> cannons = new ArrayList<>();
        JsonNode cannonsNode = node.path("cannons");
        for (JsonNode cannon : cannonsNode) {
            cannons.add(CannonPenalty.stringToCannonPenalty(cannon.get(1).asText(),cannon.get(0).asText()));
        }
        int reward = node.path("reward").asInt();

        return new SldPirates(id, level, daysLost, strength, reward, cannons);
    }

    @Override
    public int getDaysLost() {return daysLost;}

    @Override
    public String getCardName() {
        return "Pirates";
    }

    @Override
    public void init(GameServer game) {
        this.game = game;
        this.flyBoard = game.getFlyboard();

       allowedPlayers = new ArrayList<>(flyBoard.getPlayers());
       playerIterator = allowedPlayers.iterator();

       cannonIterator = cannons.iterator();
    }

    @Override
    public int getCredits() {return credits;}

    @Override
    public int getStrength() {
        return strength;
    }

    @Override
    public List<CannonPenalty> getCannonPenalty(){
        return cannons;
    }

    @Override
    public void finish(FlyBoard board) {

    }

    @Override
    public void setNextPlayer(){
        if (playerIterator.hasNext()){
            actualPlayer = playerIterator.next();
            setState(CardState.DRILL_CHOICE);
        }
        else{
            setNextCannon();
        }
    }

    public void setNextCannon(){
        if (cannonIterator.hasNext()){
            actualCannon = cannonIterator.next();
            setState(CardState.DICE_ROLL);
        }
        else{
            setState(CardState.FINALIZED);
        }
    }

    public void loadPower(Player player, List<Cordinate> doubleDrills){
        ShipBoard shipBoard = player.getShipBoard();
        double power = shipBoard.getBaseFirePower();

        for (Cordinate cord : doubleDrills){
            if (shipBoard.getOptComponentByCord(cord).isEmpty())
                throw new IncorrectShipBoardException("Not valid cord");
            Component comp = shipBoard.getOptComponentByCord(cord).get();

            if (comp.getFirePower(true) <= 0)
                throw new IncorrectShipBoardException("Not a drill to activate");

            power += comp.getFirePower(true);

            if (power < this.strength){
                this.penaltyPlayers.add(player);
            }
        }

    }

    public CannonPenalty getActualCannon() {
        return actualCannon;
    }

    public List<Player> getPenaltyPlayers() {
        return penaltyPlayers;
    }
}
