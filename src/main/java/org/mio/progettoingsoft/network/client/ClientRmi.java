package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.network.server.VirtualServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ClientRmi extends Client {
    private final VirtualServer server;

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
    public void handleGameInfo(GameInfo gameInfo){

        try {
            server.handleGameInfo(gameInfo);
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
}