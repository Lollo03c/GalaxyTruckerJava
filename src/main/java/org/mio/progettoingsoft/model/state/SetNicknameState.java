package org.mio.progettoingsoft.model.state;

import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.exceptions.InvalidInputException;
import org.mio.progettoingsoft.network.client.ClientController;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.message.NicknameMessage;
import org.mio.progettoingsoft.views.Tui;

import java.util.Optional;

public class SetNicknameState extends ClientState {

    @Override
    public Optional<Message> processTui(Tui view) throws InvalidInputException{
        System.out.print("Select a nickname : ");

        String nickname = view.readInput();

        Message message = ClientController.get().handleNicknameInput(nickname);
        ClientController.get().setNextState(new WaitingState(WaitingState.WaitingType.GENERIC));


        return Optional.ofNullable(message);
    }
}
