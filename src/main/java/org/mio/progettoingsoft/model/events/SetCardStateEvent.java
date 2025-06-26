package org.mio.progettoingsoft.model.events;

import org.mio.progettoingsoft.advCards.sealed.CardState;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.server.ServerController;

import java.util.Map;

public final class SetCardStateEvent extends Event{
    private final CardState cardState;

    public SetCardStateEvent(String nickname, CardState cardState) {
        super(nickname);
        this.cardState = cardState;
    }

    public CardState getCardState() {
        return cardState;
    }

    @Override
    public void send(Map<String, VirtualClient> clients) {
        try {
            clients.get(nickname).setCardState(cardState);
        } catch (Exception e) {
            ServerController.getInstance().handleGameCrash(e, nickname, 0);
        }
    }
}
