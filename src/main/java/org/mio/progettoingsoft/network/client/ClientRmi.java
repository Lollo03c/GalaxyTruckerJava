package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.exceptions.IncorrectClientException;
import org.mio.progettoingsoft.exceptions.IncorrectNameException;
import org.mio.progettoingsoft.exceptions.SetGameModeException;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.network.server.VirtualServer;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientRmi extends Client implements Serializable {
    ExecutorService executor = Executors.newFixedThreadPool(2);

    public ClientRmi() throws RemoteException{
        super();
    }

    @Override
    public void connect(){
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            virtualServer = (VirtualServer) registry.lookup("GameSpace");

            System.out.println("Connesso");

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registryClient(){
        try {
            int idClient = virtualServer.registerClient(this);

            ClientController.getInstance().setIdClient(idClient);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setNickname(String nickname) throws IncorrectNameException, IncorrectClientException, SetGameModeException {
        try {
            virtualServer.setNickname(controller.getIdClient(), nickname);
            controller.setConfirmedNickname(nickname);
            System.out.println("nickname aggiunto");
        } catch (RemoteException e){
            e.printStackTrace();
        }
    }

    @Override
    public void setGameInfo(GameInfo gameInfo){
        try {
            virtualServer.setGameInfo(gameInfo);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setState(GameState newState){
        executor.execute(() -> controller.setState(newState));
    }
}
