package org.mio.progettoingsoft.model.events;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.server.ServerController;

import java.util.Map;

public final class RemoveComponentEvent extends Event{
    private final Cordinate cordinate;

    public RemoveComponentEvent(String nickname, Cordinate cordinate) {
        super(nickname);
        this.cordinate = cordinate;
    }

    public Cordinate getCordinate() {
        return cordinate;
    }

    @Override
    public void send(Map<String, VirtualClient> clients){
<<<<<<< Updated upstream
        for (String nick : clients.keySet()){
            if (! nick.equals(nickname)) {
                try {
                    clients.get(nick).removeComponent(nickname, cordinate);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
=======
        for (VirtualClient client : clients.values()){
            try{
                client.removeComponent(nickname, cordinate);
            } catch (Exception e) {
                ServerController.getInstance().handleGameCrash(e, nickname, 0);
>>>>>>> Stashed changes
            }
        }
    }
}
