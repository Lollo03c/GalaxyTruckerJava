package org.mio.progettoingsoft.advCards.sealed;

import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.exceptions.BadPlayerException;
import org.mio.progettoingsoft.exceptions.NotEnoughBatteriesException;

import java.util.ArrayList;
import java.util.List;

public final class SldSlavers extends SldAdvCard {
    private final int strength;
    private final int credits;
    private final int daysLost;

    @Override
    public String getCardName() {
        return "Slavers";
    }

    public SldSlavers(int id, int level, int strength, int credits, int daysLost) {
        super(id, level);
        this.strength = strength;
        this.credits = credits;
        this.daysLost = daysLost;
    }

    public void init(FlyBoard board) {
        if (board.getState() != StateEnum.DRAW_CARD) {
            throw new IllegalStateException("Illegal state: " + board.getState());
        }
        this.allowedPlayers = board.getScoreBoard();
        this.playerIterator = allowedPlayers.iterator();
        if (this.playerIterator.hasNext()) {
            this.actualPlayer = this.playerIterator.next();
        } else {
            throw new BadPlayerException("No players");
        }
        this.state = CardState.COMPARING;
    }

    /* !!! Card workflow after init !!! */
    /*
    - for each player, while the card is not defeated:
        - comparePlayer:
            - if >0 the card can be defeated with no extra power: the next state is directly APPLYING
            - if <=0 the card is not defeated, the player will choose if activate double drills to defeat: the next state id DRILL_CHOICE
        - applyEffect:
            - if (with or without activation) the power is > strength the card is defeated, gives rewards and skip to finish (FINALIZED)
            - if (with or without activation) the power is = strength the card is not defeated but the player has no penalties (COMPARING for next player)
            - if (with or without activation) the power is < strength the player is defeated (CREW_REMOVE_CHOICE and removeCrew method, then COMPARING for next player)
     */

    public int comparePower(FlyBoard board, Player player) {
        if (this.state != CardState.COMPARING) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        if (actualPlayer.equals(player)) {
            float base = player.getShipBoard().getBaseFirePower();
            if (base > this.strength) {
                this.state = CardState.APPLYING;
                return 1;
            } else if (base < this.strength) {
                this.state = CardState.DRILL_CHOICE;
                return -1;
            } else {
                this.state = CardState.DRILL_CHOICE;
                return 0;
            }
        } else {
            throw new BadPlayerException("The player " + player.getUsername() + " can't play " + this.getCardName() + " at the moment");
        }

    }

    public void applyEffect(FlyBoard board, Player player, boolean wantsToActivate, List<Integer[]> coordinatesDoubleToActivate) {
        if (this.state != CardState.APPLYING && this.state != CardState.DRILL_CHOICE) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        if (player.equals(this.actualPlayer)) {
            float power = player.getShipBoard().getBaseFirePower();
            if (this.state == CardState.DRILL_CHOICE) {
                if (coordinatesDoubleToActivate.size() > actualPlayer.getShipBoard().getQuantBatteries()) {
                    throw new NotEnoughBatteriesException();
                }
                for (int i = 0; i < coordinatesDoubleToActivate.size(); i++) {
                    int row = coordinatesDoubleToActivate.get(i)[0];
                    int col = coordinatesDoubleToActivate.get(i)[1];
                    power += actualPlayer.getShipBoard().getComponent(row, col).getFirePower();
                }
                this.state = CardState.APPLYING;
            }
            if (power > this.strength) {
                if (wantsToActivate) {
                    board.moveDays(actualPlayer, -this.daysLost);
                    actualPlayer.addCredits(this.credits);
                }
                this.state = CardState.FINALIZED;
            } else if (power < this.strength) {
                this.state = CardState.CREW_REMOVE_CHOICE;
            } else {
                this.nextPlayer();
            }
        }else{
            throw new BadPlayerException("The player " + player.getUsername() + " can't play " + this.getCardName() + " at the moment");
        }
    }

    public void removeCrew(FlyBoard board, Player player, List<Integer[]> housingCordinatesList) {
        if(this.state != CardState.CREW_REMOVE_CHOICE){
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        if (player.equals(this.actualPlayer)) {
            if(!housingCordinatesList.isEmpty()){
                for(int i = 0; i < housingCordinatesList.size(); i++){
                    int row = housingCordinatesList.get(i)[0];
                    int col = housingCordinatesList.get(i)[1];
                    board.getPlayerByUsername(actualPlayer.getUsername()).get().getShipBoard().getComponent(row, col).removeGuest();
                }
                this.nextPlayer();
            }else{
                throw new BadPlayerException("Empty list");
            }
        }else{
            throw new BadPlayerException("The player " + player.getUsername() + " can't play " + this.getCardName() + " at the moment");
        }

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
        board.setState(StateEnum.DRAW_CARD);
    }

}
