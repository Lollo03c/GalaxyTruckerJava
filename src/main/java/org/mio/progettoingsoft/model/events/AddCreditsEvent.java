package org.mio.progettoingsoft.model.events;

import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.server.ServerController;
import org.mio.progettoingsoft.utils.Logger;

import java.util.Map;

public final class AddCreditsEvent extends Event{
    private final int addedCredits;
    public AddCreditsEvent(String nickname , int addedCredits) {
        super(nickname);

        Logger.debug(nickname + " earned " + addedCredits + "value");
        this.addedCredits = addedCredits;
    }

    @Override
    public void send(Map<String, VirtualClient> clients) {
        for (VirtualClient client : clients.values()){
            try {
                client.addCredits(nickname, addedCredits);
            } catch (Exception e) {
                ServerController.getInstance().handleGameCrash(e, nickname, 0);
            }
        }
    }
}
