package org.mio.progettoingsoft.model.events;

import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.server.ServerController;

import java.util.Map;

public final class SetCardPlayedEvent extends Event{
    private final int idCard;

    public SetCardPlayedEvent(String nickname, int idCard) {
        super(nickname);
        this.idCard = idCard;
    }

    @Override
    public void send(Map<String, VirtualClient> clients) {
        for (VirtualClient client : clients.values()){
            try{
                client.setPlayedCard(idCard);
            } catch (Exception e) {
                ServerController.getInstance().handleGameCrash(e, nickname, 0);
            }
        }
    }
}
