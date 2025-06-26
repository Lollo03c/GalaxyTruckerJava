package org.mio.progettoingsoft;

import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.utils.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/*
    implemented with singleton
    stores all onGoing games and manages the creation of a new game
 */

public class GameManager{
    private static GameManager instance;

    private AtomicInteger nextIdPlayer = new AtomicInteger(0);
    private AtomicInteger nextIdGame = new AtomicInteger(0);

    private final List<String> nicknames = new ArrayList<>();

    private Map<Integer, GameServer> ongoingGames;
    private GameServer waitingGame = null;

    private final Map<Integer, VirtualClient> clientsToAccept = new ConcurrentHashMap<>();

    private final Object lockCreatingGame = new Object();
    private final AtomicBoolean creatingGame = new AtomicBoolean(false);

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
        if (waitingGame == null) {
            waitingGame = new Game(getNextGameIdToStart());
            Logger.debug("Created new waiting game.");
        }
        return waitingGame;
    }



    /**
     *
     * @return a 'Map<Integer, {@link Game}>' representing all the onGoing games;
     */
    public synchronized Map<Integer, GameServer> getOngoingGames() {
        return ongoingGames;
    }

    public int addClientToAccept(VirtualClient client){
            while (clientsToAccept.containsKey(nextIdPlayer.getAndIncrement())){

            }
            int idClient = nextIdPlayer.getAndIncrement();
            clientsToAccept.put(idClient, client);

            Logger.debug("Client " + idClient + " connected to server.");

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
//    public int getNextIdPlayer(){
//        synchronized (this) {
//            while (clientsToAccept.containsKey(nextIdPlayer))
//                nextIdPlayer++;
//
//            return nextIdPlayer++;
//        }
//    }

    public synchronized void addNickname(String nick){
        nicknames.add(nick);
    }

    private synchronized int getNextGameIdToStart(){
        while (ongoingGames.containsKey(nextIdGame.get())){
            nextIdGame.set(nextIdGame.get() + 1);
        }
        return nextIdGame.getAndIncrement();
    }

    /**
     * add a client to the server
     * @param idClient id of the client
     * @param nickname
     */
    public void addPlayerToGame(int idClient, String nickname) {
        if (!clientsToAccept.containsKey(idClient)) {
            return;
        }


        VirtualClient client = clientsToAccept.get(idClient);
        if (nicknames.contains(nickname)) {
            try {
                client.wrongNickname();
                return;
            } catch (Exception e) {
                clientsToAccept.remove(idClient);
            }

        }

        try {
            client.setNickname(nickname);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        synchronized (lockCreatingGame) {
           while (creatingGame.get()) {
               try {
                   lockCreatingGame.wait();
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
           }

           GameServer gameToStart = getWaitingGame();
           if (gameToStart.askSetting()){
               try{
                   creatingGame.set(true);

                   client.setGameId(gameToStart.getIdGame());
                   client.askGameSettings(nickname);

                   gameToStart.addPlayer(nickname, client);
                   clientsToAccept.remove(idClient);
                   nicknames.add(nickname);

                   Logger.info("Added player " + nickname + " to game " + gameToStart.getIdGame());
               }
               catch (Exception e){
                   throw new RuntimeException(e);
               }
           }
           else {
               try {
                   client.setGameId(gameToStart.getIdGame());
                   client.setState(GameState.WAITING_PLAYERS);

                   gameToStart.addPlayer(nickname, client);

                   Logger.info("Added player " + nickname + " to game " + gameToStart.getIdGame());

                   clientsToAccept.remove(idClient);
                   nicknames.add(nickname);

               } catch (Exception e) {
                    throw new RuntimeException(e);
               }

               if (gameToStart.getClients().size() == gameToStart.getNumPlayers()){
                   GameServer ready = gameToStart;
                   ongoingGames.put(ready.getIdGame(), ready);

                   waitingGame = null;
                   new Thread(ready::startGame).start();
               }
           }
        }
    }

    public AtomicBoolean getCreatingGame(){
        return this.creatingGame;
    }

    public Object getLockCreatingGame(){
        return lockCreatingGame;
    }

    public void removeOnGoingGame(int idGame){
        ongoingGames.remove(idGame);
    }
}
