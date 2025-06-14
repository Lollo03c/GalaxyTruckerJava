package org.mio.progettoingsoft.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.advCards.Smugglers;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.exceptions.BadPlayerException;
import org.mio.progettoingsoft.exceptions.NotEnoughBatteriesException;
import org.mio.progettoingsoft.model.interfaces.GameServer;

import java.util.ArrayList;
import java.util.List;

public final class SldSmugglers extends SldAdvCard {
    private final int stolenGoods;
    private final List<GoodType> goods;
    private final int strength;
    private final int daysLost;

    private boolean effectTaken = false;
    private boolean giverReward = false;
    private boolean stealGoods = false;

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
    public void init(GameServer game) {
        this.game = game;
        this.flyBoard = game.getFlyboard();

        allowedPlayers = flyBoard.getScoreBoard();
        this.playerIterator = allowedPlayers.iterator();
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

    public void applyEffect(Player player, boolean wantsToActivate, List<Cordinate> drillsCordinate) {
        stealGoods = false;
        giverReward = false;

        if (!wantsToActivate)
            return;

        if (player.equals(this.actualPlayer)) {
            double power = player.getShipBoard().getBaseFirePower();
            ShipBoard shipBoard = player.getShipBoard();

            if (drillsCordinate.size() > actualPlayer.getShipBoard().getQuantBatteries()) {
                throw new NotEnoughBatteriesException();
            }

            for (Cordinate cordinate : drillsCordinate){
                power += shipBoard.getOptComponentByCord(cordinate).get().getFirePower(true);
            }

            if (power > this.strength) {
                giverReward = true;
                flyBoard.moveDays(player, -daysLost);
                effectTaken = true;

            } else if (power < this.strength) {
                stealGoods = true;
            } else {

            }
        }else{
            throw new BadPlayerException("The player " + player.getNickname() + " can't play " + this.getCardName() + " at the moment");
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

    @Override
    public void setNextPlayer(){
        if (playerIterator.hasNext() && !effectTaken){
            actualPlayer = playerIterator.next();
            setState(CardState.DRILL_CHOICE);
        }
        else{
            setState(CardState.FINALIZED);
        }
    }

    public boolean isGiverReward() {
        return giverReward;
    }

    public boolean isStealGoods() {
        return stealGoods;
    }
}
