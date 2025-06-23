package org.mio.progettoingsoft.model.events;

import org.mio.progettoingsoft.network.client.VirtualClient;

import java.util.Map;

public final class GenericErrorEvent extends Event{
    private final String message;

    public GenericErrorEvent(String nickname, String message) {
        super(nickname);
        this.message = message;
    }

    @Override
    public void send(Map<String, VirtualClient> clients) {
        try {
            clients.get(nickname).genericChoiceError(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
