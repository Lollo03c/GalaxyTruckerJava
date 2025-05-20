package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.network.server.VirtualServer;

public abstract class Client {
    protected ClientController controller = ClientController.getInstance();

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

}

