package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.GuestType;
import org.mio.progettoingsoft.model.enums.GameInfo;

import java.rmi.Remote;
import java.util.List;
import java.util.Map;

/**
 * Interface defining the server methods that must be made accessible to clients
 * to allow interaction with the server's game logic (controller).
 * All methods throw {@link Exception} to handle potential communication or logic issues.
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
    void takeBuild(int idGame, String nickname) throws Exception;

    void endBuild(int idGame, String nickname) throws Exception;
    void choosePlace(int idGame, String nickname, int place) throws Exception;
    void endValidation(int idGame, String nickname, boolean usedBattery) throws Exception;

    void activateDoubleEngine(int idGame, String nickname, int number) throws Exception;

    void leaveFlight(int idGame, String nickname, boolean leave) throws Exception;

    void drawCard(int idGame, String nickname) throws Exception;
    void applyEffect(int idGame, String nickname) throws Exception;
    void skipEffect(int idGame, String nickname, int idCard) throws Exception;
    void crewRemove(int idGame, String nickname, List<Cordinate> cordsToRemove) throws Exception;
    void addGood(int idGame, String nickname, int compId, GoodType type) throws Exception;
    void removeGood(int idGame, String nickaname, int compId, GoodType type) throws Exception;
    void activateDoubleDrills(int idGame, String nickname, List<Cordinate> drillCordinates) throws Exception;
    void landOnPlanet(int idGame, String nickname, int choice)throws Exception;

    void setRollResult(int idGame, String nickname, int first, int second) throws Exception;
    void advanceMeteor(int idGame, String nickname, boolean destroyed, boolean energy) throws Exception;
    void advanceCannon(int idGame, String nickname, boolean destroyed, boolean energy) throws Exception;
    void removeComponent(int idGame, String nickname, Cordinate cordinate, boolean toAllClient) throws Exception;
    void startHourglass(int idGame) throws Exception;

    void addCrew(int idGame, String nickname, Map<Cordinate, List<GuestType>> addedCrew) throws Exception;
}
