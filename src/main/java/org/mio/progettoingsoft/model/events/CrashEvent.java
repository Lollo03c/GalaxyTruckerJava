package org.mio.progettoingsoft.model.events;

import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.server.ServerController;

import java.util.Map;

public final class CrashEvent extends Event {

    public CrashEvent(String nickname) {
        super(nickname);
    }

    @Override
    public void send(Map<String, VirtualClient> clients) {
        for (String clientNickname : clients.keySet()) {
            try{
                if (!clientNickname.equals(nickname))
                    clients.get(clientNickname).notifyCrash(nickname);
            } catch (Exception e) {
                ServerController.getInstance().handleGameCrash(e, nickname, 0);
            }
        }
    }
}
