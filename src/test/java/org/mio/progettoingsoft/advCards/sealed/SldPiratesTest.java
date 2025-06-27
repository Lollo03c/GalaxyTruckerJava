package org.mio.progettoingsoft.advCards.sealed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.model.Cordinate;
import org.mio.progettoingsoft.model.FlyBoard;
import org.mio.progettoingsoft.model.Game;
import org.mio.progettoingsoft.network.server.GameManager;
import org.mio.progettoingsoft.model.advCards.sealed.SldAdvCard;
import org.mio.progettoingsoft.model.advCards.sealed.SldPirates;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.server.ServerController;

import java.util.*;

class SldPiratesTest {
    GameServer game;
    SldAdvCard card;
    ServerController controller = ServerController.getInstance();
    FlyBoard flyBoard;

    final int gameId = 1;
    final int cardId = 23;

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
        flyBoard.getPlayerByUsername("andrea").getShipBoard().removeComponent(new Cordinate(0, 2));
        flyBoard.getPlayerByUsername("andrea").getShipBoard().removeComponent(new Cordinate(3, 6));
        int a = 0;

        controller.drawCardTest(gameId, "antonio", cardId);
        card = flyBoard.getPlayedCard();
        card.disegnaCard();
    }

    @Test
    void shuold_do_one_iteration(){
        SldPirates pirates = (SldPirates) card;

        flyBoard.getPlayerByUsername("antonio").getShipBoard().drawShipboard();
        flyBoard.getPlayerByUsername("andrea").getShipBoard().drawShipboard();

        card.init(game);
        card.setNextPlayer();

        controller.activateDoubleDrills(gameId, "antonio", Collections.emptyList());
        controller.activateDoubleDrills(gameId, "andrea", Collections.emptyList());

        controller.setRollResult(gameId, "antonio", 2, 3);
        controller.advanceCannon(gameId, "antonio", true, false);
        controller.advanceCannon(gameId, "andrea", true, false);

        flyBoard.getPlayerByUsername("antonio").getShipBoard().drawShipboard();
        flyBoard.getPlayerByUsername("andrea").getShipBoard().drawShipboard();

        controller.setRollResult(gameId, "antonio", 1, 4);
        controller.advanceCannon(gameId, "antonio", true, false);
        controller.advanceCannon(gameId, "andrea", false, true);

        flyBoard.getPlayerByUsername("antonio").getShipBoard().drawShipboard();
        flyBoard.getPlayerByUsername("andrea").getShipBoard().drawShipboard();
    }

    @Test
    void shuold_do_secodno_iteration(){
        SldPirates pirates = (SldPirates) card;
        card.init(game);
        card.setNextPlayer();

        controller.activateDoubleDrills(gameId, "antonio", new ArrayList<>(List.of(
                new Cordinate(0, 4)
        )));

        flyBoard.getPlayerByUsername("antonio").getShipBoard().drawShipboard();
        flyBoard.getPlayerByUsername("andrea").getShipBoard().drawShipboard();
    }
}