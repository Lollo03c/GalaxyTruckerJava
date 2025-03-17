package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.Direction;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.ShipBoard;

import java.util.Optional;

public class LightCannon extends CannonPenalty {

    public LightCannon(Direction direction) {
        super(direction);
    }

    @Override
    public void apply(Player player, int value) {
        ShipBoard board = player.getShipBoard();
        Optional<Component> hitComponent = findHit(player, value);

        if (hitComponent.isEmpty())
            return;

        boolean activedShield = player.getView().askShield(direction);

        if (activedShield){
            board.removeEnergy();
        }
        else{
            board.removeComponent(hitComponent.get());
        }
    }
}
