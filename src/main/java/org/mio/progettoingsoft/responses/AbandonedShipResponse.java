package org.mio.progettoingsoft.responses;

import org.mio.progettoingsoft.AdvCardType;
import org.mio.progettoingsoft.HourGlass;
import org.mio.progettoingsoft.components.Housing;
import org.mio.progettoingsoft.components.HousingColor;

import java.util.List;

public class AbandonedShipResponse extends Response{
    final boolean acceptEffect;
    final List<Integer  > crewDeleted;

    public AbandonedShipResponse(HousingColor colorPlayer, boolean acceptEffect, List<Integer> crewDeleted){
        super(AdvCardType.ABANDONED_SHIP, colorPlayer);
        this.acceptEffect = acceptEffect;
        this.crewDeleted = crewDeleted;
    }

    public boolean isAcceptEffect() {
        return acceptEffect;
    }

    public List<Integer> getCrewDeleted() {
        return crewDeleted;
    }
}
