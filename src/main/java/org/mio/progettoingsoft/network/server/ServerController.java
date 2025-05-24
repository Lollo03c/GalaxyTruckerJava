package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.utils.Logger;

import java.rmi.RemoteException;
import java.util.List;

public class ServerController {
    /**
     * SINGLETON IMPLEMENTATION
     * */
    private static ServerController instance;

    public static ServerController getInstance() {
        if(instance == null){
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

    public void handleGameInfo(GameInfo gameInfo, String nickname){
        GameManager gameManager = GameManager.getInstance();
        GameServer game = gameManager.getWaitingGame();
        game.setupGame(gameInfo.mode(), gameInfo.nPlayers());
        try {
            game.getClients().get(nickname).setState(GameState.WAITING_PLAYERS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addComponent(int idGame, String nickname, int idComp, Cordinate cordinate, int rotations){
        GameManager gameManager = GameManager.getInstance();
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);

        ShipBoard shipBoard = game.getFlyboard().getPlayerByUsername(nickname).getShipBoard();
        shipBoard.addComponentToPosition(idComp, cordinate, rotations);

        Logger.debug(nickname + " added component " + idComp);

        for (Player player : game.getFlyboard().getPlayers()){
            if (!player.getNickname().equals(nickname)){
                VirtualClient client = game.getClients().get(player.getNickname());

                try {
                    client.addComponent(nickname, idComp, cordinate, rotations);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getCoveredComponent(int idGame, String nickname){
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

    public void discardComponent(int idGame, int idComponent){
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        flyBoard.addUncoveredComponent(idComponent);

        for (VirtualClient client : game.getClients().values()){
            try {
                client.addUncoveredComponent(idComponent);
            }
            catch (Exception e){

            }
        }
    }

    public void drawUncovered(int idGame, String nickname, Integer idComponent){
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
            }
            catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        else{
            try {
                game.getClients().get(nickname).setState(GameState.UNABLE_UNCOVERED_COMPONENT);
            } catch (Exception e) {

            }
        }
    }

    public void bookDeck(int idGame, String nickname, Integer deckNumber){
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();

        synchronized (flyBoard.getAvailableDecks()){
            List<Integer> availableDecks = flyBoard.getAvailableDecks();

            boolean removed = availableDecks.remove(deckNumber);

            if (removed){
                for (VirtualClient client : game.getClients().values()){
                    try{
                        client.removeDeck(deckNumber);
                    }
                    catch (Exception e){

                    }
                }

                try{
                    game.getClients().get(nickname).setInHandDeck(deckNumber);
                    game.getClients().get(nickname).setState(GameState.VIEW_DECK);
                } catch (Exception e) {

                }
            }
            else{
                try{
                    game.getClients().get(nickname).setState(GameState.UNABLE_DECK);
                } catch (Exception e) {

                }
            }
        }
    }

    public void freeDeck(int idGame, String nickname, Integer deckNumber){
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();

        synchronized (flyBoard.getAvailableDecks()){
            List<Integer> availableDecks = flyBoard.getAvailableDecks();
            availableDecks.add(deckNumber);

            for (VirtualClient client : game.getClients().values()){
                try {
                    client.addAvailableDeck(deckNumber);
                }
                catch (Exception e){

                }
            }

            try {
                game.getClients().get(nickname).setState(GameState.BUILDING_SHIP);
            } catch (Exception e) {

            }
        }
    }
}
