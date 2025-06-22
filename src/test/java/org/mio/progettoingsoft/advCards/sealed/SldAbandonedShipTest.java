package org.mio.progettoingsoft.advCards.sealed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Game;
import org.mio.progettoingsoft.GameManager;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.IncorrectFlyBoardException;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.server.ServerController;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SldAbandonedShipTest {
    GameServer game;
    SldAdvCard card;
    ServerController controller = ServerController.getInstance();
    FlyBoard flyBoard;

    final int gameId = 1;
    final int cardId = 37;

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
    void should_skip_if_effect_not_taken(){
        assertEquals(4, card.getCrewLost());
        assertEquals(1, card.getDaysLost());
        assertEquals(6, card.getCredits());

        card.init(game);
        card.setNextPlayer();

        controller.skipEffect(gameId, "antonio", cardId);

        assertEquals(flyBoard.getPlayerByUsername("andrea"), card.getActualPlayer());
    }

    @Test
    void should_throw_exceptions(){
        card.init(game);
        card.setNextPlayer();

        List<Cordinate> removeAntonio = new ArrayList<Cordinate>(List.of(
                new Cordinate(2, 3),
                new Cordinate(3, 2),
                new Cordinate(3, 2),
                new Cordinate(2, 4) )
        );

        flyBoard.getPlayerByUsername("antonio").getShipBoard().drawShipboard();
        SldAbandonedShip abandonedShip = (SldAbandonedShip) card;
        assertThrows(IncorrectFlyBoardException.class, () -> controller.removeCrew(gameId, "andrea", removeAntonio));
        assertThrows(IncorrectFlyBoardException.class, () -> controller.removeCrew(gameId, "antonio", null));
        assertThrows(IncorrectFlyBoardException.class, () -> controller.removeCrew(gameId, "antonio", Collections.emptyList()));

        assertThrows(IncorrectFlyBoardException.class, () -> controller.removeCrew(gameId, "antonio",
                new ArrayList<>(List.of(
                    new Cordinate(3, 2),
                    new Cordinate(3, 2),
                    new Cordinate(2, 4) )
                ))
        );
    }

    @Test
    void should_work_for_first(){
        card.init(game);
        card.setNextPlayer();

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


        System.out.println(card.getActualPlayer().getNickname());
        card.getActualPlayer().getShipBoard().drawShipboard();

        SldAbandonedShip abandonedShip = (SldAbandonedShip) card;
        controller.removeCrew(gameId, "antonio", card.actualPlayer.getColor().equals(HousingColor.BLUE) ? removeBlue : removeYellow);
        card.getActualPlayer().getShipBoard().drawShipboard();

        assertEquals(CardState.FINALIZED, card.getState());
    }

    @Test
    void should_work_for_second(){
        card.init(game);
        card.setNextPlayer();


        controller.skipEffect(gameId, "antonio", cardId);

        flyBoard.getPlayerByUsername("andrea").getShipBoard().drawShipboard();

        System.out.println(card.getActualPlayer().getNickname());
        SldAbandonedShip abandonedShip = (SldAbandonedShip) card;
        controller.removeCrew(gameId, "andrea", card.actualPlayer.getColor().equals(HousingColor.BLUE) ? removeBlue : removeYellow);

        assertEquals(CardState.FINALIZED, card.getState());
    }



}