package org.mio.progettoingsoft;

import org.mio.progettoingsoft.exceptions.IncorrectClientException;
import org.mio.progettoingsoft.exceptions.IncorrectNameException;
import org.mio.progettoingsoft.exceptions.SetGameModeException;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.client.VirtualClient;

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
    private GameServer waitingGame = null;

    private final Map<Integer, VirtualClient> clientsToAccept = new ConcurrentHashMap<>();

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
        if (instance == null)
            instance = new GameManager();

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
    public synchronized GameServer getWaitingGame(){
        if (waitingGame == null)
            waitingGame = new Game(getNextGameIdToStart());

        return waitingGame;
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

    public synchronized int addClientToAccept(VirtualClient client){
        int idClient = getNextIdPlayer();
        clientsToAccept.put(idClient, client);
        System.out.println("Aggiunto client " + client + "con id " + idClient);
        return idClient;
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

    private synchronized int getNextGameIdToStart(){
        while (ongoingGames.containsKey(nextGameId))
            nextGameId++;

        return nextGameId++;
    }

    public synchronized void addPlayerToGame(int idClient, String nickname) throws IncorrectClientException, IncorrectNameException, SetGameModeException {
        if (!clientsToAccept.containsKey(idClient))
            throw new IncorrectClientException("");

        if (nicknames.contains(nickname))
            throw new IncorrectNameException("");

        GameServer gameToStart = getWaitingGame();
        gameToStart.addPlayer(nickname, clientsToAccept.get(idClient));

        nicknames.add(nickname);

        clientsToAccept.remove(idClient);

        if (waitingGame.askSetting())
            throw new SetGameModeException("");

        if (gameToStart.getClients().size() == gameToStart.getNumPlayers()){
            GameServer ready = gameToStart;

            waitingGame = null;
            new Thread(ready::startGame).start();
        }
    }
}
