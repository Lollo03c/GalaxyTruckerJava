package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.network.client.ClientController;
import org.mio.progettoingsoft.views.Tui;

import java.io.IOException;
import java.rmi.NotBoundException;

public class TuiController extends ClientController {
    private final Tui view;

    public TuiController() {
        super();
        this.view = new Tui(this);
    }

    @Override
    public void run() {
        while(true) {
                try {
                    handleInput(view.gameMenu(this.getGameState()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
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
