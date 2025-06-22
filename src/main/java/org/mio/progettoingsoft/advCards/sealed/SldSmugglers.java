package org.mio.progettoingsoft.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.advCards.Smugglers;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.exceptions.BadPlayerException;
import org.mio.progettoingsoft.exceptions.NotEnoughBatteriesException;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.model.events.GenericErrorEvent;
import org.mio.progettoingsoft.model.events.SetCardStateEvent;
import org.mio.progettoingsoft.model.events.SetStateEvent;
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

    public void applyEffect(Player player, List<Cordinate> drillsCordinate) {
        stealGoods = false;
        giverReward = false;


        if (player.equals(this.actualPlayer)) {
            double power = player.getShipBoard().getBaseFirePower();
            ShipBoard shipBoard = player.getShipBoard();

            if (drillsCordinate.size() > actualPlayer.getShipBoard().getQuantBatteries()) {
                Event event = new GenericErrorEvent(player.getNickname(), "too many double drills activated for the energy stored");
                game.addEvent(event);
            }

            for (Cordinate cordinate : drillsCordinate){
                power += shipBoard.getOptComponentByCord(cordinate).get().getFirePower(true);
            }
            shipBoard.removeEnergy(drillsCordinate.size());

            System.out.println("power "+ power + "over " + strength);
            if (power > this.strength) {
                effectTaken = true;
                flyBoard.moveDays(player, -daysLost);
                setState(CardState.GOODS_PLACEMENT);




            } else if (power < this.strength) {
                shipBoard.stoleGood(stolenGoods);
                setNextPlayer();
            } else {
                setNextPlayer();
            }
        }else{
            throw new BadPlayerException("The player " + player.getNickname() + " can't play " + this.getCardName() + " at the moment");
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
    public void setNextPlayer(){
        if (playerIterator.hasNext() && !effectTaken){
            actualPlayer = playerIterator.next();
            setState(CardState.DRILL_CHOICE);
        }
        else{
            setState(CardState.FINALIZED);
        }
    }
}
