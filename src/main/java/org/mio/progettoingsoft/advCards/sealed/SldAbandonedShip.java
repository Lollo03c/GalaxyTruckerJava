package org.mio.progettoingsoft.advCards.sealed;

import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.StateEnum;
import org.mio.progettoingsoft.exceptions.BadPlayerException;

import javax.swing.plaf.nimbus.State;
import java.util.List;

public final class SldAbandonedShip extends SldAdvCard {
    private final int daysLost;
    private final int credits;
    private final int crewLost;

    public String getCardName(){
        return "Abandoned Ship";
    }

    public SldAbandonedShip(int id, int level, int daysLost, int credits, int crewLost) {
        super(id, level);
        this.daysLost = daysLost;
        this.credits = credits;
        this.crewLost = crewLost;
    }

    public void init(FlyBoard board) {
        if(board.getState() != StateEnum.DRAW_CARD){
            throw new IllegalStateException("Illegal state: " + board.getState());
        }
        this.state = CardState.CREW_REMOVE_CHOICE;
        board.setState(StateEnum.CARD_EFFECT);
        this.allowedPlayers = board.getScoreBoard().stream()
                .filter(player -> player.getShipBoard().getQuantityGuests() > this.crewLost)
                .toList();
        this.playerIterator = allowedPlayers.iterator();
    }

    public void applyEffect(FlyBoard board, Player player, boolean wantsToActivate, List<Integer[]> housingCordinatesList) {
        if(this.state != CardState.CREW_REMOVE_CHOICE) {
            throw new IllegalStateException("The effect can't be applied or has been already applied: " + this.state);
        }
        if(!board.getScoreBoard().contains(player)) {
            throw new BadPlayerException("The player " + player.getUsername() + " is not in the board");
        }

        this.state = CardState.APPLYING;

        if(playerIterator.hasNext()){
            Player actualPlayer = playerIterator.next();
            if(player.equals(actualPlayer)){
                if(wantsToActivate){
                    for(int i = 0; i < housingCordinatesList.size(); i++){
                        int row = housingCordinatesList.get(i)[0];
                        int col = housingCordinatesList.get(i)[1];
                        board.getPlayerByUsername(actualPlayer.getUsername()).get().getShipBoard().getComponent(row, col).removeGuest();
                    }
                    board.moveDays(board.getPlayerByUsername(actualPlayer.getUsername()).get(), -this.daysLost);
                    board.getPlayerByUsername(actualPlayer.getUsername()).get().addCredits(this.credits);
                    this.state = CardState.FINALIZED;
                }else{
                    this.state = CardState.CREW_REMOVE_CHOICE;
                }
            }else{
                throw new BadPlayerException(this.getCardName());
            }
        }
    }

    public void finish(FlyBoard board) {
        if(this.state != CardState.FINALIZED){
            throw new IllegalStateException("Illegal state for 'finish': " + this.state);
        }
        board.setState(StateEnum.DRAW_CARD);
    }
}
