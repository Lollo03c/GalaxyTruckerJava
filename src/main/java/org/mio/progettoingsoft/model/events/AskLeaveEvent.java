package org.mio.progettoingsoft.model.events;

import org.mio.progettoingsoft.advCards.sealed.CardState;
import org.mio.progettoingsoft.network.client.VirtualClient;

import java.util.Map;

public final class AskLeaveEvent extends Event{
    public AskLeaveEvent(String nickname) {
        super(nickname);
    }

    @Override
    public void send(Map<String, VirtualClient> clients) {
        for (VirtualClient client : clients.values()){
//            try{
//                client.setCardState(CardState.ASK_LEAVE);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
        }
    }
}
