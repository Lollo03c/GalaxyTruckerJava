package org.mio.progettoingsoft.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Game;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.advCards.AbandonedStation;
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
    public void init(Game game) {
        FlyBoard board = game.getFlyboard();

//        if (board.getState() != GameState.DRAW_CARD) {
//            throw new IllegalStateException("Illegal state: " + board.getState());
//        }
        allowedPlayers = board.getScoreBoard().stream()
                .filter(player -> player.getShipBoard().getQuantityGuests() >= crewNeeded)
                .toList();
        playerIterator = allowedPlayers.iterator();
        if (playerIterator.hasNext()) {
            actualPlayer = playerIterator.next();
        } else {
            throw new RuntimeException("No allowed players");
        }
//        board.setState(GameState.CARD_EFFECT);
        this.state = CardState.ACCEPTATION_CHOICE;
    }

    public List<GoodType> applyEffect(FlyBoard board, Player player, boolean wantsToApply) {
        if (this.state != CardState.ACCEPTATION_CHOICE) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        this.state = CardState.APPLYING;
        if (actualPlayer.equals(player)) {
            if (wantsToApply) {
                board.moveDays(actualPlayer, -daysLost);
                this.state = CardState.GOODS_PLACEMENT;
                return new ArrayList<>(goods);
            } else {
                if (playerIterator.hasNext()) {
                    actualPlayer = playerIterator.next();

                    this.state = CardState.ACCEPTATION_CHOICE;

                } else {
                    this.state = CardState.FINALIZED;
                }
                return new ArrayList<>(Collections.emptyList());
            }
        } else {
            throw new BadPlayerException("The player " + actualPlayer.getNickname() + " can't play " + this.getCardName() + " at the moment");
        }
    }

    public void goodsPlaced(Player player) {
        if (this.state != CardState.GOODS_PLACEMENT) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        if (player.equals(actualPlayer)) {
            this.state = CardState.FINALIZED;
        } else {
            throw new BadPlayerException("The player " + actualPlayer.getNickname() + " can't confirm goods placement");
        }
    }

    @Override
    public void finish(FlyBoard board) {
        if (this.state != CardState.FINALIZED) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }
//        board.setState(GameState.DRAW_CARD);
    }
}
