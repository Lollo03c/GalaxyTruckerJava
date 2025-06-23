package org.mio.progettoingsoft.model.events;

import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.network.client.VirtualClient;

import java.util.Map;

public final class RemoveGoodEvent extends Event{
    private final int idComp;
    private final GoodType goodType;

    public RemoveGoodEvent(String nickname, int idComp, GoodType goodType) {
        super(nickname);
        this.idComp = idComp;
        this.goodType = goodType;
    }

    public int getIdComp() {
        return idComp;
    }

    public GoodType getGoodType() {
        return goodType;
    }

    @Override
    public void send(Map<String, VirtualClient> clients){
        for (VirtualClient client : clients.values()){
            try{
                client.removeGood(idComp, goodType);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
