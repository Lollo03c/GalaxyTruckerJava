package org.mio.progettoingsoft.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.advCards.Slaver;
import org.mio.progettoingsoft.exceptions.BadPlayerException;
import org.mio.progettoingsoft.exceptions.IncorrectFlyBoardException;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;
import org.mio.progettoingsoft.exceptions.NotEnoughBatteriesException;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.model.events.RemoveGuestEvent;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.utils.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public final class SldSlavers extends SldAdvCard {
    private final int strength;
    private final int credits;
    private final int daysLost;
    private final int crewLost;

    private List<Player> lostPlayers = new ArrayList<>();
    private Iterator<Player> loserIterator;

    @Override
    public String getCardName() {
        return "Slavers";
    }

    public static SldSlavers loadSlaver(JsonNode node) {
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
    public int getStrength() {
        return strength;
    }

    @Override
    public int getCrewLost() {
        return crewLost;
    }

    @Override
    public int getCredits() {
        return credits;
    }

    @Override
    public int getDaysLost() {
        return daysLost;
    }


    public void init(GameServer game) {
        this.game = game;
        this.flyBoard = game.getFlyboard();
        this.allowedPlayers = flyBoard.getScoreBoard();
        this.playerIterator = allowedPlayers.iterator();
    }

    @Override
    public void setNextPlayer() {
        if (playerIterator.hasNext()) {
            actualPlayer = playerIterator.next();
            setState(CardState.DRILL_CHOICE);
        } else {
            loserIterator = lostPlayers.iterator();
            setNextLoser();
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

    public void applyEffect(Player player,List<Cordinate> drillsCordinate) {
        if (this.state != CardState.APPLYING && this.state != CardState.COMPARING && this.state != CardState.DRILL_CHOICE) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }

        if (!player.equals(this.actualPlayer)) {
            throw new BadPlayerException("The player " + player.getNickname() + " can't play " + this.getCardName() + " at the moment");
        }

        ShipBoard shipBoard = player.getShipBoard();
        shipBoard.removeEnergy(drillsCordinate.size());

        double power = player.getShipBoard().getBaseFirePower();
        for (Cordinate cord : drillsCordinate)
            power += shipBoard.getOptComponentByCord(cord).get().getFirePower(true);
        if (power < strength){
            lostPlayers.add(player);
            setNextPlayer();
        }
        else if (power > strength){
            actualPlayer = player;
            setState(CardState.ACCEPTATION_CHOICE);
        }
        else{
            setNextPlayer();
        }
    }

    public void skipEffect(){
        loserIterator = lostPlayers.iterator();
        setNextLoser();
    }

    public void takeCredits(){
        flyBoard.moveDays(actualPlayer, -daysLost);
        actualPlayer.addCredits(credits);

        loserIterator = lostPlayers.iterator();
        setNextLoser();
    }

    public void setNextLoser(){
        if (loserIterator.hasNext()){
            actualPlayer = loserIterator.next();
            setState(CardState.CREW_REMOVE_CHOICE);
        }
        else{
            setState(CardState.FINALIZED);
        }
    }

    public void removeCrew(String nickname, List<Cordinate> cordinates){
        if (! nickname.equals(actualPlayer.getNickname())){
            throw new IncorrectFlyBoardException("");
        }

        for (Cordinate cord : cordinates){
            int idComp = actualPlayer.getShipBoard().getOptComponentByCord(cord).get().getId();
            flyBoard.getComponentById(idComp).removeGuest();

        }

        for (Cordinate cord : cordinates){
            int idComp = actualPlayer.getShipBoard().getOptComponentByCord(cord).get().getId();

            Event event = new RemoveGuestEvent(nickname, idComp);
            game.addEvent(event);
        }

        setNextLoser();

    }
}
