package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.model.enums.GameMode;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Client implements VirtualClient {
    protected ClientController controller = ClientController.getInstance();
    private ExecutorService executors = Executors.newFixedThreadPool(4);

    protected int idClient;

    public abstract void connect();
    public abstract void handleNickname(String nickname);
    public abstract void handleGameInfo(GameInfo gameInfo, String nickname);
    public abstract void getCoveredComponent(int idGame);
    public abstract void handleComponent(int idGame, String nickname, int idComp, Cordinate cordinate, int rotations);
    public abstract void discardComponent(int idComponent);
    public abstract void drawUncovered(int idComponent);
    public abstract void bookDeck(int deckNumber);
    public abstract void freeDeck(int deckNumber);

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

    public void addAvailableDeck(int deckNumber){
        executors.submit(() -> controller.addAvailableDeck(deckNumber));
    }








}

