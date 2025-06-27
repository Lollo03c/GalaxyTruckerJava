package org.mio.progettoingsoft.model.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.model.components.GoodType;
import org.mio.progettoingsoft.exceptions.BadPlayerException;
import org.mio.progettoingsoft.model.Cordinate;
import org.mio.progettoingsoft.model.Player;
import org.mio.progettoingsoft.model.ShipBoard;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.model.events.GenericErrorEvent;
import org.mio.progettoingsoft.model.interfaces.GameServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a "Smugglers" adventure card in the game.
 * This card type involves players encountering smugglers, requiring them to
 * overcome a certain strength value using their ship's fire power.
 * Success leads to rewards (days lost, meaning progress, and potentially goods),
 * while failure results in goods being stolen from the player.
 * It extends {@link SldAdvCard} and provides specific logic for smuggler encounters.
 */
public final class SldSmugglers extends SldAdvCard {
    private final int stolenGoods;
    private final List<GoodType> goods;
    private final int strength;
    private final int daysLost;

    private boolean effectTaken = false;
    private boolean giverReward = false;
    private boolean stealGoods = false;

    /**
     * Constructs a new {@code SldSmugglers} adventure card.
     *
     * @param id The unique identifier for this smuggler card.
     * @param level The level of the smuggler encounter.
     * @param stolenGoods The number of goods stolen as a penalty.
     * @param goods A list of {@link GoodType} representing the goods that can be rewarded.
     * @param strength The combat strength of the smugglers.
     * @param daysLost The number of days lost as a reward.
     */
    public SldSmugglers(int id, int level, int stolenGoods, List<GoodType> goods, int strength, int daysLost) {
        super(id, level);
        this.stolenGoods = stolenGoods;
        this.goods = goods;
        this.strength = strength;
        this.daysLost = daysLost;
    }

    /**
     * Overrides the base method to return the strength of the smugglers on this card.
     *
     * @return The strength value.
     */
    @Override
    public int getStrength() {
        return strength;
    }

    /**
     * Overrides the base method to return the number of days lost associated with this card.
     * This is part of the reward for successfully overcoming the smugglers.
     *
     * @return The number of days lost.
     */
    @Override
    public int getDaysLost() {return daysLost;}

    /**
     * Overrides the base method to return the list of goods that can be rewarded by this card.
     *
     * @return A list of {@link GoodType} objects.
     */
    @Override
    public List<GoodType> getGoods() { return goods; }

    /**
     * Overrides the base method to return the number of goods stolen as a penalty by this card.
     *
     * @return The number of goods stolen.
     */
    @Override
    public int getStolenGoods(){ return stolenGoods; }

    /**
     * Overrides the base method to return the name of this card.
     *
     * @return The string "Smugglers".
     */
    @Override
    public String getCardName() {
        return "Smugglers";
    }

    /**
     * Static factory method to load an {@code SldSmugglers} object from a JSON node.
     * It parses the node to extract ID, level, strength, days lost, rewards (goods types),
     * and goods lost (stolen goods), then constructs a new {@code SldSmugglers} instance.
     *
     * @param node The {@link JsonNode} containing the Smugglers card data.
     * @return A new {@code SldSmugglers} instance populated with data from the JSON node.
     */
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

    /**
     * Initializes the Smugglers card's state and context within the game.
     * Sets up game and fly board references, and initializes the list of allowed players
     * (all players on the fly board) and the player iterator.
     * The flags {@code effectTaken}, {@code giverReward}, and {@code stealGoods} are reset.
     *
     * @param game The {@link GameServer} instance managing the current game.
     */
    @Override
    public void init(GameServer game) {
        this.game = game;
        this.flyBoard = game.getFlyboard();

        allowedPlayers = flyBoard.getScoreBoard();
        this.playerIterator = allowedPlayers.iterator();
    }

//    public int comparePower(FlyBoard board, Player player) {
//        if (this.state != CardState.COMPARING) {
//            throw new IllegalStateException("Illegal state: " + this.state);
//        }
//        if (actualPlayer.equals(player)) {
//            double base = player.getShipBoard().getBaseFirePower();
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
//
//    }

    /**
     * Applies the effect of the Smugglers card based on the player's provided drill power.
     * Players activate drills to increase their fire power and compare it against the smugglers' strength.
     * <ul>
     * <li>If power is greater than strength: The player gains a reward (days lost for progress),
     * and the card transitions to {@code GOODS_PLACEMENT} state to allow for placing goods.
     * The {@code effectTaken} flag is set to true, preventing other players from getting this reward.</li>
     * <li>If power is less than strength: The player has a specified number of goods stolen.</li>
     * <li>If power is equal to strength: No specific effect (neither reward nor penalty) for this player.</li>
     * </ul>
     *
     * @param player The {@link Player} applying the effect.
     * @param drillsCordinate A list of {@link Cordinate} representing the drills activated by the player.
     * @throws BadPlayerException if the provided player is not the current active player.
     */
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

            for (Cordinate cordinate : drillsCordinate) {
                power += shipBoard.getOptComponentByCord(cordinate).get().getFirePower(true);
            }
            shipBoard.removeEnergy(drillsCordinate.size());

            System.out.println("power " + power + "over " + strength);
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
        } else {
            throw new BadPlayerException("The player " + player.getNickname() + " can't play " + this.getCardName() + " at the moment");
        }
    }

    /**
     * Sets the next player to confront the smugglers or finalizes the card's effect.
     * The card continues processing players one by one until {@code effectTaken} is true
     * (meaning a player has successfully overcome the smugglers and claimed the reward),
     * or all players have had their turn.
     */
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
