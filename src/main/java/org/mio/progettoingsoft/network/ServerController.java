package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.Lobby;
import org.mio.progettoingsoft.network.message.GameSetupInput;
import org.mio.progettoingsoft.network.message.JoinedGameMessage;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.message.RequestSetupMessage;

import java.rmi.RemoteException;

public class ServerController {
    private final Lobby lobby;

    public ServerController() {
        this.lobby = new Lobby();
    }

    public void addPlayerToGame(VirtualView client, String nickname) throws RemoteException {
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
                lobby.createGame(input.getClient(), input.getNickname(), input.getNumPlayers());

                System.out.print("Game created " + input.getNickname() + "\n");
            }
            default -> throw new RemoteException();
        }
    }
}
