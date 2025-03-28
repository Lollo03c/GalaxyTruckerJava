package org.mio.progettoingsoft.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mio.progettoingsoft.AdvCardType;
import org.mio.progettoingsoft.advCards.AbandonedShip;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.HousingColor;

import java.util.List;
import java.util.Map;

/*
{
    "advCard" : "ABANDONED_STATION",
    "color" : (player's color),
    "acceptEffect" : true or false,
    "depos" : {
        1 : {"green" : 2, "blue" : 1, "red" : 1},
        4 : {"yellow" : 1, "green" : 2 }
    }


}
 */

public class AbandonedStatationResponse extends Response{
    @JsonProperty("acceptEffect")
    private boolean acceptEffect;

    @JsonProperty("depos")
    private Map<Integer, Map<GoodType, Integer>> depos;

    public AbandonedStatationResponse(){

    }

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
