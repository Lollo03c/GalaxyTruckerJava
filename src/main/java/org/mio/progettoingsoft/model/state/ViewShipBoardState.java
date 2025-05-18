package org.mio.progettoingsoft.model.state;

import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.exceptions.IncorrectFlyBoardException;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.views.Tui;
import org.mio.progettoingsoft.views.tui.VisualShipboardNormal;

import java.util.Optional;

public class ViewShipBoardState extends ClientState {

    @Override
    public Optional<Message> processTui (Tui view){
        System.out.println("Players name ");
        for (Player pl : controller.getFlyBoard().getPlayers()){
            System.out.println(pl.getNickname());
        }
        System.out.println("Insert player's nickname : ");
        String otherNick = view.readInput();

        try {
            controller.getFlyBoard().getPlayerByUsername(otherNick).getShipBoard().drawShipboard();
        }
        catch (IncorrectFlyBoardException e){
            System.out.println("Player not found");
        }

        controller.setNextState(new BuildingShipState());
        return Optional.empty();
    }
}
