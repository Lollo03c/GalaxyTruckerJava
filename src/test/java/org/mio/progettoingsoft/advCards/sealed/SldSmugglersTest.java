package org.mio.progettoingsoft.advCards.sealed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.server.ServerController;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SldSmugglersTest {

    GameServer game;
    SldAdvCard card;
    ServerController controller = ServerController.getInstance();
    FlyBoard flyBoard;

    final int gameId = 1;
    final int cardId = 22;

    List<Cordinate> toactivateYellow = new ArrayList<>(List.of(
            new Cordinate(0,  4),
            new Cordinate(1, 1)
    ));

    @BeforeEach
    void setup() {
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
    void should_add_some_goods() {
        SldSmugglers smugglers = (SldSmugglers) card;

        card.init(game);
        card.setNextPlayer();

        Player first = flyBoard.getPlayerByUsername("antonio");
        if (first.getColor().equals(HousingColor.YELLOW)) {
            controller.activateDoubleDrills(gameId, "antonio", toactivateYellow);

            assertEquals(CardState.GOODS_PLACEMENT, card.getState());
            controller.skipEffect(gameId, "antonio", cardId);

            assertEquals(CardState.FINALIZED, card.getState());

//            assertEquals("andrea", card.getActualPlayer().getNickname());
//            controller.activateDoubleDrills(gameId, "andrea", Collections.emptyList());
//            assertEquals(0, flyBoard.getPlayerByUsername("andrea").getShipBoard().getStoredQuantity(GoodType.RED));
            System.out.println("eseguito");
        }
    }

    @Test
    void should_add_reject_all(){
        SldSmugglers smugglers = (SldSmugglers) card;

        card.init(game);
        card.setNextPlayer();

        Player first = flyBoard.getPlayerByUsername("antonio");
        if (first.getColor().equals(HousingColor.YELLOW)){
            controller.activateDoubleDrills(gameId, "antonio", Collections.emptyList());

            controller.skipEffect(gameId, "antonio", cardId);


            assertEquals("andrea", card.getActualPlayer().getNickname());
            controller.activateDoubleDrills(gameId, "andrea", Collections.emptyList());
            assertEquals(0, flyBoard.getPlayerByUsername("andrea").getShipBoard().getStoredQuantity(GoodType.RED));


            assertEquals("lollo", card.getActualPlayer().getNickname());
            flyBoard.getPlayerByUsername("lollo").getShipBoard().drawShipboard();
            controller.activateDoubleDrills(gameId, "lollo", Collections.emptyList());
            flyBoard.getPlayerByUsername("lollo").getShipBoard().drawShipboard();
            assertEquals(0, flyBoard.getPlayerByUsername("lollo").getShipBoard().getStoredQuantity(GoodType.RED));
            assertEquals(0, flyBoard.getPlayerByUsername("lollo").getShipBoard().getStoredQuantity(GoodType.YELLOW));
            assertEquals(2, flyBoard.getPlayerByUsername("lollo").getShipBoard().getStoredQuantity(GoodType.GREEN));
        }


     }
}