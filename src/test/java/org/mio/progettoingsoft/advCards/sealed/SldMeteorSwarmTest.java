package org.mio.progettoingsoft.advCards.sealed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Game;
import org.mio.progettoingsoft.GameManager;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.server.ServerController;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SldMeteorSwarmTest {

    GameServer game;
    SldAdvCard card;
    ServerController controller = ServerController.getInstance();
    FlyBoard flyBoard;

    final int gameId = 1;
    final int cardId = 29;

    @BeforeEach
    void setup() {
        HashSet<String> nicks = new HashSet<>(Set.of("antonio", "andrea"));

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
//        controller.drawCard(gameId, "antonio");
        int a = 0;

        controller.drawCardTest(gameId, "antonio", cardId);
        card = flyBoard.getPlayedCard();
    }

    @Test
    void should_get_correct_parameters(){
        SldMeteorSwarm meteorSwarm = (SldMeteorSwarm) card;

        card.init(game);
        meteorSwarm.setNextMeteor();

        controller.setRollResult(gameId, "antonio", 3, 3);
        controller.advanceMeteor(gameId, "antonio", false, false);
        controller.advanceMeteor(gameId, "andrea", false, false);

        controller.setRollResult(gameId, "antonio", 3, 3);
        controller.advanceMeteor(gameId, "antonio", false, false);
        controller.advanceMeteor(gameId, "andrea", false, false);

        controller.setRollResult(gameId, "antonio", 3, 3);
        controller.advanceMeteor(gameId, "antonio", false, false);
        controller.advanceMeteor(gameId, "andrea", false, false);

        controller.setRollResult(gameId, "antonio", 3, 3);
        controller.advanceMeteor(gameId, "antonio", false, true);
        controller.advanceMeteor(gameId, "andrea", false, false);

        controller.setRollResult(gameId, "antonio", 3, 3);
        controller.advanceMeteor(gameId, "antonio", false, false);
        controller.advanceMeteor(gameId, "andrea", false, false);

        controller.setRollResult(gameId, "antonio", 3, 3);
        controller.advanceMeteor(gameId, "antonio", false, false);
        controller.advanceMeteor(gameId, "andrea", false, false);

        assertEquals(CardState.FINALIZED, card.getState());


    }
}