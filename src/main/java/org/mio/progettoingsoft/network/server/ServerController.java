package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.advCards.sealed.SldAdvCard;
import org.mio.progettoingsoft.advCards.sealed.SldOpenSpace;
import org.mio.progettoingsoft.advCards.sealed.SldStardust;
import org.mio.progettoingsoft.exceptions.BadParameterException;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.utils.Logger;

import java.lang.reflect.Array;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ServerController {
    /**
     * SINGLETON IMPLEMENTATION
     */
    private static ServerController instance;

    public static ServerController getInstance() {
        if (instance == null) {
            instance = new ServerController();
        }
        return instance;
    }

    public int addClientToAccept(VirtualClient client) {
        GameManager gameManager = GameManager.getInstance();
        return gameManager.addClientToAccept(client);
    }

    public void handleNickname(int idClient, String nickname) {
        GameManager gameManager = GameManager.getInstance();

        gameManager.addPlayerToGame(idClient, nickname);
    }

    public void handleGameInfo(GameInfo gameInfo, String nickname) {
        GameManager gameManager = GameManager.getInstance();
        GameServer game = gameManager.getWaitingGame();
        game.setupGame(gameInfo.mode(), gameInfo.nPlayers());
        try {
            game.getClients().get(nickname).setState(GameState.WAITING_PLAYERS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addComponent(int idGame, String nickname, int idComp, Cordinate cordinate, int rotations) {
        GameManager gameManager = GameManager.getInstance();
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);

        ShipBoard shipBoard = game.getFlyboard().getPlayerByUsername(nickname).getShipBoard();
        shipBoard.addComponentToPosition(idComp, cordinate, rotations);

        Logger.debug(nickname + " added component " + idComp);

        for (Player player : game.getFlyboard().getPlayers()) {
            if (!player.getNickname().equals(nickname)) {
                VirtualClient client = game.getClients().get(player.getNickname());

                try {
                    client.addComponent(nickname, idComp, cordinate, rotations);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getCoveredComponent(int idGame, String nickname) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();

        VirtualClient client = game.getClients().get(nickname);

        try {
            client.setInHandComponent(flyBoard.getCoveredComponents().removeLast());
            client.setState(GameState.COMPONENT_MENU);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void discardComponent(int idGame, int idComponent) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        flyBoard.addUncoveredComponent(idComponent);

        for (VirtualClient client : game.getClients().values()) {
            try {
                client.addUncoveredComponent(idComponent);
            } catch (Exception e) {

            }
        }
    }

    public void applyStardust(int idGame, SldStardust card) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyboard = game.getFlyboard();
        card.applyEffect(flyboard);
        /*List<Player> reversedScoreboard = flyboard.getScoreBoard().reversed();
        for (Player p : reversedScoreboard){
            int exposedConnectors = p.getShipBoard().getExposedConnectors();
            flyboard.moveDays(p, -exposedConnectors);
        }*/

        SldAdvCard nextCard = flyboard.drawSldAdvCard();
        String type = nextCard.getCardName().toUpperCase();
        GameState next = GameState.stringToGameState(type);
        //TODO settare il Gamestate allo stato della carta pescata
        //a chi devo settarlo il nuovo stato? A tutti o basta settarlo a uno solo ?
        //game.getClients().get(nickname).setState(GameState.COMPONENT_MENU);

    }

    public void drawUncovered(int idGame, String nickname, Integer idComponent) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();

        boolean removed = flyBoard.getUncoveredComponents().remove(idComponent);

        if (removed) {
            for (VirtualClient client : game.getClients().values()) {
                try {
                    client.removeUncovered(idComponent);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                game.getClients().get(nickname).setInHandComponent(idComponent);
                game.getClients().get(nickname).setState(GameState.COMPONENT_MENU);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                game.getClients().get(nickname).setState(GameState.UNABLE_UNCOVERED_COMPONENT);
            } catch (Exception e) {

            }
        }
    }

    public void bookDeck(int idGame, String nickname, Integer deckNumber) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();

        synchronized (flyBoard.getAvailableDecks()) {
            List<Integer> availableDecks = flyBoard.getAvailableDecks();

            boolean removed = availableDecks.remove(deckNumber);

            if (removed) {
                for (VirtualClient client : game.getClients().values()) {
                    try {
                        client.removeDeck(deckNumber);
                        Logger.debug("removed deck " + deckNumber + " from client " + client);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                try {
                    game.getClients().get(nickname).setInHandDeck(deckNumber);
                    game.getClients().get(nickname).setState(GameState.VIEW_DECK);
                    Logger.debug("Set deck " + deckNumber + " to " + nickname);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    game.getClients().get(nickname).setState(GameState.UNABLE_DECK);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void freeDeck(int idGame, String nickname, Integer deckNumber) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();

        synchronized (flyBoard.getAvailableDecks()) {
            List<Integer> availableDecks = flyBoard.getAvailableDecks();
            availableDecks.add(deckNumber);
            Logger.debug("Free deck " + deckNumber + ".");

            for (VirtualClient client : game.getClients().values()) {
                try {
                    client.addAvailableDeck(deckNumber);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                game.getClients().get(nickname).setState(GameState.BUILDING_SHIP);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void endBuild(int idGame, String nickname) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        List<Integer> availablePlaces = flyBoard.getAvailableStartingPositions();
        VirtualClient client = game.getClients().get(nickname);
        try {
            client.setAvailablePlaces(availablePlaces);
            client.setState(GameState.CHOOSE_POSITION);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void choosePlace(int idGame, String nickname, int place) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        GameState state;
        VirtualClient client = game.getClients().get(nickname);
        try {
            flyBoard.addPlayerToCircuit(nickname, place);
            for(VirtualClient c : game.getClients().values()) {
                try {
                    c.addOtherPlayerToCircuit(nickname, place);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            state = GameState.END_BUILDING;
        } catch (BadParameterException e) {
            try {
                client.setAvailablePlaces(flyBoard.getAvailableStartingPositions());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            state = GameState.WRONG_POSITION;
        }
        try {
            client.setState(state);
            if(flyBoard.isReadyToAdventure()) {
                for (VirtualClient c : game.getClients().values()) {
                    c.setState(GameState.DRAW_CARD);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void activateDoubleEngine(int idGame, String  nickname, int number){
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        SldAdvCard card = flyBoard.getPlayedCard();

        //todo da controllare che il player sia quello giusto

        switch (card){
            case SldOpenSpace openSpace -> {
                openSpace.applyEffect(flyBoard, flyBoard.getPlayerByUsername(nickname), number);

                int nSteps = 2 * number + flyBoard.getPlayerByUsername(nickname).getShipBoard().getBaseEnginePower();

                //nofica l'avanzamento del player agli avversari
                for (VirtualClient client : game.getClients().values()){
                    try {
                        client.advancePlayer(nickname, nSteps);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            default -> {
                Logger.error("carta non valida per effetto activeDoubleEngine");
            }
        }
    }
}
