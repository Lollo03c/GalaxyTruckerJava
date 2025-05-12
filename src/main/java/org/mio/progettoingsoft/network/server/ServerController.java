package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.GameManager;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.message.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

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

    private final static Logger logger = LoggerFactory.getLogger(ServerController.class);

    private final GameManager gameManager = GameManager.getInstance();

    private final Map<String, VirtualClient> userToClient = new ConcurrentHashMap<>();

    private boolean waitingForGameSetting = false;

    /**
     * called when the server receive a new connection from a client
     * send a {@link WelcomeMessage} to the client and regsiter the client in the waiting clients list
     * @param client : virtual client to send message to
     * @throws Exception : throws the exc thrown by the showUpdate message
     */
    public void addClientToAccept(VirtualClient client) throws Exception {
        int idClient = GameManager.getInstance().getNextIdPlayer();
        client.showUpdate(new WelcomeMessage(idClient));
        gameManager.addClientToAccept(idClient, client);
    }

    /**
     * called when server received a {@link NicknameMessage}
     * add the player to the next game to start
     *
     * @param nickname : 'String'
     * @param idPlayer : temporary id to get its client
     */
    public void addPlayer(String nickname, int idPlayer) throws Exception {

        VirtualClient client = gameManager.getClientsToAccept().get(idPlayer);

        if (client == null) {
            logger.info("Received a NicknameMessage (\"{}\") from unknown setupID ({})", nickname, idPlayer);
            return;
        }

        if (gameManager.getNicknames().contains(nickname) || nickname.equals(Message.getBroadcastAddress())) {
            logger.info("\"{}\" already taken", nickname);
            client.showUpdate(new ErrorMessage(-1, nickname, ErrorType.NICKNAME));
            return;
        }

        if (gameManager.getWaitingGame().isEmpty())
            gameManager.createWaitingGame();

        GameServer waitingGame = gameManager.getWaitingGame().get();

        waitingGame.addPlayer(nickname, client);
        gameManager.getClientsToAccept().remove(idPlayer);
        this.userToClient.put(nickname, client);
        gameManager.addNickname(nickname);

        logger.info("{} inserito alla partita {}", nickname, waitingGame.getIdGame());

        if (gameManager.getWaitingGame().get().askSetting()) {
            client.showUpdate(new GameSetupMessage(waitingGame.getIdGame(), nickname, 0, null));
            waitingForGameSetting = true;
        } else {
            client.showUpdate(new WaitingForPlayerMessage(waitingGame.getIdGame(), nickname, waitingGame.getNumPlayers(), waitingGame.getGameMode()));
        }


        if (waitingGame.isFull()) {
            GameServer readyToStart = waitingGame;
            new Thread(() -> {
                try {
                    gameManager.addGame(readyToStart);
                    readyToStart.startGame();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();

            gameManager.emptyWaitingGame();
        }

    }

    /**
     * called when server receives a {@link GameSetupMessage}
     * Set-up the {@link org.mio.progettoingsoft.model.enums.GameMode} and the number of players of the next game to start
     *
     * @param message : {@link  GameSetupMessage}
     */
    public void setupGame(GameSetupMessage message) throws Exception {
        Optional<GameServer> optGame = gameManager.getWaitingGame();

        // TODO l'error di tipo SETUP non è ancora gestito lato client!!!!
        if (optGame.isEmpty()) {
            logger.error("Recieved gameSetupMessage, but no game was created.");
            this.userToClient.get(message.getNickname()).showUpdate(new ErrorMessage(-1, message.getNickname(), ErrorType.SETUP));
            return;
        }

        GameServer waitingGame = optGame.get();
        if (waitingGame.getIdGame() != message.getIdGame()) {
            logger.error("Recieved gameSetupMessage with erroneous game ID: {}", message.getIdGame());
            this.userToClient.get(message.getNickname()).showUpdate(new ErrorMessage(-1, message.getNickname(), ErrorType.SETUP));
            return;
        }

        waitingGame.setupGame(message.getMode(), message.getNumPlayers());
        waitingGame.getClients().get(message.getNickname()).showUpdate(new WaitingForPlayerMessage(waitingGame.getIdGame(), message.getNickname(), waitingGame.getNumPlayers(), waitingGame.getGameMode()));

        waitingForGameSetting = false;
    }

    public boolean getIsWaitinGameSetup(){
        return waitingForGameSetting;
    }
}

