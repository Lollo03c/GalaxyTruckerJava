package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.components.GoodType;

import java.util.ArrayList;
import java.util.List;

public class Smugglers extends AdvancedEnemy {
    private final int stolenGoods;
    private final List<GoodType> goods;
    private final int strength;
    private final int daysLost;

    public Smugglers(int id, int level, int strength, int daysLost, int stolenGoods, List<GoodType> goods) {
        super(id, level, strength, daysLost, AdvCardType.SMUGGLERS);
        this.stolenGoods = stolenGoods;
        this.goods = goods;
        this.strength = strength;
        this.daysLost = daysLost;
    }
    
    public static Smugglers loadSmugglers(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        int strength = node.path("strength").asInt();
        int daysLost = node.path("daysLost").asInt();
        List<GoodType> rewards = new ArrayList<>();
        JsonNode rewardNode = node.path("reward");
        for (JsonNode reward : rewardNode) {
            rewards.add(GoodType.stringToGoodType(reward.asText()));
        }
        int goodsLost = node.path("goodsLost").asInt();

        return new Smugglers(id, level, strength, daysLost, goodsLost, rewards);
    }

    @Override
    public int getStrength(){
        return strength;
    }

    @Override
    public int getDaysLost(){
        return daysLost;
    }

    @Override
    public List<GoodType> getGoods(){
        return goods;
    }

    @Override
    public int getStolenGoods(){
        return stolenGoods;
    }

    @Override
    public void start(FlyBoard board){
        boolean ended = false;

        List<Player> playerList = new ArrayList<>(board.getScoreBoard());
        for(Player player : playerList){
            List<Component> doubleDrills = player.getView().askDoubleDrill();

            float power = player.getShipBoard().getBaseEnginePower();
            for (Component drill : doubleDrills){
                power += drill.getFirePower();
            }
            player.getShipBoard().removeEnergy(doubleDrills.size());

            if (power > strength){
                boolean answer = player.getView().askForEffect(type);

                if (answer){
                    board.moveDays(player, -daysLost);
                    for (GoodType type : goods){
                        Component depot = player.getView().askForDepotToAdd(type);
                        depot.addGood(type);
                    }
                }
                break;
            }
            else if(power < strength){
                player.getShipBoard().stoleGood();
            }
        }
    }
}
