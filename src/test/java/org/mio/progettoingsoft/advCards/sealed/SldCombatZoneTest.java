package org.mio.progettoingsoft.advCards.sealed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.model.Cordinate;
import org.mio.progettoingsoft.model.FlyBoard;
import org.mio.progettoingsoft.model.Game;
import org.mio.progettoingsoft.model.ShipBoard;
import org.mio.progettoingsoft.model.advCards.sealed.SldAdvCard;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.server.GameManager;
import org.mio.progettoingsoft.network.server.ServerController;

import java.util.*;

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


    }

    @Test
    void should_play_first_card(){
        controller.drawCardTest(gameId, "antonio", 16);
        card = flyBoard.getPlayedCard();

        if (card.getId() == 16) {
//            flyBoard.drawCircuit();

            ShipBoard anto = flyBoard.getPlayerByUsername("antonio").getShipBoard();
            ShipBoard andrea = flyBoard.getPlayerByUsername("andrea").getShipBoard();
            ShipBoard lollo = flyBoard.getPlayerByUsername("lollo").getShipBoard();

            card.init(game);

            controller.activateDoubleEngine(gameId, "antonio", 0);
            controller.activateDoubleEngine(gameId, "andrea", 0);
            controller.activateDoubleEngine(gameId, "lollo", 0);

            controller.removeCrew(gameId, "antonio", new ArrayList<>(List.of(
                    new Cordinate(2, 3),
                    new Cordinate(2, 3)
            )));

            controller.activateDoubleDrills(gameId, "antonio", Collections.emptyList());
            controller.activateDoubleDrills(gameId, "andrea", Collections.emptyList());
            controller.activateDoubleDrills(gameId, "lollo", Collections.emptyList());

            controller.setRollResult(gameId, "andrea", 1, 1);
            controller.advanceCannon(gameId, "andrea", false, false);

//            flyBoard.drawCircuit();
        }
    }

    @Test
    public void should_play_second_card() {
        controller.drawCardTest(gameId, "antonio", 36);
        card = flyBoard.getPlayedCard();

        if (card.getId() == 36) {
            ShipBoard anto = flyBoard.getPlayerByUsername("antonio").getShipBoard();
            ShipBoard andrea = flyBoard.getPlayerByUsername("andrea").getShipBoard();
            ShipBoard lollo = flyBoard.getPlayerByUsername("lollo").getShipBoard();

            flyBoard.drawCircuit();
            card.init(game);
            controller.activateDoubleDrills(gameId, "antonio", Collections.emptyList());
            controller.activateDoubleDrills(gameId, "andrea", Collections.emptyList());
            controller.activateDoubleDrills(gameId, "lollo", Collections.emptyList());

            flyBoard.drawCircuit();


            controller.activateDoubleEngine(gameId, "antonio", 0);
            controller.activateDoubleEngine(gameId, "lollo", 0);
            controller.activateDoubleEngine(gameId, "andrea", 0);

        }
    }
}