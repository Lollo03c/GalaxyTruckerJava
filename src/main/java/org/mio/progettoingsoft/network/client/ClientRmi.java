package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.network.server.VirtualServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientRmi extends Client implements VirtualClient{
    private final VirtualServer server;
    private ExecutorService executors = Executors.newFixedThreadPool(4);

    public ClientRmi() throws RemoteException {
        System.setProperty("java.rmi.server.hostname", "localhost");  // l'IP reale del client sulla rete
        UnicastRemoteObject.exportObject(this, 0);

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            server = (VirtualServer) registry.lookup("GameSpace");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void connect(){
        try {
            idClient = server.registerClient(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ping(String msg) throws RemoteException{
        System.out.println(msg);
    }

    @Override
    public void handleNickname(String nickname) {
        try {
            server.handleNickname(idClient, nickname);
        } catch (RemoteException e) {
            //server non raggiungibile
        }
    }

    @Override
    public void handleGameInfo(GameInfo gameInfo, String nickname){
        try {
            server.handleGameInfo(gameInfo, nickname);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getCoveredComponent(int idGame){
        try{
            server.getCoveredComponent(idGame, controller.getNickname());
//            controller.setState(GameState.COMPONENT_MENU);
        }
        catch (RemoteException e){

        }
    }

    @Override
    public void handleComponent(int idGame, String nickname, int idComp, Cordinate cordinate, int rotations){
        try {
            server.addComponent(idGame, nickname, idComp, cordinate, rotations);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void discardComponent(int idComponent){
        try{
            server.discardComponent(controller.getIdGame(), idComponent);
        }
        catch (RemoteException e){

        }
    }

    @Override
    public void drawUncovered(int idComponent){
        try{
            server.drawUncovered(controller.getIdGame(), controller.getNickname(), idComponent);
        }
        catch (RemoteException e){

        }
    }

    @Override
    public void bookDeck(int deckNumber){
        try{
            server.bookDeck(controller.getIdGame(),controller.getNickname(), deckNumber);
        }
        catch (RemoteException e){

        }
    }

    @Override
    public void freeDeck(int deckNumber){
        try{
            server.freeDeck(controller.getIdGame(), controller.getNickname(), controller.getInHandDeck());
        }
        catch (RemoteException e){

        }
    }

    /**
     * Metodi derivati da VirtualClient, ovvero i metodi che vengono chiamati dal server
     */

    @Override
    public void setNickname(String nickname) throws RemoteException{
        executors.submit(() -> {
            controller.setNickname(nickname);
            System.out.println("nickname corretto");
            controller.setState(GameState.WAITING);
        });
    }

    @Override
    public void askGameSettings(String nickname) throws RemoteException{
        executors.submit(() -> {
            controller.setState(GameState.GAME_MODE);
        });
    }

    @Override
    public void wrongNickname() throws RemoteException{
        executors.submit(() -> {
            controller.setState(GameState.ERROR_NICKNAME);
        });
    }

    @Override
    public void setGameId(int idGame) {
        controller.setGameId(idGame);
    }

    @Override
    public void setState(GameState state) throws RemoteException{
        controller.setState(state);
    }

    @Override
    public void setFlyBoard(GameMode mode, Map<String, HousingColor> players) throws RemoteException{
        executors.submit(() ->
                controller.setFlyBoard(mode, players)
        );
    }

    @Override
    public void addComponent(String nickname, int idComp, Cordinate cordinate, int rotations) throws RemoteException{
        executors.submit(() ->
                controller.addOtherComponent(nickname, idComp, cordinate, rotations)
        );
    }

    @Override
    public void setInHandComponent(int idComponent) throws RemoteException{
        executors.submit(() ->
                controller.setInHandComponent(idComponent)
        );
    }

    @Override
    public void addUncoveredComponent(int idComp) throws RemoteException{
        executors.submit(() -> {
            controller.addUncoveredComponent(idComp);
        });
    }

    @Override
    public void removeUncovered(int idComp) throws RemoteException{
        executors.submit(() ->
                controller.removeUncovered(idComp)
        );
    }

    @Override
    public void removeDeck(Integer deckNumber) throws RemoteException{
        executors.submit(() -> controller.removeDeck(deckNumber));
    }

    @Override
    public void setInHandDeck(int deck){
        executors.submit(() -> controller.setInHandDeck(deck));
    }

    @Override
    public void addAvailableDeck(int deckNumber){
        executors.submit(() -> controller.addAvailableDeck(deckNumber));
    }

//    @Override
    public VirtualServer getServer(){
        return server;
    }
}