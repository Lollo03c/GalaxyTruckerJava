package org.mio.progettoingsoft;

import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.model.FlyBoard;
import org.mio.progettoingsoft.model.Game;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.server.GameManager;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void should_create_a_game(){
        HashSet<String> nicks = new HashSet<>(Set.of("antonio", "andrea", "lollo"));

        GameServer game = new Game(1, true);
        GameManager.getInstance().getOngoingGames().put(1, game);

        assertEquals(1, game.getIdGame());

        game.setupGame(GameMode.NORMAL, 3);
        game.createFlyboard(GameMode.NORMAL, nicks);

        game.startGame();
        FlyBoard flyBoard = game.getFlyboard();

        assertEquals(GameMode.NORMAL, game.getGameMode());
        assertEquals(3, game.getNumPlayers());
        game.getLock();
    }

}