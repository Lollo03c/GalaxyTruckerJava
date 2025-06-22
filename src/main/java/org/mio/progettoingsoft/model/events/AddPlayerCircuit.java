package org.mio.progettoingsoft.model.events;

import org.mio.progettoingsoft.network.client.VirtualClient;

import java.util.Map;

public final class AddPlayerCircuit extends Event{
    private final int place;

    public AddPlayerCircuit(String nickname, int place) {
        super(nickname);
        this.place = place;
    }

    public int getPlace() {
        return place;
    }

    @Override
    public void send(Map<String, VirtualClient> clients){
        for (VirtualClient client : clients.values()){
            try{
                client.addOtherPlayerToCircuit(nickname, place);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
