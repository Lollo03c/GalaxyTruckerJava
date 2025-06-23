package org.mio.progettoingsoft.model.events;

import org.mio.progettoingsoft.network.client.VirtualClient;

import java.util.Map;

public final class MovePlayerEvent extends Event {
    private final int steps;

    public MovePlayerEvent(String nickname, int steps) {
        super(nickname);
        this.steps = steps;
    }

    public int getSteps() {
        return steps;
    }

    @Override
    public void send(Map<String, VirtualClient> clients) {
        for (VirtualClient client : clients.values()){
            try{
                client.advancePlayer(nickname, steps);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }
}
