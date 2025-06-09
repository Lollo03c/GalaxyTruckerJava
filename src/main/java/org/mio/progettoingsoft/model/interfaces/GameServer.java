package org.mio.progettoingsoft.model.interfaces;

import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.GameController;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.network.client.VirtualClient;

import java.util.Map;

public interface GameServer {

    void startGame();
    void addPlayer(String nickname, VirtualClient client);
    void setupGame(GameMode mode, int numPlayers);
    int getIdGame();
    int getNumPlayers();
    GameMode getGameMode();
    Map<String, VirtualClient> getClients();
    boolean isFull();
    boolean askSetting();
//    void addReceivedMessage(Message message);

    FlyBoard getFlyboard();
    GameController getController();

}
