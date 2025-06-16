package org.mio.progettoingsoft.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.advCards.Slaver;
import org.mio.progettoingsoft.exceptions.BadPlayerException;
import org.mio.progettoingsoft.exceptions.NotEnoughBatteriesException;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.utils.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SldSlavers extends SldAdvCard {
    private final int strength;
    private final int credits;
    private final int daysLost;
    private final int crewLost;

    @Override
    public String getCardName() {
        return "Slavers";
    }

    public static SldSlavers loadSlaver(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        int strength = node.path("strength").asInt();
        int daysLost = node.path("daysLost").asInt();
        int reward = node.path("reward").asInt();
        int crewLost = node.path("crewLost").asInt();

        return new SldSlavers(id, level, strength, daysLost, reward, crewLost);
    }

    public SldSlavers(int id, int level, int strength, int daysLost, int credits, int crewLost) {
        super(id, level);
        this.crewLost = crewLost;
        this.strength = strength;
        this.credits = credits;
        this.daysLost = daysLost;
    }

    @Override
    public int getStrength(){return strength;}

    @Override
    public int getCrewLost(){ return crewLost; }

    @Override
    public int getCredits(){ return credits; }

    @Override
    public int getDaysLost( ){return daysLost;}



    public void init(GameServer game) {;
        this.game = game;
        this.flyBoard = game.getFlyboard();
        this.allowedPlayers = flyBoard.getScoreBoard();
        this.playerIterator = allowedPlayers.iterator();
        this.state = CardState.COMPARING;
    }

    @Override
    public void setNextPlayer() {
        if (playerIterator.hasNext()) {
            actualPlayer = playerIterator.next();
            setState(CardState.COMPARING);
        }
        else{
            Logger.debug("the effect of SldSlavers is over");
            setState(CardState.FINALIZED);
        }
    }


    /* !!! Card workflow after init !!! */
    /*
    - for each player, while the card is not defeated:
        - comparePlayer:
            - if >0 the card can be defeated with no extra power: the next state is directly APPLYING
            - if <=0 the card is not defeated, the player will choose if activate double drills to defeat: the next state is DRILL_CHOICE
        - applyEffect:
            - if (with or without activation) the power is > strength the card is defeated, gives rewards and skip to finish (FINALIZED)
            - if (with or without activation) the power is = strength the card is not defeated but the player has no penalties (COMPARING for next player)
            - if (with or without activation) the power is < strength the player is defeated (CREW_REMOVE_CHOICE and removeCrew method, then COMPARING for next player)
     */

    public int comparePower(FlyBoard board, Player player) {
//        if (this.state != CardState.COMPARING) {
//            throw new IllegalStateException("Illegal state: " + this.state);
//        }
//        if (actualPlayer.equals(player)) {
//            float base = player.getShipBoard().getBaseFirePower();
//            if (base > this.strength) {
//                this.state = CardState.APPLYING;
//                return 1;
//            } else if (base < this.strength) {
//                this.state = CardState.DRILL_CHOICE;
//                return -1;
//            } else {
//                this.state = CardState.DRILL_CHOICE;
//                return 0;
//            }
//        } else {
//            throw new BadPlayerException("The player " + player.getNickname() + " can't play " + this.getCardName() + " at the moment");
//        }
        return 0;
    }

    public void applyEffect( Player player, boolean wantsToActivate, List<Cordinate> drillsCordinate) {
        if (this.state != CardState.APPLYING && this.state != CardState.DRILL_CHOICE) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }

        if (!player.equals(this.actualPlayer)) {
            throw new BadPlayerException("The player " + player.getNickname() + " can't play " + this.getCardName() + " at the moment");
        }

        double power = player.getShipBoard().getBaseFirePower();

        if (this.state == CardState.DRILL_CHOICE) {
            if (drillsCordinate.size() > actualPlayer.getShipBoard().getQuantBatteries()) {
                throw new NotEnoughBatteriesException();
            }

            for (Cordinate c : drillsCordinate) {
                Optional<Component> comp = actualPlayer.getShipBoard().getOptComponentByCord(c);
                if (comp.isPresent()) {
                    power += comp.get().getFirePower(true);
                } else {
                    throw new IllegalArgumentException("Invalid coordinate: " + c);
                }
            }

            this.state = CardState.APPLYING;
        }

        if (power > this.strength) {
            if (wantsToActivate) {
                flyBoard.moveDays(actualPlayer, -this.daysLost);
                actualPlayer.addCredits(this.credits);
            }
            this.state = CardState.FINALIZED;
        } else if (power < this.strength) {
            this.state = CardState.CREW_REMOVE_CHOICE;
        } else {
            this.setNextPlayer();
        }
    }


    public void removeCrew(FlyBoard board, Player player, List<Cordinate> housingCordinatesList) {
        if (this.state != CardState.CREW_REMOVE_CHOICE) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }

        if (!player.equals(this.actualPlayer)) {
            throw new BadPlayerException("The player " + player.getNickname() + " can't play " + this.getCardName() + " at the moment");
        }

        if (housingCordinatesList.isEmpty()) {
            throw new BadPlayerException("Empty list");
        }

        ShipBoard shipBoard = board.getPlayerByUsername(actualPlayer.getNickname()).getShipBoard();

        for (Cordinate cord : housingCordinatesList) {
            Optional<Component> compOpt = shipBoard.getOptComponentByCord(cord);
            if (compOpt.isPresent()) {
                compOpt.get().removeGuest();
            } else {
                throw new IllegalArgumentException("Invalid coordinate: " + cord);
            }
        }

        this.setNextPlayer();
    }


    private void nextPlayer(){
        if(this.playerIterator.hasNext()){
            this.actualPlayer = this.playerIterator.next();
            this.state = CardState.COMPARING;
        }else{
            this.state = CardState.FINALIZED;
        }
    }

    // removes players with no crew remaining
    public void finish(FlyBoard board) {
        if (this.state != CardState.FINALIZED) {
            throw new IllegalStateException("Illegal state for 'finish': " + this.state);
        }
        List<Player> noCrewPlayers = new ArrayList<Player>();
        for(Player p : board.getScoreBoard()){
            if(p.getShipBoard().getQuantityGuests() == 0){
                noCrewPlayers.add(p);
            }
        }
        if(!noCrewPlayers.isEmpty()){
            // here the method should call a procedure or throw an exception to remove the players with no power
            throw new RuntimeException("There's at least a player with no crew, not implemented yet");

        }
//        board.setState(GameState.DRAW_CARD);
    }

}
