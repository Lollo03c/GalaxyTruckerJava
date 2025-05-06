package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.views.Tui;

import java.io.IOException;
import java.rmi.NotBoundException;

public class TuiController extends ClientController {
    private final Tui view;

    public TuiController() {
        super();
        this.view = new Tui();
    }

    @Override
    public void run() {
        while(true) {
            try {
                handleInput(view.gameMenu(gameState));
            } catch (NotBoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
