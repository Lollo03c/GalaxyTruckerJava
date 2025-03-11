package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.AdventureCard;
import org.mio.progettoingsoft.Player;

import java.util.Collections;
import java.util.List;

public class Stardust extends AdventureCard {
    public Stardust(int id, int level) {
        super(id,level);
    }

    public void start(List<Player> scoryBoard){
        Collections.reverse(scoryBoard);
        for(Player p : scoryBoard){
            int daysLost = p.getShipBoard().getExposedConnectors();
            movedays(p, -daysLost);
        }
    }
}
