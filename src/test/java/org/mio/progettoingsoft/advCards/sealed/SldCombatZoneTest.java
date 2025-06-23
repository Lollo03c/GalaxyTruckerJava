package org.mio.progettoingsoft.advCards.sealed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Game;
import org.mio.progettoingsoft.GameManager;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.server.ServerController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SldCombatZoneTest {
    GameServer game;
    SldAdvCard card;
    ServerController controller = ServerController.getInstance();
    FlyBoard flyBoard;

    final int gameId = 1;
    final int cardId = 16;

    List<Cordinate> removeBlue = new ArrayList<Cordinate>(List.of(
            new Cordinate(2, 3),
            new Cordinate(3, 2),
            new Cordinate(3, 2),
            new Cordinate(2, 4) )
    );

    List<Cordinate> removeYellow = new ArrayList<Cordinate>(List.of(
            new Cordinate(2, 3),
            new Cordinate(2, 3),
            new Cordinate(3, 3),
            new Cordinate(4, 5) )
    );

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
    void should_init(){
        card.init(game);
    }
}