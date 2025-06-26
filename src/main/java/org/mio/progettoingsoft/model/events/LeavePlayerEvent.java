package org.mio.progettoingsoft.model.events;

import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.server.ServerController;

import java.util.Map;

public final class LeavePlayerEvent extends Event{
    public LeavePlayerEvent(String nickname) {
        super(nickname);
    }

    @Override
    public void send(Map<String, VirtualClient> clients) {
        for (VirtualClient client : clients.values()){
            try{
                client.leaveFlight(nickname);
            } catch (Exception e) {
                ServerController.getInstance().handleGameCrash(e, nickname, 0);
            }
        }
    }
}
