package org.mio.progettoingsoft.advcards;

import org.mio.progettoingsoft.Direction;
import org.mio.progettoingsoft.Player;

public abstract class Meteor {
    private Direction direction;
    public abstract void hit(Player player);
}
