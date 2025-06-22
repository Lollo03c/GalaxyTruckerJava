package org.mio.progettoingsoft.model.events;

import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.utils.Logger;

import java.util.Map;

public final class RemoveEnergyEvent extends Event{
    private final int idComp;

    public RemoveEnergyEvent(String nickname, int idComp) {
        super(nickname);

        Logger.debug("removed energy from " + idComp);
        this.idComp = idComp;
    }

    @Override
    public void send(Map<String, VirtualClient> clients) {
        for (VirtualClient client : clients.values()){
            try {
                client.removeBattery(idComp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
