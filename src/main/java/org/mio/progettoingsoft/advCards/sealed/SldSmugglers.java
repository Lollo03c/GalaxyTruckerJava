package org.mio.progettoingsoft.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Game;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.advCards.Smugglers;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.exceptions.BadPlayerException;
import org.mio.progettoingsoft.exceptions.NotEnoughBatteriesException;

import java.util.ArrayList;
import java.util.List;

public final class SldSmugglers extends SldAdvCard {
    private final int stolenGoods;
    private final List<GoodType> goods;
    private final int strength;
    private final int daysLost;

    public SldSmugglers(int id, int level, int stolenGoods, List<GoodType> goods, int strength, int daysLost) {
        super(id, level);
        this.stolenGoods = stolenGoods;
        this.goods = goods;
        this.strength = strength;
        this.daysLost = daysLost;
    }

    @Override
    public int getStrength() {
        return strength;
    }

    @Override
    public int getDaysLost() {return daysLost;}

    @Override
    public List<GoodType> getGoods() { return goods; }

    @Override
    public int getStolenGoods(){ return stolenGoods; }

    @Override
    public String getCardName() {
        return "Smugglers";
    }

    @Override
    public void init(Game game) {
        FlyBoard board = game.getFlyboard();
//        if (board.getState() != GameState.DRAW_CARD) {
//            throw new IllegalStateException("Illegal state: " + board.getState());
//        }
        allowedPlayers = board.getScoreBoard();
        this.playerIterator = allowedPlayers.iterator();
        if (this.playerIterator.hasNext()) {
            this.actualPlayer = this.playerIterator.next();
        } else {
            throw new RuntimeException("No allowed players");
        }
        this.state = CardState.COMPARING;
    }

    public int comparePower(FlyBoard board, Player player) {
        if (this.state != CardState.COMPARING) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        if (actualPlayer.equals(player)) {
            double base = player.getShipBoard().getBaseFirePower();
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
            throw new BadPlayerException("The player " + player.getNickname() + " can't play " + this.getCardName() + " at the moment");
        }

    }

    public void applyEffect(FlyBoard board, Player player, boolean wantsToActivate, List<Integer[]> coordinatesDoubleToActivate) {
        if (this.state != CardState.APPLYING && this.state != CardState.DRILL_CHOICE) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        if (player.equals(this.actualPlayer)) {
            double power = player.getShipBoard().getBaseFirePower();
            if (this.state == CardState.DRILL_CHOICE) {
                if (coordinatesDoubleToActivate.size() > actualPlayer.getShipBoard().getQuantBatteries()) {
                    throw new NotEnoughBatteriesException();
                }
                for (Integer[] integers : coordinatesDoubleToActivate) {
                    int row = integers[0];
                    int col = integers[1];
//                    power += actualPlayer.getShipBoard().getComponent(row, col).getFirePower();
                }
                this.state = CardState.APPLYING;
            }
            if (power > this.strength) {
                if (wantsToActivate) {
                    board.moveDays(actualPlayer, -this.daysLost);
                    actualPlayer.giveGoods(goods);
                    this.state = CardState.GOODS_PLACEMENT;
                }else{
                    this.state = CardState.FINALIZED;
                }

            } else if (power < this.strength) {
//                actualPlayer.getShipBoard().removeGoods(this.stolenGoods);
                this.nextPlayer();
            } else {
                this.nextPlayer();
            }
        }else{
            throw new BadPlayerException("The player " + player.getNickname() + " can't play " + this.getCardName() + " at the moment");
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

    public void goodPlaced(FlyBoard board, Player player) {
        if(this.state != CardState.GOODS_PLACEMENT){
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        if (player.equals(this.actualPlayer)) {
            this.state = CardState.FINALIZED;
        }else{
            throw new BadPlayerException("The player " + player.getNickname() + " cannot confirm goods placement at the moment");
        }
    }

    public static SldSmugglers loadSmugglers(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        int strength = node.path("strength").asInt();
        int daysLost = node.path("daysLost").asInt();
        List<GoodType> rewards = new ArrayList<>();
        JsonNode rewardNode = node.path("reward");
        for (JsonNode reward : rewardNode) {
            rewards.add(GoodType.stringToGoodType(reward.asText()));
        }
        int stolenGoods = node.path("goodsLost").asInt();

        return new SldSmugglers(id, level, stolenGoods, rewards, strength, daysLost);
    }

    @Override
    public void finish(FlyBoard board) {
        if(this.state != CardState.FINALIZED){
            throw new IllegalStateException("Illegal state: " + this.state);
        }
//        board.setState(GameState.DRAW_CARD);
    }
}
