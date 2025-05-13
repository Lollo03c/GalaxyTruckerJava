package org.mio.progettoingsoft.model.state;

import org.mio.progettoingsoft.exceptions.InvalidInputException;
import org.mio.progettoingsoft.network.client.Client;
import org.mio.progettoingsoft.network.client.ClientController;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.views.Tui;

import java.util.Optional;

public class WaitingState extends ClientState{

    private final ClientController controller = ClientController.get();

    public enum WaitingType{
        GENERIC, PLAYERS;
    }

    private final WaitingType type;

    public WaitingState(WaitingType type){
        this.type = type;
    }

    @Override
    public Optional<Message> processTui(Tui view) throws InvalidInputException{
        synchronized (controller){

            switch (type){
                case GENERIC -> {}
                case PLAYERS -> printPlayers();

            }
            try {
                controller.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Optional.empty();
        }
    }

    private void printPlayers(){
        ClientController controller = ClientController.get();

        System.out.println("Waiting for other players");
        System.out.println("Game ID : " + controller.getGame().getIdGame());
        System.out.println("Game mode : " + controller.getGame().getGameMode());
        System.out.println("Number of Players : " + controller.getGame().getNumPlayers());
        System.out.println();
    }
}
