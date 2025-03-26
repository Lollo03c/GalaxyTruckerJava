package org.mio.progettoingsoft.responses;

import org.mio.progettoingsoft.AdvCardType;
import org.mio.progettoingsoft.advCards.AbandonedShip;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.HousingColor;

import java.util.List;
import java.util.Map;

public class AbandonedStatationResponse extends Response{
    private final boolean acceptEffect;
    private final Map<Integer, Map<GoodType, Integer>> depos;

    public Map<Integer, Map<GoodType, Integer>> getDepos() {
        return depos;
    }

    public boolean isAcceptEffect() {
        return acceptEffect;
    }

    public AbandonedStatationResponse(HousingColor color, boolean acceptedEffect, Map<Integer, Map<GoodType, Integer>> depos){
        super(AdvCardType.ABANDONED_STATION, color);

        this.acceptEffect = acceptedEffect;
        this.depos = depos;
    }
}
