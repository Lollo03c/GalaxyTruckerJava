package org.mio.progettoingsoft.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Game;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.GameState;

import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.exceptions.BadPlayerException;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.model.events.SetCardStateEvent;
import org.mio.progettoingsoft.model.events.SetStateEvent;
import org.mio.progettoingsoft.model.interfaces.GameServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SldAbandonedStation extends SldAdvCard {
    private final int daysLost;
    private final int crewNeeded;
    private final List<GoodType> goods;

    private boolean effectTaken = false;

    public SldAbandonedStation(int id, int level, int daysLost, int crewNeeded, List<GoodType> goods) {
        super(id, level);
        this.daysLost = daysLost;
        this.crewNeeded = crewNeeded;
        this.goods = goods;
    }

    public static SldAbandonedStation loadAbandonedStation(JsonNode node) {
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        int daysLost = node.path("daysLost").asInt();
        int crewNeeded = node.path("crewNeeded").asInt();
        List<GoodType> goods = new ArrayList<>();
        JsonNode goodsNode = node.path("goods");
        for (JsonNode good : goodsNode) {
            goods.add(GoodType.stringToGoodType(good.asText()));
        }

        return new SldAbandonedStation(id, level,  daysLost,crewNeeded, goods);

    }

    @Override
    public List<GoodType> getGoods(){
        return goods;
    }

    @Override
    public int getDaysLost() {return daysLost;}

    @Override
    public int getCrewNeeded(){
        return crewNeeded;
    }

    @Override
    public String getCardName() {
        return "Abandoned Station";
    }

    @Override
    public void init(GameServer game) {
        this.game = game;
        this.flyBoard = game.getFlyboard();

        allowedPlayers = flyBoard.getScoreBoard().stream()
                .filter(player -> player.getShipBoard().getQuantityGuests() >= crewNeeded)
                .toList();
        playerIterator = allowedPlayers.iterator();
        effectTaken = false;
    }

    public void applyEffect(Player player, boolean wantsToApply) {
        if (this.state != CardState.ACCEPTATION_CHOICE) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }

        if (actualPlayer.equals(player)) {
            if (wantsToApply) {
                effectTaken = true;
                flyBoard.moveDays(actualPlayer, -daysLost);

                Event event = new SetCardStateEvent(player.getNickname(), CardState.GOODS_PLACEMENT);
                game.addEvent(event);
            }
        }

    }


    @Override
    public void setNextPlayer(){
        if (playerIterator.hasNext() && !effectTaken) {
            actualPlayer = playerIterator.next();
            setState(CardState.ACCEPTATION_CHOICE);
        } else {
            effectTaken = false;
            setState(CardState.FINALIZED);
        }
    }
}
