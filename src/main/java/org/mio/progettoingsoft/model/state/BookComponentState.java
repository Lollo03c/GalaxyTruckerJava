package org.mio.progettoingsoft.model.state;

import org.mio.progettoingsoft.ShipBoard;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;
import org.mio.progettoingsoft.network.message.AddBookedMessage;
import org.mio.progettoingsoft.network.message.AddComponentMessage;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.views.ShipCell;
import org.mio.progettoingsoft.views.Tui;

import java.util.List;
import java.util.Optional;

public class BookComponentState extends ClientState{
    int idComp;

    public BookComponentState(int idComp){
        this.idComp = idComp;
    }

    @Override
    public Optional<Message> processTui(Tui view){
        ShipBoard ship = controller.getFlyBoard().getPlayerByUsername(controller.getNickname()).getShipBoard();
        Message message = null;
        try {
            ship.addBookedComponent(idComp);
            controller.setNextState(new BuildingShipState());
            message = new AddBookedMessage(controller.getGame().getIdGame(), controller.getNickname(), idComp, -1);

       } catch (IncorrectShipBoardException e) {
            System.out.println("Booked spaces already full");
            System.out.println("Decide what to swap");

            List<Optional<Integer>> bookComponents = ship.getBookedComponents();

            for (Optional<Integer> comp : bookComponents){
                if (comp.isPresent()) {
                    new ShipCell(controller.getFlyBoard().getComponentById(comp.get())).computeComponent();
                }
            }

            System.out.println("1 : swap first component");
            System.out.println("2 : swap second component");
            System.out.println("3 : do not swap");

            int chosenAction = Integer.parseInt(view.readInput());

            if (chosenAction == 1){
                int removedComp = ship.getBookedComponents().get(0).get();
                ship.getBookedComponents().set(0, Optional.of(idComp));
                message = new AddBookedMessage(controller.getGame().getIdGame(), controller.getNickname(), idComp, removedComp);
                controller.setNextState(new AddComponentState(removedComp));
            }
            else if (chosenAction == 2){
                int removedComp = ship.getBookedComponents().get(0).get();
                ship.getBookedComponents().set(1, Optional.of(idComp));
                message = new AddBookedMessage(controller.getGame().getIdGame(), controller.getNickname(), idComp, removedComp);
                controller.setNextState(new AddComponentState(removedComp));
            }
            else
                controller.setNextState(new AddComponentState(idComp));
        }
        return Optional.ofNullable(message);
    }
}
