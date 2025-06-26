package org.mio.progettoingsoft.network.client.rmi;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.Direction;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.advCards.sealed.CardState;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.GuestType;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.model.enums.CannonType;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.enums.MeteorType;
import org.mio.progettoingsoft.network.client.Client;
import org.mio.progettoingsoft.network.client.ClientController;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.server.VirtualServer;
import org.mio.progettoingsoft.network.server.rmi.VirtualServerRmi;
import org.mio.progettoingsoft.utils.ConnectionInfo;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RmiClient extends UnicastRemoteObject implements VirtualClient, Client {
    private final VirtualServerRmi server;
    private ExecutorService executors = Executors.newFixedThreadPool(4);
    private final ClientController controller;
    private final ConnectionInfo connectionInfo;

    /**
     * Implements the RMI client functionalities, extending {@link UnicastRemoteObject}
     * and implementing {@link VirtualClient} and {@link Client}. This class handles
     * the connection to the RMI server and processes callbacks from the server.
     */
    public RmiClient(ConnectionInfo connectionInfo) throws RemoteException {
        super();
        this.controller = ClientController.getInstance();
        this.connectionInfo = connectionInfo;

        try {
            System.out.println("Connetto a " + connectionInfo.getIpHost());
            Registry registry = LocateRegistry.getRegistry(connectionInfo.getIpHost(), connectionInfo.getRmiPort());
            server = (VirtualServerRmi) registry.lookup(connectionInfo.getServerName());
        } catch (NotBoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        System.out.println("Connesso al server: " + server);
    }

    /**
     * METHODS INHERITED FROM Client INTERFACE
     */

    @Override
    public void connect() throws RemoteException {
        System.out.println("Inizio connect");
        controller.setIdClient(server.registerClient(this));
        System.out.println("Fine connect");
    }

    @Override
    public VirtualServer getServer(){
        return server;
    }


    /**
     * METHODS INHERITED FROM VirtualClientRmi  INTERFACE, they're directly called from the server
     */
    @Override
    public void ping(String msg) throws RemoteException{
        System.out.println(msg);
    }

    @Override
    public void setNickname(String nickname) throws RemoteException{
        executors.submit(() -> {
            controller.setNickname(nickname);
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
        executors.submit(() ->
            controller.setGameId(idGame)
        );
    }

    @Override
    public void setState(GameState state) throws RemoteException{
        controller.setState(state);
    }

    @Override
    public void setCardState(CardState state) throws RemoteException{
        executors.submit(() -> controller.setCardState(state));
    }

    @Override
    public void setFlyBoard(GameMode mode, Map<String, HousingColor> players, List<List<Integer>> decks) throws RemoteException{
        executors.submit(() ->
                controller.setFlyBoard(mode, players, decks)
        );
    }

    @Override
    public void addComponent(String nickname, int idComp, Cordinate cordinate, int rotations) throws RemoteException{
        executors.submit(() ->
                controller.addOtherPlayersComponent(nickname, idComp, cordinate, rotations)
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

    @Override
    public void setBuiltShip(String nickname) throws RemoteException{
        executors.submit(() -> controller.assignBuild(nickname));
    }

    public void setAvailablePlaces(List<Integer> availablePlaces){
        executors.submit(() -> {
            controller.setAvailablePlaces(availablePlaces);
        });
    }

    @Override
    public void addOtherPlayerToCircuit(String nickname, int place) throws Exception {
        executors.submit(() -> {
            controller.addOtherPlayerToCircuit(nickname, place);
        });
    }

    @Override
    public void advancePlayer(String nickname, int steps) throws RemoteException{
        executors.submit(() -> controller.advancePlayer(nickname, steps));
    }

    public void setPlayedCard(int idCard) throws RemoteException{
        executors.submit(() -> controller.setCard(idCard));
    }

    @Override
    public void addCredits(String nickname, int credits) throws RemoteException{
        executors.submit(() -> controller.addCredits(nickname, credits));
    }

    @Override
    public void removeCrew(int idComp) throws RemoteException{
        executors.submit(() -> controller.crewLost(idComp));
    }

    @Override
    public void genericChoiceError(String msg) throws RemoteException{
        executors.submit(()->controller.genericChoiceError(msg));
    }

    @Override
    public void addGood(int idComp, GoodType type) throws RemoteException{
        executors.submit(() ->
            controller.addGoodToModel(idComp, type)
        );
    }

    @Override
    public void removeGoodPendingList(String nickname, GoodType type) throws RemoteException{
        executors.submit(() ->
            controller.removePendingGood(nickname, type)
        );
    }

    @Override
    public void removeGood(int idComp, GoodType type) throws RemoteException{
        executors.submit(() ->
            controller.removeGoodFromModel(idComp, type)
        );
    }

    @Override
    public void addGoodPendingList(String nickname, GoodType type) throws RemoteException{
        executors.submit(() ->
            controller.addPendingGood(nickname, type)
        );
    }

    @Override
    public void setPlayerOnPlanet(String nickname, int choice) throws RemoteException{
        executors.submit(() ->
                controller.setPlayerOnPlanet(nickname,choice)
        );
    }

    @Override
    public void meteorHit(MeteorType type, Direction direction, int number, Cordinate cordinate) throws RemoteException{
        executors.submit(() -> {
            controller.meteorHit(type, direction, number, cordinate);
        });
    }

    @Override
    public void removeBattery(int batteryDepotId) throws RemoteException{
        executors.submit(() -> {
            controller.removeBatteryFromModel(batteryDepotId);
        });
    }

    @Override
    public void removeComponent(String nickname, Cordinate cord) throws RemoteException{
        executors.submit(() -> {
            controller.removeComponentFromModel(nickname, cord);
        });
    }

    @Override
    public void cannonHit(CannonType type, Direction direction, int number) throws RemoteException{
        executors.submit(() -> {
            controller.cannonHit(type, direction, number);
        });
    }

    @Override
    public void startedHourglass(int idGame) throws RemoteException{
        executors.submit(() -> {
            controller.setPendingHourglass(true);
            controller.incrementHourglassCounter();
        });
    }

    @Override
    public void leaveFlight(String nickname) throws RemoteException{
        executors.submit(() -> {
            controller.leaveFlightFromModel(nickname);
        });
    }

    @Override
    public void addCrewMember(String nickname, Cordinate cordinate, GuestType guestType) throws RemoteException{
        executors.submit(() -> {
            controller.addCrewToModel(nickname, cordinate, guestType);
        });
    }

    @Override
    public void notifyCrash(String nickname) throws RemoteException{
        executors.submit(() -> {
            controller.notifyCrash(nickname);
        });
    }
}