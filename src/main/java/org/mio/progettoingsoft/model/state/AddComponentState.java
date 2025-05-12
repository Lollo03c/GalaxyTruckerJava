package org.mio.progettoingsoft.model.state;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.network.client.ClientController;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.views.Tui;

import java.util.Optional;

public class AddComponentState extends ClientState {
    private final int idComp;

    public AddComponentState(int idComp){
        this.idComp = idComp;
    }

    @Override
    public Optional<Message> processTui(Tui view){
        Component component = controller.getFlyBoard().getComponentById(idComp);
        System.out.println("You draw component # " + component.getTuiString());

        System.out.println(controller.getFlyBoard().getPlayerByUsername(controller.getNickname()).getShipBoard().toString());

        System.out.println("Inserire la riga : ");
        System.out.println("Inserire la colonna : ");

        controller.setNextState(new BuildingShipState());


        return Optional.empty();
    }
}
