package org.mio.progettoingsoft.model.events;

import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.utils.Logger;

import java.util.Map;

public abstract sealed class Event permits AddCreditsEvent, AddGoodEvent, AddPendingGoodEvent, AddPlayerCircuit, CannonHitEvent, GenericErrorEvent, MetoriteEvent, MovePlayerEvent, RemoveComponentEvent, RemoveEnergyEvent, RemoveGoodEvent, RemoveGuestEvent, RemovePendingGoodEvent, SetCardStateEvent, SetPlayedCard, SetStateEvent {
    protected final String nickname;

    public Event(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void send(Map<String, VirtualClient> clients){
        Logger.error("invio messaggio non supportato");
    }
}
