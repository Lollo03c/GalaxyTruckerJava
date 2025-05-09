package org.mio.progettoingsoft.model.interfaces;

import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.message.Message;

import java.util.Map;

public interface GameServer {

    void startGame();
    void setupGame(GameMode mode, int numPlayers);
    int getIdGame();
    int getNumPlayers();
    GameMode getGameMode();
    Map<String, VirtualClient> getClients();
    void addPlayer(String nickname, VirtualClient client);
    boolean isFull();
    boolean askSetting();
    void addReceivedMessage(Message message);

}
