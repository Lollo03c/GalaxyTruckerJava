package org.mio.progettoingsoft;

import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.message.GameSetupMessage;
import org.mio.progettoingsoft.network.message.NicknameMessage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/*
    implemented with singleton
    stores all onGoing games and manages the creation of a new game
 */

public class GameManager{
    private static GameManager instance;

    private int nextIdPlayer = 0;
    private int nextGameId = 0;

    private final List<String> nicknames = new ArrayList<>();

    private Map<Integer, GameServer> ongoingGames;
    private GameServer waitingGame;

    private final Map<Integer, VirtualClient> clientsToAccept = new ConcurrentHashMap<>();
    private NicknameMessage nicknameMessage = null;
    private GameSetupMessage gameSetupMessage = null;

    public static GameManager create() {
        if(instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    /**
     * return the singleton repreeing the whole class
     *
     * @return the instance of the class
     */
    public static synchronized GameManager getInstance(){
        return instance;
    }

    public synchronized List<String> getNicknames(){
        return nicknames;
    }

    public synchronized Map<Integer, VirtualClient> getClientsToAccept(){
        return clientsToAccept;
    }

    public GameManager(){
        ongoingGames = new HashMap<>();
    }

    /**
     * get the current waiting game
     *
     * @return  Optional.empty() if the game has to be created
     *          Optional.of(Game) an optional containing the current waiting game
     */
    public synchronized Optional<GameServer> getWaitingGame(){
        return waitingGame == null ? Optional.empty() : Optional.of(waitingGame);
    }

    /**
     * Create a new {@link Game} with the first idGame available
     */
    public synchronized void createWaitingGame(){
        while (ongoingGames.containsKey(nextGameId))
            nextGameId++;

        waitingGame = new Game(nextGameId);
    }


    /**
     *
     * @return a 'Map<Integer, {@link Game}>' representing all the onGoing games;
     */
    public synchronized Map<Integer, GameServer> getOngoingGames() {
        return ongoingGames;
    }

    public synchronized void addClientToAccept(int idClient, VirtualClient client)  {
        clientsToAccept.put(idClient, client);
    }

    /**
     * set the waitingGame as null
     */
    public synchronized void emptyWaitingGame(){
        waitingGame = null;
    }


    /**
     *
     * @return the next possible available idPlayer
     */
    public synchronized int getNextIdPlayer(){
        while (clientsToAccept.containsKey(nextIdPlayer))
            nextIdPlayer++;

        return nextIdPlayer++;
    }

    public synchronized void addNickname(String nick){
        nicknames.add(nick);
    }
}
