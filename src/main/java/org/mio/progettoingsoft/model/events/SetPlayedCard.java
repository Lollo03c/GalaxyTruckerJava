package org.mio.progettoingsoft.model.events;

import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.server.ServerController;

import java.util.Map;

public final class SetPlayedCard extends Event{
    private final int idCard;

    public SetPlayedCard(String nickname, int idCard) {
        super(nickname);
        this.idCard = idCard;
    }

    public int getIdCard() {
        return idCard;
    }

    @Override
    public void send(Map<String, VirtualClient> clients) {
        for (VirtualClient client : clients.values()){
            try{
                client.setPlayedCard(idCard);
                client.setState(GameState.NEW_CARD);
            } catch (Exception e) {
                ServerController.getInstance().handleGameCrash(e, nickname, 0);
            }

        }
    }
}
