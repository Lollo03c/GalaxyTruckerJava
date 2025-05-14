package org.mio.progettoingsoft.view;

import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.network.client.ClientController;

public abstract class View implements Runnable {

    protected int idGame;
    protected String nickname;
    protected FlyBoard flyBoard;
}
