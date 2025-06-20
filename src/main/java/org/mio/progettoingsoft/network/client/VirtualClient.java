package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.Direction;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.advCards.sealed.CardState;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.model.enums.CannonType;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.enums.MeteorType;

import java.rmi.Remote;
import java.util.List;
import java.util.Map;

/**
 * Represents the server-side view of a connected client.
 *
 * <p>
 * This interface defines the methods that the server can invoke on the client side.
 * It represents the "view" of the client from the server's perspective and is used
 * to send updates or requests to the client during the game.
 * </p>
 *
 * <p>Each implementation of this interface should handle the logic required to update
 * the client UI or internal state accordingly.</p>
 *
 * <p>This interface abstracts the communication channel (RMI or Socket) and allows the
 * server to interact with clients in a uniform way.</p>
 *
 * <p>
 * Typical implementations of this interface are:
 * <ul>
 *  <li>{@code ClientRmi} – for clients connected via Java RMI</li>
 *  <li>{@code ClientSocket} – for clients connected via TCP Sockets</li>
 * </ul>
 * </p>
 */
public interface VirtualClient extends Remote {

    void ping(String msg) throws Exception;
    void setNickname(String nickname) throws Exception;
    void askGameSettings(String nickname) throws Exception;
    void wrongNickname() throws Exception;
    void setGameId(int gameId) throws Exception;

    void setState(GameState state) throws Exception;
    void setCardState(CardState state) throws Exception;

    void setFlyBoard(GameMode mode, Map<String, HousingColor> players, List<List<Integer>> decks) throws Exception;

    void setInHandComponent(int idComponent) throws Exception;
    void addComponent(String nickname, int idComp, Cordinate cordinate, int rotations) throws Exception;
    void addUncoveredComponent(int idComp) throws Exception;
    void removeUncovered(int idComp) throws Exception;

    void removeDeck(Integer deckNumber) throws Exception;
    void setInHandDeck(int deck) throws Exception;
    void addAvailableDeck(int deckNumber) throws Exception;

    void setBuiltShip(String nickname) throws Exception;

    void setAvailablePlaces(List<Integer> availablePlaces) throws Exception;
    void addOtherPlayerToCircuit(String nickname, int place) throws Exception;

    void setPlayedCard(int idCard) throws Exception;
    void advancePlayer(String nickname, int steps, int energyToRemove) throws Exception;
    void addCredits(String nickname, int credits) throws Exception;
    void removeCrew(String nickname, List<Cordinate> housingCordinates) throws Exception;
    void addGood(int idComp, GoodType type) throws Exception;
    void removeGoodPendingList(String nickname, GoodType type) throws Exception;

    void removeGood(int idComp, GoodType type) throws Exception;
    void addGoodPendingList(String nickname, GoodType type) throws Exception;
    void setPlayerOnPlanet(String nickname, int choice) throws Exception;
    void genericChoiceError(String msg) throws Exception;

    void meteorHit(MeteorType type, Direction direction, int number) throws Exception;

    void removeBatteries(List<Integer> batteryDepotId) throws Exception;
    void removeComponent(String nickname, Cordinate cord) throws Exception;

    void cannonHit(CannonType type, Direction direction, int number) throws Exception;

    void startedHourglass(int idGame) throws Exception;
}
