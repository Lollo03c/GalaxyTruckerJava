package org.mio.progettoingsoft.model.events;

import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.server.ServerController;

import java.util.Map;

public final class SetStateEvent extends Event{
    private final GameState gameState;

    public SetStateEvent(String nickname, GameState gameState) {
        super(nickname);
        this.gameState = gameState;
    }

    @Override
    public void send(Map<String, VirtualClient> clients) {
        try{
            clients.get(nickname).setState(gameState);
        } catch (Exception e) {
            ServerController.getInstance().handleGameCrash(e, nickname, 0);
        }
    }
}
