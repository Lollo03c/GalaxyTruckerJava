package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.exceptions.IncorrectClientException;
import org.mio.progettoingsoft.exceptions.IncorrectNameException;
import org.mio.progettoingsoft.exceptions.SetGameModeException;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.network.server.VirtualServer;

import javax.swing.plaf.IconUIResource;
import java.io.Serializable;
import java.rmi.RemoteException;

public class ClientController {
    private static ClientController instance;

    public static ClientController getInstance(){
        if (instance == null){
            instance = new ClientController();
        }

        return instance;
    }

    private VirtualServer virtualServer;
    private GameState state = GameState.START;
    private Object lockState = new Object();
    private Client client;

    private int idClient;
    private int idGame;
    private String nickname;

    public GameState getState(){
        synchronized (lockState){
            return state;
        }
    }

    public void setState(GameState newState){
        synchronized (lockState){
            this.state = newState;
        }
    }

    public void connectToServer(boolean isRmi){
        if (isRmi){
            try {
                client = new ClientRmi();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            try {
                client = new ClientSocket();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        System.out.println("creato");
        client.connect();
        System.out.println("connesso");
        client.registryClient();

        System.out.println("registrato");
        setState(GameState.NICKNAME);
    }

    public void setIdClient(int idClient){
        this.idClient = idClient;
//        System.out.println(idClient);
    }

    public int getIdClient(){
        return idClient;
    }

    public void setNickname(String nickname){

        try {
            client.setNickname(nickname);

            setState(GameState.WAITING);
        } catch (IncorrectNameException e) {
            setState(GameState.ERROR_NICKNAME);
        } catch (IncorrectClientException e) {
            throw new RuntimeException(e);
        } catch (SetGameModeException e) {
            setState(GameState.GAME_MODE);
        }
    }

    public int getIdGame(){
        return idGame;
    }

    public String getNickname(){
        return nickname;
    }

    public void setIdGame(int idGame){
        this.idGame = idGame;
    }

    public void setConfirmedNickname(String nickname){
        this.nickname = nickname;
//        System.out.println("DEBUG -> Nickname Impostato");
    }

    public void setGameInfo(GameInfo gameInfo){
        client.setGameInfo(gameInfo);

//        System.out.println("game impostato");
        setState(GameState.WAITING);

    }
}
