package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.Lobby;
import org.mio.progettoingsoft.network.SerMessage.*;
import org.mio.progettoingsoft.network.message.*;

import java.rmi.RemoteException;

public class ServerController {
    private final Lobby lobby;

    public ServerController() {
        this.lobby = new Lobby();
    }

    public void addPlayerToGame(VirtualClient client, String nickname) throws RemoteException {
        if(lobby.getWaitingGame() == null) {
            client.update(new RequestSetupMessage(client, nickname));
        } else {
            lobby.joinGame(client, nickname);
            client.update(new JoinedGameMessage(client, nickname));
        }
    }

    public void handleInput(Message message) throws RemoteException {
        switch (message) {
            case GameSetupInput gsi -> {
                GameSetupInput input = (GameSetupInput) message;
                lobby.createGame(message.getClient(), input.getNickname(), input.getNumPlayers());
                System.out.print("Game created " + input.getNickname() + "\n");
            }
            default -> throw new RemoteException();
        }
    }

    public void addPlayerToGame2(VirtualClient client, String nickname) throws RemoteException {
        if(lobby.getWaitingGame() == null) {
            client.update2(new RequestSetupMessage2(nickname));

        } else {
            lobby.joinGame(client, nickname);
            client.update2(new JoinedGameMessage2(nickname));
        }
    }
    public void handleInput2(VirtualClient client, SerMessage message) throws RemoteException  {
        switch (message) {
            case NewPlayerMessage npm -> {
                addPlayerToGame2(client, npm.getNickname());
                System.out.println("New player added " + message.getNickname() + "\n");
            }
            case GameSetupInput2 gsi2 -> {
                GameSetupInput2 input = (GameSetupInput2) message;
                lobby.createGame(client, message.getNickname(), input.getNumPlayers());
                System.out.print("Game created " + input.getNickname() + "\n");
            }
            default -> throw new RemoteException();
        }
    }
}
