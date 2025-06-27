package org.mio.progettoingsoft.advCards.sealed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.model.FlyBoard;
import org.mio.progettoingsoft.model.Game;
import org.mio.progettoingsoft.network.server.GameManager;
import org.mio.progettoingsoft.model.Player;
import org.mio.progettoingsoft.exceptions.IncorrectFlyBoardException;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;
import org.mio.progettoingsoft.model.advCards.sealed.SldAdvCard;
import org.mio.progettoingsoft.model.advCards.sealed.SldOpenSpace;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.server.ServerController;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SldOpenSpaceTest {
    GameServer game;
    ServerController controller = ServerController.getInstance();
    SldAdvCard card;

    final int gameId = 1;
    final int cardId = 26;

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

//    @Test
//    void should_reject_illegal_player(){
//        FlyBoard flyBoard = game.getFlyboard();
//
//        card.init(game);
//        card.setNextPlayer();
//
//        SldOpenSpace openSpace = (SldOpenSpace) card;
//
//        Player illegalPlayer = new Player("sda", HousingColor.RED, GameMode.NORMAL, flyBoard);
//        assertThrows(IncorrectFlyBoardException.class, () -> openSpace.applyEffect(illegalPlayer, 3));
//    }

    @Test
    void should_reject_wrong_player(){
        FlyBoard flyBoard = game.getFlyboard();

        card.init(game);
        card.setNextPlayer();

        SldOpenSpace openSpace = (SldOpenSpace) card;

        Player player = flyBoard.getPlayerByUsername("andrea");
        assertThrows(IncorrectFlyBoardException.class, () -> openSpace.applyEffect(player, 3));
    }

    @Test
    void should_accept_leader_player(){
        FlyBoard flyBoard = game.getFlyboard();

        card.init(game);
        card.setNextPlayer();

        SldOpenSpace openSpace = (SldOpenSpace) card;

        Player player = flyBoard.getPlayerByUsername("antonio");
        openSpace.applyEffect(player, 1);
    }

    @Test
    void should_reject_not_enought_batteries(){
        FlyBoard flyBoard = game.getFlyboard();

        card.init(game);
        card.setNextPlayer();

        SldOpenSpace openSpace = (SldOpenSpace) card;

        Player player = flyBoard.getPlayerByUsername("antonio");
        assertThrows(IncorrectShipBoardException.class, () -> openSpace.applyEffect(player, 6));
    }
    @Test
    void should_reject_negative_double_engines(){
        FlyBoard flyBoard = game.getFlyboard();

        card.init(game);
        card.setNextPlayer();

        SldOpenSpace openSpace = (SldOpenSpace) card;

        Player player = flyBoard.getPlayerByUsername("antonio");
        assertThrows(IncorrectShipBoardException.class, () -> openSpace.applyEffect(player, -1));
    }

    @Test
    void should_reject_too_mant_double_engines(){
        FlyBoard flyBoard = game.getFlyboard();

        card.init(game);
        card.setNextPlayer();

        SldOpenSpace openSpace = (SldOpenSpace) card;

        Player player = flyBoard.getPlayerByUsername("antonio");
        assertThrows(IncorrectShipBoardException.class, () -> openSpace.applyEffect(player, 2));
    }

    @Test
    void should_remove_energy(){
        FlyBoard flyBoard = game.getFlyboard();

        card.init(game);
        card.setNextPlayer();

        SldOpenSpace openSpace = (SldOpenSpace) card;


        Player zero = flyBoard.getPlayerByUsername("antonio");

        int initial = zero.getShipBoard().getQuantBatteries();
        openSpace.applyEffect(zero, 1);

        assertEquals(initial - 1, zero.getShipBoard().getQuantBatteries());


    }

    @Test
    void should_work_if_not_activeted_by_first(){
        FlyBoard flyBoard = game.getFlyboard();

        card.init(game);
        card.setNextPlayer();

        SldOpenSpace openSpace = (SldOpenSpace) card;

        Player zero = flyBoard.getPlayerByUsername("antonio");
        openSpace.applyEffect(zero, 0);

        Player first = flyBoard.getPlayerByUsername("andrea");
        openSpace.applyEffect(first, 1);

        Player second = flyBoard.getPlayerByUsername("lollo");
        openSpace.applyEffect(second, 0);

    }


}