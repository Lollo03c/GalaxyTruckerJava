package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.Direction;
import org.mio.progettoingsoft.Player;

import java.util.Optional;

public class HeavyCannon extends CannonPenalty {

    public HeavyCannon(Direction direction) {
        super(direction);
    }

    @Override
    public void apply(Player player, int value) {
        Optional<Component> hitComponent = findHit(player, value);

        if (hitComponent.isEmpty())
            return;

        player.getShipBoard().removeComponent(hitComponent.get());
    }
}
