package org.mio.progettoingsoft.model.state;

import org.mio.progettoingsoft.exceptions.InvalidInputException;
import org.mio.progettoingsoft.network.ConnectionType;
import org.mio.progettoingsoft.network.client.ClientController;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.views.Tui;

import java.util.Optional;

public class ConnectionSetup extends ClientState{

    private final String hostAddress = "127.0.0.1";
    private final int rmiPort = 1099;
    private final int socketPort = 1234;
    private final String serverName = "localhost";

    @Override
    public Optional<Message> processTui(Tui view) throws InvalidInputException{
        System.out.println("Select connection type: ");
        System.out.println("1: RMI");
        System.out.println("2: Socket");
        System.out.print("Make your choice: ");

        String input = view.readInput();

        try {
            int chosenAction = Integer.parseInt(input);

            if (!(chosenAction == 1 || chosenAction == 2))
                throw new InvalidInputException("Not a valid choose. Try again\n");

            boolean isRmi = chosenAction == 1;
            ConnectionType connectionType = new ConnectionType(isRmi, hostAddress, isRmi ? rmiPort : socketPort, serverName);
            ClientController.get().handleConnectionTypeInput(connectionType);

            ClientController.get().setNextState(new WaitingState(WaitingState.WaitingType.GENERIC));
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Not a valid choose. Try again\n");
        }

        return Optional.empty();
    }
}
