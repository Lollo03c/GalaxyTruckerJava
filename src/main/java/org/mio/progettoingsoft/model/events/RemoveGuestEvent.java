package org.mio.progettoingsoft.model.events;

import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.utils.Logger;

import java.util.Map;

public final class RemoveGuestEvent extends Event{
    private final int idComp;

    public RemoveGuestEvent(String nickname, int idComp) {
        super(nickname);

        Logger.debug("Removed crew memeber from " + idComp);
        this.idComp = idComp;
    }

    public int getIdComp() {
        return idComp;
    }

    @Override
    public void send(Map<String, VirtualClient> clients){
        for (VirtualClient client : clients.values()){
            try {
                client.removeCrew(idComp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
