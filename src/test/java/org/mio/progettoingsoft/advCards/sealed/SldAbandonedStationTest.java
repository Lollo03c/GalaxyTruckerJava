package org.mio.progettoingsoft.advCards.sealed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.model.FlyBoardNormal;
import org.mio.progettoingsoft.model.ShipBoardNormal;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.server.ServerController;

import javax.swing.*;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SldAbandonedStationTest {
    GameServer game;
    FlyBoard flyBoard;
    SldAdvCard card;
    ServerController controller = ServerController.getInstance();

    final int gameId = 1;
    final int cardId = 19;
    @BeforeEach
    void setup(){
        HashSet<String> nicks = new HashSet<>(Set.of("antonio", "andrea", "lollo"));

        game = new Game(gameId, true);
        GameManager.getInstance().getOngoingGames().put(gameId, game);
        game.setupGame(GameMode.NORMAL, 3);
        game.createFlyboard(GameMode.NORMAL, nicks);

        game.startGame();
        flyBoard = game.getFlyboard();

        for (String nick : nicks) {
            controller.takeBuild(gameId, nick);
        }
        controller.choosePlace(gameId, "antonio", 6);
        controller.choosePlace(gameId, "andrea", 3);
        controller.choosePlace(gameId, "lollo", 1);
//        controller.drawCard(gameId, "antonio");
        int a = 0;

        controller.drawCardTest(gameId, "antonio", cardId);
        card = flyBoard.getPlayedCard();



    }

    @Test
    void should_test_parameters(){
        assertEquals(19, card.getId());

        List<GoodType> goods = List.of(GoodType.YELLOW, GoodType.GREEN);
        assertTrue(card.getGoods().containsAll(goods));
        assertEquals(goods.size(), card.getGoods().size());

        assertEquals(1, card.getDaysLost());
        assertEquals(5, card.getCrewNeeded());
        assertEquals("Abandoned Station", card.getCardName());
    }

    @Test
    void should_init() {
        card.init(game);
        card.setNextPlayer();

        Player first = card.getActualPlayer();

        SldAbandonedStation station = (SldAbandonedStation) card;
        station.applyEffect(first, true);

        int idComp = first.getShipBoard().getOptComponentByCord(new Cordinate(1, 2)).get().getId();
        first.getShipBoard().getOptComponentByCord(new Cordinate(1, 2)).get().removeGood(GoodType.YELLOW);
        first.getShipBoard().drawShipboard();

        controller.addGood(gameId, "antonio", idComp, GoodType.YELLOW);
        first.getShipBoard().drawShipboard();

        controller.removeGood(gameId, "antonio",
                first.getShipBoard().getOptComponentByCord(new Cordinate(3, 6)).get().getId(),
                GoodType.RED);

        first.getShipBoard().drawShipboard();

        controller.skipEffect(gameId, "antonio", cardId);
        assertEquals(CardState.FINALIZED, card.getState());

    }



}