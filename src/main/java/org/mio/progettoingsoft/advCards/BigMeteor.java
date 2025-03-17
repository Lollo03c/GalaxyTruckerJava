package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.*;

import java.util.List;
import java.util.Optional;

public class BigMeteor extends Meteor {

    public BigMeteor(Direction direction) {
        super(direction);
    }

    @Override
    public void hit(Player player, int value) {
        Optional<Component> hitComponent = findHit(player, value);

        if (hitComponent.isEmpty())
            return;

        Component component = hitComponent.get();

        ShipBoard board = player.getShipBoard();

        List<Component> cannons = board.getComponentsStream()
                .filter(comp -> comp.getDirection() != null)
                .filter(comp -> comp.getDirection().equals(direction))
                .toList();

        boolean singleFounded = cannons.stream()
                .filter(comp -> comp.getFirePower() == 1.0f)
                .anyMatch(comp -> board.getColumnComponent(comp) == value);

        if (!singleFounded){
            boolean doubleFounded = cannons.stream()
                    .filter(comp -> comp.getFirePower() == 2.0f)
                    .anyMatch(comp -> board.getColumnComponent(comp) == value);

            if (doubleFounded){
                boolean activated = player.getView().askOneDoubleDrill();

                if (activated){
                    board.removeEnergy();
                }
                else{
                    board.removeComponent(component);
                }
            }
        }

    }
}
