package org.mio.progettoingsoft.views;

import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.network.input.Input;

public interface VirtualView {
    Input gameMenu(GameState gameState);

}
