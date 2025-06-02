package org.mio.progettoingsoft.advCards.sealed;

import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Game;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SldStardust extends SldAdvCard{
    public SldStardust(int level, int id) {
        super(level, id);
    }
    @Override
    public String getCardName() {
        return "Stardust";
    }

    @Override
    public void init(Game game) {
        FlyBoard board = game.getFlyboard();
//        if(board.getState() != GameState.DRAW_CARD){
//            throw new IllegalStateException("Illegal state: " + board.getState());
//        }
//        board.setState(GameState.CARD_EFFECT);
        this.state = CardState.APPLYING;
    }

    public void applyEffect(FlyBoard board) {
        if(this.state != CardState.APPLYING){
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        List<Player> playersReverse = new ArrayList<>(board.getScoreBoard());
        Collections.reverse(playersReverse);
        for (Player player : playersReverse) {
            int daysLost = player.getShipBoard().getExposedConnectors();
            board.moveDays(player, -daysLost);
        }
        this.state = CardState.FINALIZED;
    }

    @Override
    public void finish(FlyBoard board) {
        if(this.state != CardState.FINALIZED){
            throw new IllegalStateException("Illegal state: " + this.state);
        }
//        board.setState(GameState.DRAW_CARD);
    }



}
