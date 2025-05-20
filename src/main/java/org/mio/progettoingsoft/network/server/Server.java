package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.client.VirtualClient;

import java.rmi.RemoteException;

public abstract class Server implements VirtualServer{

    @Override
    public void handleNickname(int idClient, String nickname) {
        GameManager gameManager = GameManager.getInstance();

        gameManager.addPlayerToGame(idClient, nickname);
    }

    @Override
    public void handleGameInfo(GameInfo gameInfo, String nickname){
        GameManager gameManager = GameManager.getInstance();
        GameServer game = gameManager.getWaitingGame();
        game.setupGame(gameInfo.mode(), gameInfo.nPlayers());
        try {
            game.getClients().get(nickname).setState(GameState.WAITING_PLAYERS);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addComponent(int idGame, String nickname, int idComp, Cordinate cordinate, int rotations){
        GameManager gameManager = GameManager.getInstance();
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);

        ShipBoard shipBoard = game.getFlyboard().getPlayerByUsername(nickname).getShipBoard();
        shipBoard.addComponentToPosition(idComp, cordinate, rotations);

        for (Player player : game.getFlyboard().getPlayers()){
            if (!player.getNickname().equals(nickname)){
                VirtualClient client = game.getClients().get(player.getNickname());

                try {
                    client.addComponent(nickname, idComp, cordinate, rotations);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void getCoveredComponent(int idGame, String nickname){
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();

        VirtualClient client = game.getClients().get(nickname);

        try {
            client.setInHandComponent(flyBoard.getCoveredComponents().removeLast());
            client.setState(GameState.COMPONENT_MENU);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void discardComponent(int idGame, int idComponent){
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        flyBoard.addUncoveredComponent(idComponent);

        for (VirtualClient client : game.getClients().values()){
            try {
                client.addUnoveredComponent(idComponent);
            }
            catch (RemoteException e){

            }
        }
    }
}
