package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.Game;
import org.mio.progettoingsoft.GameManager;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.message.*;

import java.rmi.RemoteException;
import java.util.Optional;

public class ServerController {
    /**
     * SINGLETON IMPLEMENTATION
     * */
    private static ServerController instance;

    public static ServerController create(){
        if(instance == null){
            instance = new ServerController();
        }
        return instance;
    }

    public static ServerController getInstance(){
       return instance;
    }


    private GameManager gameManager = GameManager.getInstance();

    private boolean waitingForGameSetting = false;

    /**
     * called when server received a {@link NicknameMessage}
     * add the player to the next game to start
     *
     * @param nickname : 'String'
     * @param idPlayer : temporary id to get its client
     */
    public void addPlayer(String nickname, int idPlayer) throws Exception {
        VirtualClient client = gameManager.getWaitingClients().get(idPlayer);
        if (client == null) {
            return;
        }

        if (gameManager.getNicknames().contains(nickname)) {

            try {
                //client.reportError(0, null, ErrorType.NICKNAME);
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
        else {
            client.showUpdate(new WaitingForPlayerMessage(waitingGame.getIdGame(), nickname, waitingGame.getNumPlayers(), waitingGame.getGameMode()));
        }


        if (waitingGame.isFull()) {
            Game readyToStart = waitingGame;
            new Thread(() -> {
                try {
                    readyToStart.startGame();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();

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
    public void setupGame(GameSetupMessage message) throws Exception {
        Optional<Game> optGame = gameManager.getWaitingGame();
        if (optGame.isEmpty())
            return;

        Game waitingGame = optGame.get();
        if (waitingGame.getIdGame() != message.getIdGame())
            return;

        waitingGame.setupGame(message.getMode(), message.getNumPlayers());
        waitingGame.getClients().get(message.getNickname()).showUpdate(new WaitingForPlayerMessage(waitingGame.getIdGame(), message.getNickname(), waitingGame.getNumPlayers(), waitingGame.getGameMode()));

        waitingForGameSetting = false;
    }
}

