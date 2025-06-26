package org.mio.progettoingsoft.model.events;

import org.mio.progettoingsoft.Direction;
import org.mio.progettoingsoft.model.enums.CannonType;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.server.ServerController;

import java.util.Map;

public final class CannonHitEvent extends Event{
    private final CannonType type;
    private final Direction direction;
    private final int value;

    public CannonHitEvent(String nickname, CannonType type, Direction direction, int value) {
        super(nickname);
        this.type = type;
        this.direction = direction;
        this.value = value;
    }

    @Override
    public void send(Map<String, VirtualClient> clients) {
        try{
            clients.get(nickname).cannonHit(type, direction, value);
        } catch (Exception e) {
            ServerController.getInstance().handleGameCrash(e, nickname, 0);
        }
    }
}
