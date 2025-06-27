package org.mio.progettoingsoft.model.events;

import org.mio.progettoingsoft.model.components.GoodType;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.server.ServerController;

import java.util.Map;

public final class AddGoodEvent extends Event{
    private final int idComp;
    private final GoodType good;

    public AddGoodEvent(String nickname, int idComp, GoodType good) {
        super(nickname);
        this.idComp = idComp;
        this.good = good;
    }

    public int getIdComp() {
        return idComp;
    }

    public GoodType getGood() {
        return good;
    }

    @Override
    public void send(Map<String, VirtualClient> clients){
        for (VirtualClient client : clients.values()){
            try{
                client.addGood(idComp, good);
            } catch (Exception e) {
                ServerController.getInstance().handleGameCrash(e, nickname, 0);
            }
        }
    }
}
