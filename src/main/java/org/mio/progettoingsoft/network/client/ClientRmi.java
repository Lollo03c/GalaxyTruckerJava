package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.GameManager;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.IncorrectNameException;
import org.mio.progettoingsoft.exceptions.SetGameModeException;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.network.server.VirtualServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class ClientRmi extends Client implements VirtualClient{
    private VirtualServer server;

    public ClientRmi() throws RemoteException {
        System.setProperty("java.rmi.server.hostname", "localhost");  // l'IP reale del client sulla rete
        UnicastRemoteObject.exportObject(this, 0);

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            server = (VirtualServer) registry.lookup("GameSpace");

//            System.out.println("connesso al server Rmi");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
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
    public int getCoveredComponent(int idGame){
        try{
            controller.setState(GameState.ADD_COMPONENT);
            server.getCoveredComponent(idGame, controller.getNickname());
        }
        catch (RemoteException e){

        }
        return  -1;
    }

    @Override
    public void handleComponent(int idGame, String nickname, int idComp, Cordinate cordinate, int rotations){
        try {
            server.addComponent(idGame, nickname, idComp, cordinate, rotations);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}