package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.Game;
import org.mio.progettoingsoft.GameManager;
import org.mio.progettoingsoft.network.message.*;

import java.rmi.RemoteException;
import java.util.Optional;

public class ServerController {
    private static ServerController instance;

    private GameManager gameManager = GameManager.getInstance();

    public static ServerController create(){
        if(instance == null){
            instance = new ServerController();
        }
        return instance;
    }

    public static ServerController getInstance(){
       return instance;
    }
    private boolean waitingForGameSetting = false;

    /**
     * called when server received a {@link NicknameMessage}
     * add the player to the next game to start
     *
     * @param nickname : 'String'
     * @param idPlayer : temporary id to get its client
     */
    public void addPlayer(String nickname, int idPlayer){
        VirtualClient client = gameManager.getWaitingClients().get(idPlayer);
        if (client == null) {
            return;
        }

        if (gameManager.getNicknames().contains(nickname)) {

            try {
                client.reportError(0, null, ErrorType.NICKNAME);
            } catch (Exception e) {

            }
            return;
        }

        if (gameManager.getWaitingGame().isEmpty())
            gameManager.createWaitingGame();

        Game waitingGame = gameManager.getWaitingGame().get();

        waitingGame.addPlayer(nickname, client);
        gameManager.getWaitingClients().remove(idPlayer);
        gameManager.addNickname(nickname);

        if (gameManager.getWaitingGame().get().askSetting()){
            client.showUpdate(new GameSetupMessage(waitingGame.getIdGame(), nickname, 0, null));
        }

        waitingForGameSetting = true;

        if (waitingGame.isFull()) {
            Game readyToStart = waitingGame;
            new Thread(() -> readyToStart.startGame()).start();

            gameManager.emptyWaitingGame();
        }
        int a = 0;
    }

    /**
     * called when server receives a {@link GameSetupMessage}
     * Set-up the {@link org.mio.progettoingsoft.model.enums.GameMode} and the number of players of the next game to start
     *
     * @param message : {@link  GameSetupMessage}
     */
    public void setupGame(GameSetupMessage message){
        Optional<Game> optGame = gameManager.getWaitingGame();
        if (optGame.isEmpty())
            return;

        Game waitingGame = optGame.get();
        if (waitingGame.getIdGame() != message.getIdGame())
            return;

        waitingGame.setupGame(message.getMode(), message.getNumPlayers());

        waitingForGameSetting = false;
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

