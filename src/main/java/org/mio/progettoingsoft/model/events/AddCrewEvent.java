package org.mio.progettoingsoft.model.events;

import org.mio.progettoingsoft.model.Cordinate;
import org.mio.progettoingsoft.model.components.GuestType;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.server.ServerController;

import java.util.Map;

public final class AddCrewEvent extends Event{
    private final Cordinate cord;
    private final GuestType type;

    public AddCrewEvent(String nickname, Cordinate cord, GuestType type) {
        super(nickname);
        this.cord = cord;
        this.type = type;
    }

    @Override
    public void send(Map<String, VirtualClient> clients) {
        for (String nick : clients.keySet()){
            if (! nickname.equals(nick)) {
                try {
                    clients.get(nick).addCrewMember(nickname, cord, type);
                } catch (Exception e) {
                    ServerController.getInstance().handleGameCrash(e, nickname, 0);
                }
            }
        }
    }
}
