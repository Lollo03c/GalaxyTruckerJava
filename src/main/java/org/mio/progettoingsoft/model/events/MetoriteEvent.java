package org.mio.progettoingsoft.model.events;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.Direction;
import org.mio.progettoingsoft.model.enums.MeteorType;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.server.ServerController;

import java.util.Map;

public final class MetoriteEvent extends Event{
    private final Direction direction;
    private final int value;
    private final MeteorType type;
    private final Cordinate cordinate;

    public MetoriteEvent(String nickname, Direction direction, int value, MeteorType type, Cordinate cord) {
        super(nickname);
        this.direction = direction;
        this.value = value;
        this.type = type;
        this.cordinate = cord;
    }

    @Override
    public void send(Map<String, VirtualClient> clients) {
        try{
            clients.get(nickname).meteorHit(type, direction, value, cordinate);
        } catch (Exception e) {
            ServerController.getInstance().handleGameCrash(e, nickname, 0);
        }
    }
}
