package org.mio.progettoingsoft.responses;

import org.mio.progettoingsoft.AdvCardType;
import org.mio.progettoingsoft.advCards.Smugglers;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.HousingColor;

import java.util.Map;

public class SmugglersResponse extends Response{
    private final Map<Integer, Map<GoodType, Integer>> depos;
    private final boolean acceptEffect;

    public SmugglersResponse(HousingColor color, boolean acceptEffect, Map<Integer, Map<GoodType, Integer>> depos){
        super(AdvCardType.SMUGGLERS, color);

        this.acceptEffect = acceptEffect;
        this.depos = depos;
    }

    public Map<Integer, Map<GoodType, Integer>> getDepos() {
        return depos;
    }

    public boolean isAcceptEffect() {
        return acceptEffect;
    }
}
