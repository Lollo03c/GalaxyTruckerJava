package org.mio.progettoingsoft.model.events;

import org.mio.progettoingsoft.advCards.Planet;
import org.mio.progettoingsoft.advCards.sealed.CardState;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.server.ServerController;

import java.util.Map;

public final class LandOnPlanetEvent extends Event{
    private final Planet planet;

    public LandOnPlanetEvent(String nickname, Planet planet) {
        super(nickname);
        this.planet = planet;
    }

    @Override
    public void send(Map<String, VirtualClient> clients) {
        try{
            for (GoodType type : planet.getGoods()){
                clients.get(nickname).addGoodPendingList(nickname, type);
            }
            clients.get(nickname).setCardState(CardState.GOODS_PLACEMENT);
        } catch (Exception e) {
            ServerController.getInstance().handleGameCrash(e, nickname, 0);
        }
    }
}
