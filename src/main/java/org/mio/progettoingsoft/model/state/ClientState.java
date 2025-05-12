package org.mio.progettoingsoft.model.state;

import org.mio.progettoingsoft.exceptions.InvalidInputException;
import org.mio.progettoingsoft.model.interfaces.GameClient;
import org.mio.progettoingsoft.network.client.ClientController;
import org.mio.progettoingsoft.network.input.Input;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.views.Tui;

import java.util.Optional;

public abstract class ClientState {
    protected ClientController controller = ClientController.get();

    public abstract Optional<Message> processTui(Tui view) throws InvalidInputException;
}