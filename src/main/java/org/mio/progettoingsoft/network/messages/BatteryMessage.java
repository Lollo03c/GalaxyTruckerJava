package org.mio.progettoingsoft.network.messages;

import java.util.Collections;
import java.util.List;

public final class BatteryMessage extends Message{
    private final int batteryDepotId;
    private final int quantity;

    public BatteryMessage(int gameId, String nickname, int batteryDepotId) {
        super(gameId, nickname);
        this.batteryDepotId = batteryDepotId;
        this.quantity = 0;
    }


    public int getQuantity() {
        return quantity;
    }

    public int getBatteryDepotId() {
        return batteryDepotId;
    }
}
