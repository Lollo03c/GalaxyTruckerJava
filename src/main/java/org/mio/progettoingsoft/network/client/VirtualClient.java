package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.model.enums.GameMode;

import java.rmi.Remote;
import java.rmi.RemoteException;
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

    void setFlyBoard(GameMode mode, Map<String, HousingColor> players) throws Exception;

    void setInHandComponent(int idComponent) throws Exception;
    void addComponent(String nickname, int idComp, Cordinate cordinate, int rotations) throws Exception;
    void addUncoveredComponent(int idComp) throws Exception;
    void removeUncovered(int idComp) throws Exception;

    void removeDeck(Integer deckNumber) throws Exception;
    void setInHandDeck(int deck) throws Exception;
    void addAvailableDeck(int deckNumber) throws Exception;
}
