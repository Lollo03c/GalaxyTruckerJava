package org.mio.progettoingsoft.model.events;

import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.network.client.VirtualClient;

import java.util.Map;

public final class RemovePendingGoodEvent extends Event{
    private final GoodType type;

    public RemovePendingGoodEvent(String nickname, GoodType type) {
        super(nickname);
        this.type = type;
    }

    public GoodType getType() {
        return type;
    }

    @Override
    public void send(Map<String, VirtualClient> clients){
        for (VirtualClient client : clients.values()){
            try{
                client.removeGoodPendingList(nickname, type);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
