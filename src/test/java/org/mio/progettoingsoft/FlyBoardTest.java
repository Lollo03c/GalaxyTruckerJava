package org.mio.progettoingsoft;

import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.interfaces.GameServer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
class FlyBoardTest {

    @Test
    public void should_get_flyBoard_params(){
        HashSet<String> nicks = new HashSet<>(Set.of("antonio", "andrea", "lollo"));

        int gameId = 1;

        GameServer game = new Game(gameId, true);
        GameManager.getInstance().getOngoingGames().put(gameId, game);
        game.setupGame(GameMode.NORMAL, 3);
        game.createFlyboard(GameMode.NORMAL, nicks);

        game.startGame();
        FlyBoard flyBoard = game.getFlyboard();
        flyBoard.getNicknameList();
        flyBoard.drawCircuit();
        flyBoard.drawScoreboard();

        flyBoard.assignCreditsForBeautifulShip();
        flyBoard.assignCreditsForPositions();
        flyBoard.assignCreditsForRemainingGoods();
        flyBoard.penaltyForDiscardedComponents();

        List<Integer> first_deck = flyBoard.getAdvDeckByIndex(1);
        flyBoard.drawCard();
    }


  
}