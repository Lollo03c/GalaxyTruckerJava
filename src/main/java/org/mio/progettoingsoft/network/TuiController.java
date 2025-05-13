package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.exceptions.InvalidInputException;
import org.mio.progettoingsoft.model.state.ClientState;
import org.mio.progettoingsoft.model.state.ConnectionSetup;
import org.mio.progettoingsoft.network.client.ClientController;
import org.mio.progettoingsoft.network.input.Input;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.views.Tui;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.Optional;

public class TuiController extends ClientController {
    private final Tui view;

    public TuiController() {
        super();
        this.view = new Tui(this);
    }

    @Override
    public void run() {
        state = new ConnectionSetup();

        Optional<Message> response = Optional.empty();
        while(true) {
                synchronized (stateLock2){
                    try {

                        response = state.processTui(view);

                        try {
                            if (response.isPresent()) {
                                client.sendToServer(response.get());
                                response = Optional.empty();
                            }
                        } catch (Exception e) {
                        }
                    }
                    catch (InvalidInputException e) {
                        System.out.println(e.getMessage());
                    }
                    setGameState(nextState);
                }
        }
    }

    @Override
    public void handleWrongNickname(String nickname){
            if (this.getGameState().equals(GameState.WAITING)) {
                System.out.println("Nickname '" + nickname + "' already taken. Try something else");
                setGameState(GameState.NICKNAME_REQUEST);
            }
    }
}
