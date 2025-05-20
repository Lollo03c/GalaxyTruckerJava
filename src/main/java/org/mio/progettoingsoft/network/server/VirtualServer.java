package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.model.enums.GameInfo;

import java.rmi.Remote;

/**
 * Interfaccia che definisce i metodi del server che devono essere resi accessibili ai client per poter interagire con
 * la logica del server (controller).
 */
public interface VirtualServer extends Remote {
    void handleNickname(int tempIdClient, String nickname) throws Exception;
    void handleGameInfo(GameInfo gameInfo, String nickname) throws Exception;
    void getCoveredComponent(int idGame, String nickname) throws Exception;
    void addComponent(int idGame, String nickname, int idComp, Cordinate cordinate, int rotations) throws Exception;
    void discardComponent(int idGame, int idComponent) throws Exception;
    void drawUncovered(int idGame, String nickname, int idComponent) throws Exception;
    void bookDeck(int idGame, String nickname, int deckNumber) throws Exception;
    void freeDeck(int idGame, String nickname, int deckNumber) throws Exception;
}
