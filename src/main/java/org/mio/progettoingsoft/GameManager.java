package org.mio.progettoingsoft;

import org.mio.progettoingsoft.network.VirtualClient;
import org.mio.progettoingsoft.network.message.GameSetupInput;
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

    private Map<Integer, Game> ongoingGames;
    private Game waitingGame;

    private final Map<Integer, VirtualClient> clientsToAccept = new ConcurrentHashMap<>();
    private NicknameMessage nicknameMessage = null;
    private GameSetupInput gameSetupInput = null;

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

    public List<String> getNicknames(){
        return nicknames;
    }

    public Map<Integer, VirtualClient> getWaitingClients(){
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
    public synchronized Optional<Game> getWaitingGame(){
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
    public synchronized Map<Integer, Game> getOngoingGames() {
        return ongoingGames;
    }

    public void addClientToAccept(VirtualClient client) throws Exception {
        synchronized (clientsToAccept){
            while(clientsToAccept.containsKey(nextIdPlayer)){
                nextIdPlayer++;
            }

            clientsToAccept.put(nextIdPlayer, client);


            client.showUpdate(new NicknameMessage(null, nextGameId++));
        }
    }

    /**
     * set the waitingGame as null
     */
    public void emptyWaitingGame(){
        waitingGame = null;
    }
}
