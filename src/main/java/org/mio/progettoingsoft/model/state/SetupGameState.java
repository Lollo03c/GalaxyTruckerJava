package org.mio.progettoingsoft.model.state;

import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.exceptions.InvalidInputException;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.network.client.Client;
import org.mio.progettoingsoft.network.client.ClientController;
import org.mio.progettoingsoft.network.input.SetupInput;
import org.mio.progettoingsoft.network.message.GameSetupMessage;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.views.Tui;

import java.util.Optional;

public class SetupGameState extends ClientState{

    public Optional<Message> processTui(Tui view) throws InvalidInputException{
        System.out.print("Select number of players (2-4): ");
        int nPlayers = Integer.parseInt(view.readInput());

        System.out.println("Select Game Mode : ");
        System.out.println("1 : Easy Mode ");
        System.out.println("2 : Normal Mode");
        System.out.print("Make your choice : ");
        int chosenMode = Integer.parseInt(view.readInput());
        GameMode mode = chosenMode == 1 ? GameMode.EASY : GameMode.NORMAL;

        Message response = ClientController.get().handleSetupGame(mode, nPlayers);

        ClientController.get().setNextState(new WaitingState(WaitingState.WaitingType.GENERIC));
        return Optional.of(response);
    }
}
