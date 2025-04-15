package org.mio.progettoingsoft.advCards.sealed;

import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.StateEnum;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.exceptions.BadPlayerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SldAbandonedStation extends SldAdvCard {
    private final int daysLost;
    private final int crewNeeded;
    private final List<GoodType> goods;

    public SldAbandonedStation(int id, int level, int daysLost, int crewNeeded, List<GoodType> goods) {
        super(id, level);
        this.daysLost = daysLost;
        this.crewNeeded = crewNeeded;
        this.goods = goods;
    }

    @Override
    public String getCardName() {
        return "Abandoned Station";
    }

    @Override
    public void init(FlyBoard board) {
        if(board.getState() != StateEnum.DRAW_CARD){
            throw new IllegalStateException("Illegal state: " + board.getState());
        }
        allowedPlayers = board.getScoreBoard().stream()
                .filter(player -> player.getShipBoard().getQuantityGuests() >= crewNeeded)
                .toList();
        playerIterator = allowedPlayers.iterator();
        board.setState(StateEnum.CARD_EFFECT);
        this.state = CardState.ACCEPTATION_CHOICE;
    }

    public List<GoodType> applyEffect(FlyBoard board, Player player, boolean wantsToApply){
        if(this.state != CardState.ACCEPTATION_CHOICE){
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        this.state = CardState.APPLYING;
        if(playerIterator.hasNext()){
            actualPlayer = playerIterator.next();
            if(actualPlayer.equals(player)){
                if(wantsToApply){
                    board.moveDays(actualPlayer, -daysLost);
                    this.state = CardState.GOODS_PLACEMENT;
                    return new ArrayList<>(goods);
                }else{
                    this.state = CardState.ACCEPTATION_CHOICE;
                    return new ArrayList<>(Collections.emptyList());
                }
            }else{
                throw new BadPlayerException("The player " + actualPlayer.getUsername() + " can't play " + this.getCardName() + " at the moment");
            }
        }else{
            this.state = CardState.FINALIZED;
            return new ArrayList<>(Collections.emptyList());
        }
    }

    public void goodsPlaced(Player player){
        if(this.state != CardState.GOODS_PLACEMENT){
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        if(player.equals(actualPlayer)){
            this.state = CardState.FINALIZED;
        }else{
            throw new BadPlayerException("The player " + actualPlayer.getUsername() + " can't confirm goods placement");

        }
    }

    @Override
    public void finish(FlyBoard board) {
        if(this.state != CardState.FINALIZED){
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        board.setState(StateEnum.DRAW_CARD);
    }
}
