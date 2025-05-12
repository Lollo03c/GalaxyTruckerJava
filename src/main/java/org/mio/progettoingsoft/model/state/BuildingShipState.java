package org.mio.progettoingsoft.model.state;

import org.mio.progettoingsoft.exceptions.InvalidInputException;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.network.client.ClientController;
import org.mio.progettoingsoft.network.input.SetupInput;
import org.mio.progettoingsoft.network.message.CoveredComponentMessage;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.views.Tui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BuildingShipState extends ClientState{


    @Override
    public Optional<Message> processTui(Tui view) throws InvalidInputException{
        List<Integer> possibleOptions = new ArrayList<>();
        possibleOptions.addAll(List.of(1, 2, 3));

        System.out.println("1 : Draw a covered component");
        System.out.println("2 : Pick an uncovered component");
        System.out.println("3 : Look a player's shipboard");



        if (ClientController.get().getGame().getGameMode().equals(GameMode.NORMAL)) {
            System.out.println("4 : Look a deck of adventure cards");
            possibleOptions.add(4);
        }

        int chosen = Integer.parseInt(view.readInput());

        Message response = null;
        switch (chosen) {
            case 1 -> response = new CoveredComponentMessage(controller.getGame().getIdGame(), controller.getNickname(), -1);

        }

        controller.setNextState(new WaitingState(WaitingState.WaitingType.GENERIC));
        return Optional.of(response);
    }
}
