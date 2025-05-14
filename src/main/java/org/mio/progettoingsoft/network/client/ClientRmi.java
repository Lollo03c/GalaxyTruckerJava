package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.exceptions.IncorrectClientException;
import org.mio.progettoingsoft.exceptions.IncorrectNameException;
import org.mio.progettoingsoft.exceptions.SetGameModeException;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.network.server.VirtualServer;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
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
            String ip = InetAddress.getLocalHost().getHostAddress();
            System.setProperty("java.rmi.server.hostname", ip);

//            System.out.println("Connesso");

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registryClient(){
        try {
            System.out.println(" connesso0 ");
            int idClient = virtualServer.registerClient(this);
            System.out.println(" connesso1 ");

            ClientController.getInstance().setIdClient(idClient);
            System.out.println(" connesso21 ");

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setNickname(String nickname) throws IncorrectNameException, IncorrectClientException, SetGameModeException {
        try {
            virtualServer.setNickname(controller.getIdClient(), nickname);

            try {
                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                registry.bind(nickname, (VirtualClient) this);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            } catch (AlreadyBoundException e) {
                throw new RuntimeException(e);
            }

            controller.setConfirmedNickname(nickname);
//            System.out.println("nickname aggiunto");
        } catch (RemoteException e){
            e.printStackTrace();
        }
        catch (SetGameModeException e){
            try {
                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                registry.bind(nickname, (VirtualClient) this);
            } catch (RemoteException ep) {
                throw new RuntimeException(e);
            } catch (AlreadyBoundException ep) {
                throw new RuntimeException(e);
            }

            throw new SetGameModeException("");
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
    public void setState(GameState newState) throws RemoteException{
        executor.execute(() -> controller.setState(GameState.GAME_START));
    }
}
