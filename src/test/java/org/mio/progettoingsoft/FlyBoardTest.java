package org.mio.progettoingsoft;

import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.model.FlyBoardNormal;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.interfaces.GameServer;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FlyBoardTest {

    @Test
    public void should_get_flyBoard_params() {
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

    @Test
    public void should_build_little_decks() {
        FlyBoard fly = new FlyBoardNormal(Set.of("Stefano", "Andrea", "Antonio", "Lorenzo"));
        for (List<Integer> deck : fly.getLittleDecks()) {
            System.out.println(deck.toString());
        }
        System.out.println(fly.getHiddenDeck().toString());

        assertEquals(3, fly.getLittleDecks().size());
        assertNotNull(fly.getHiddenDeck());
        assertEquals(3, fly.getHiddenDeck().size());
        for (List<Integer> deck : fly.getLittleDecks()) {
            assertNotNull(deck);
            assertEquals(3, deck.size());
        }

        for (List<Integer> deck : fly.getLittleDecks()) {
            int lv1 = 0, lv2 = 0;
            for (Integer id : deck) {
                if (fly.getSldAdvCardByID(id).getLevel() == 1) {
                    lv1++;
                } else {
                    lv2++;
                }
            }
            assertEquals(1, lv1);
            assertEquals(2, lv2);
        }

        assertEquals(1, fly.getSldAdvCardByID(fly.getHiddenDeck().get(0)).getLevel());
        assertEquals(2, fly.getSldAdvCardByID(fly.getHiddenDeck().get(1)).getLevel());
        assertEquals(2, fly.getSldAdvCardByID(fly.getHiddenDeck().get(2)).getLevel());

    }

    @Test
    public void should_build_deck_for_adventure() {
        FlyBoard fly = new FlyBoardNormal(Set.of("Stefano", "Andrea", "Antonio", "Lorenzo"));
        for (List<Integer> deck : fly.getLittleDecks()) {
            System.out.println(deck.toString());
        }
        System.out.println(fly.getHiddenDeck().toString());
        fly.buildAdventureDeck();
        assertEquals(12, fly.getDeck().size());
        System.out.println(fly.getDeck().toString());
    }


}