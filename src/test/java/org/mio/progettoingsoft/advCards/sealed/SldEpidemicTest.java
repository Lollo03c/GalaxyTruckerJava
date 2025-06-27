package org.mio.progettoingsoft.advCards.sealed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.model.FlyBoard;
import org.mio.progettoingsoft.model.Game;
import org.mio.progettoingsoft.network.server.GameManager;
import org.mio.progettoingsoft.model.Player;
import org.mio.progettoingsoft.model.advCards.sealed.SldAdvCard;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.server.ServerController;

import java.util.HashSet;
import java.util.Set;

class SldEpidemicTest {
    GameServer game;
    SldAdvCard card;
    ServerController controller = ServerController.getInstance();

    final int gameId = 1;
    final int cardId = 25;

    @BeforeEach
    void setup(){
        HashSet<String> nicks = new HashSet<>(Set.of("antonio", "andrea", "lollo"));

        game = new Game(gameId, true);
        GameManager.getInstance().getOngoingGames().put(gameId, game);
        game.setupGame(GameMode.NORMAL, 3);
        game.createFlyboard(GameMode.NORMAL, nicks);

        game.startGame();
        FlyBoard flyBoard = game.getFlyboard();

        for (String nick : nicks){
            controller.takeBuild(gameId, nick);
        }
        controller.choosePlace(gameId, "antonio", 6);
        controller.choosePlace(gameId, "andrea", 3);
        controller.choosePlace(gameId, "lollo", 1);
//        controller.drawCard(gameId, "antonio");
        int a  = 0;

        controller.drawCardTest(gameId, "antonio", cardId);
        card = flyBoard.getPlayedCard();
    }

    @Test
    void prova(){

        FlyBoard flyBoard = game.getFlyboard();
        for (Player player : flyBoard.getPlayers()){
            player.getShipBoard().drawShipboard();
        }

        card.init(game);

        for (Player player : flyBoard.getPlayers()){
            player.getShipBoard().drawShipboard();
        }

    }

}