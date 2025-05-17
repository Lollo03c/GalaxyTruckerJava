package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.exceptions.IncorrectClientException;
import org.mio.progettoingsoft.exceptions.IncorrectNameException;
import org.mio.progettoingsoft.exceptions.SetGameModeException;
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
    public void handleGameInfo(GameInfo gameInfo){
        GameManager gameManager = GameManager.getInstance();
        GameServer game = gameManager.getWaitingGame();;

        game.setupGame(gameInfo.mode(), gameInfo.nPlayers());
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
}
