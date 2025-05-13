package org.mio.progettoingsoft.model.state;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.ShipBoard;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;
import org.mio.progettoingsoft.network.message.AddComponentMessage;
import org.mio.progettoingsoft.network.message.AddUncoveredMessage;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.views.ShipCell;
import org.mio.progettoingsoft.views.Tui;
import org.mio.progettoingsoft.views.VisualShipboard;

import java.util.Optional;

public class AddComponentState extends ClientState {
    private final int idComp;

    public AddComponentState(int idComp){
        this.idComp = idComp;
    }

    @Override
    public Optional<Message> processTui(Tui view) {
        Message message = null;

        Component component = controller.getFlyBoard().getComponentById(idComp);
        System.out.println("You draw component # " + component.getTuiString());
        ShipBoard shipBoard = controller.getFlyBoard().getPlayerByUsername(controller.getNickname()).getShipBoard();

        new ShipCell(controller.getFlyBoard().getComponentById(idComp)).drawCell();
        new VisualShipboard(shipBoard).drawShipboard();

        System.out.println("1 : Insert to the shipboard");
        System.out.println("2 : Return to the flyboard as uncovered");
        System.out.println("3 : save for later");
        int chosenAction = Integer.parseInt(view.readInput());

        if (chosenAction == 2) {
            new AddUncoveredMessage(controller.getGame().getIdGame(), controller.getNickname(), idComp);
            //da mandare un messaggio al server indicando che lo ritorna scoperto
        } else if (chosenAction == 3) {
            controller.setNextState(new BookComponentState(idComp));
        } else if (chosenAction == 1) {


            int row = 0, column = 0, rotations = 0;

            System.out.println("Insert the row : ");
            row = Integer.parseInt(view.readInput());

            System.out.println("Insert the column : ");
            column = Integer.parseInt(view.readInput());

            System.out.println("Insert the rotation");
            rotations = Integer.parseInt(view.readInput());

            try {
                Cordinate cord = new Cordinate(row, column);
                shipBoard.addComponentToPosition(idComp, cord, rotations);

                message = new AddComponentMessage(controller.getGame().getIdGame(), controller.getNickname(), idComp,  cord, rotations);
                controller.setNextState(new BuildingShipState());
            } catch (IncorrectShipBoardException e) {
                System.out.println("Impossible to add to the current shipboard. Try again");
                controller.setNextState(new AddComponentState(idComp));
            }
        }


        return Optional.ofNullable(message);
    }
}
