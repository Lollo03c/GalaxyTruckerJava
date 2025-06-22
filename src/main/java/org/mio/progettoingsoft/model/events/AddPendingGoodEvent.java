package org.mio.progettoingsoft.model.events;

import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.network.client.VirtualClient;

import java.util.Map;

public final class AddPendingGoodEvent extends Event {
    private final GoodType type;

    public AddPendingGoodEvent(String nickname, GoodType type) {
        super(nickname);
        this.type = type;
    }

    @Override
    public void send(Map<String, VirtualClient> clients) {
        try{
            clients.get(nickname).addGoodPendingList(nickname, type);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
