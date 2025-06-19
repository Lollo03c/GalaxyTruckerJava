package org.mio.progettoingsoft.network.messages;

import java.util.Collections;
import java.util.List;

public final class BatteryMessage extends Message{
    private final List<Integer> batteryDepotId;
    private final int quantity;

    public BatteryMessage(int gameId, String nickname, List<Integer> batteryDepotId) {
        super(gameId, nickname);
        this.batteryDepotId = batteryDepotId;
        this.quantity = 0;
    }

    public BatteryMessage(int gameId, String nickname, int quantity){
        super(gameId, nickname);
        this.batteryDepotId = Collections.emptyList();
        this.quantity = quantity;
    }


    public List<Integer> getBatteryDepotId() {
        return batteryDepotId;
    }

    public int getQuantity() {
        return quantity;
    }
}
