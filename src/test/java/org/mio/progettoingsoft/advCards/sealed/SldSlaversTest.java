package org.mio.progettoingsoft.advCards.sealed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.advCards.CannonPenalty;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.server.ServerController;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SldSlaversTest {

    GameServer game;
    FlyBoard flyBoard;
    SldAdvCard card;
    ServerController controller = ServerController.getInstance();

    final int gameId = 1;
    final int cardId = 21;

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
        int a = 0;

        controller.drawCardTest(gameId, "antonio", cardId);
        card = flyBoard.getPlayedCard();
    }

    @Test
    void should_do_first_iteration(){
        card.disegnaCard();

        Player antonio = flyBoard.getPlayerByUsername("antonio");
        Player andrea = flyBoard.getPlayerByUsername("andrea");
        Player lollo = flyBoard.getPlayerByUsername("lollo");

        System.out.println(antonio.getShipBoard().getBaseFirePower());
        System.out.println(andrea.getShipBoard().getBaseFirePower());
        System.out.println(lollo.getShipBoard().getBaseFirePower());

        antonio.getShipBoard().drawShipboard();
        andrea.getShipBoard().drawShipboard();
        lollo.getShipBoard().drawShipboard();


        card.init(game);
        card.setNextPlayer();

        controller.activateDoubleDrills(gameId, "antonio", Collections.emptyList());
        controller.activateDoubleDrills(gameId, "andrea", Collections.emptyList());
        controller.activateDoubleDrills(gameId, "lollo", Collections.emptyList());


        controller.removeCrew(gameId, "antonio", new ArrayList<>(List.of(
                new Cordinate(2, 3),
                new Cordinate(2, 3),
                new Cordinate(3, 0),
                new Cordinate(3, 0)
        )));
        assertTrue(antonio.getShipBoard().getOptComponentByCord(new Cordinate(2, 3)).get().getGuests().isEmpty());

        controller.removeCrew(gameId, "lollo", new ArrayList<>(List.of(
                new Cordinate(2, 3),
                new Cordinate(2, 3),
                new Cordinate(3, 2),
                new Cordinate(3, 2)
        )));
        assertTrue(antonio.getShipBoard().getOptComponentByCord(new Cordinate(2, 3)).get().getGuests().isEmpty());
        assertEquals(CardState.FINALIZED, card.getState());
        int a= 0;
    }

    @Test
    void should_rejet_advantage(){
        card.init(game);
        card.setNextPlayer();

        controller.activateDoubleDrills(gameId, "antonio", new ArrayList<>(List.of(
                new Cordinate(1, 1)
        )));
        controller.skipEffect(gameId, "antonio", cardId);
        assertEquals(CardState.FINALIZED, card.getState());

        int a = 0;

    }

    @Test
    void should_take_advantage(){
        card.init(game);
        card.setNextPlayer();

        controller.activateDoubleDrills(gameId, "antonio", new ArrayList<>(List.of(
                new Cordinate(1, 1)
        )));
        controller.applyEffect(gameId, "antonio");
        assertEquals(8, flyBoard.getPlayerByUsername("antonio").getCredits());

        int a = 0;

    }
}