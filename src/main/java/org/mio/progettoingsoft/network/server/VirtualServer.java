package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.advCards.sealed.SldStardust;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.model.enums.GameInfo;

import java.rmi.Remote;
import java.util.List;

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
    void takeBuild(int idGame, String nickname) throws Exception;

    void endBuild(int idGame, String nickname) throws Exception;
    void choosePlace(int idGame, String nickname, int place) throws Exception;
    void applyStardust(int idGame, String nickname, SldStardust card) throws Exception;

    void activateDoubleEngine(int idGame, String nickname, int number) throws Exception;

    void leaveFlight(int idGame, String nickname) throws Exception;

    void drawCard(int idGame, String nickname) throws Exception;
    void applyEffect(int idGame, String nickname) throws Exception;
    void skipEffect(int idGame, String nickname, int idCard) throws Exception;
    void crewRemove(int idGame, String nickname, List<Cordinate> cordsToRemove) throws Exception;
    void addGood(int idGame, String nickname, int compId, GoodType type) throws Exception;
    void removeGood(int idGame, String nickaname, int compId, GoodType type) throws Exception;
    void activateDoubleDrills(int idGame, String nickname, List<Cordinate> drillCordinates) throws Exception;
    void landOnPlanet(int idGame, String nickname, int choice)throws Exception;
    void activateSlaver(int idGame,String nickname,List<Cordinate> activatedDrills,boolean wantsToActivate) throws Exception;

    void setRollResult(int idGame, String nickname, int number) throws Exception;
    void removeBattery(int idGame, String nickname, int quantity) throws Exception;
    void advanceMeteor(int idGame, String nickname) throws Exception;
    void advanceCannon(int idGame, String nickname) throws Exception;
    void removeComponent(int idGame, String nickname, Cordinate cordinate) throws Exception;
    void startHourglass(int idGame) throws Exception;
}
