package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.Game;
import org.mio.progettoingsoft.GameManager;
import org.mio.progettoingsoft.network.message.*;

import java.rmi.RemoteException;
import java.util.Optional;

public class ServerController {
    private static ServerController instance = null;

    private GameManager gameManager = GameManager.getInstance();

    public ServerController() {}

    public static ServerController getInstance(){
        if (instance == null)
            instance = new ServerController();

        return instance;
    }

    /**
     * called when server received a {@link NicknameMessage}
     * add the player to the next game to start
     *
     * @param nickname : 'String'
     * @param idPlayer : temporary id to get its client
     */
    public void addPlayer(String nickname, int idPlayer){
        VirtualClient client = gameManager.getWaitingClients().get(idPlayer);
        if (client != null) {
            return;
        }

        if (gameManager.getNicknames().contains(nickname)) {

            try {
                client.reportError("nickname");
            } catch (Exception e) {

            }
            return;
        }

        if (gameManager.getWaitingGame().isEmpty())
            gameManager.createWaitingGame();

        Game waitingGame = gameManager.getWaitingGame().get();

        waitingGame.addPlayer(nickname, client);
        gameManager.getWaitingClients().remove(idPlayer);

        if (waitingGame.isFull()) {
            Game readyToStart = waitingGame;
            new Thread(() -> readyToStart.startGame()).start();

            gameManager.emptyWaitingGame();
        }

    }

    /**
     * called when server receives a {@link GameSetupInput}
     * Set-up the {@link org.mio.progettoingsoft.model.enums.GameMode} and the number of players of the next game to start
     *
     * @param message : {@link  GameSetupInput}
     */
    public void setupGame(GameSetupInput message){
        Optional<Game> optGame = gameManager.getWaitingGame();
        if (optGame.isEmpty())
            return;

        Game waitingGame = optGame.get();
        if (waitingGame.getIdGame() != message.getIdGame())
            return;

        waitingGame.setupGame(message.getMode(), message.getNumPlayers());
    }

    public void addPlayerToGame(VirtualClient client, String nickname) throws RemoteException {
        /*
        if(lobby.getWaitingGame() == null) {
            client.send(new RequestSetupMessage(client, nickname));
        } else {
            lobby.joinGame(client, nickname);
            client.send(new JoinedGameMessage(client, nickname));
        }
         */
    }

    public void handleInput(Message message) throws RemoteException {
        /*
        switch (message) {
            case GameSetupInput gsi -> {
                GameSetupInput input = (GameSetupInput) message;
                lobby.createGame(message.getClient(), input.getNickname(), input.getNumPlayers());
                System.out.print("Game created " + input.getNickname() + "\n");
            }
            default -> throw new RemoteException();
        }
         */
    }
}

