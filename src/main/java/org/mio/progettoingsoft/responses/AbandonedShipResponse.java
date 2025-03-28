package org.mio.progettoingsoft.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mio.progettoingsoft.AdvCardType;
import org.mio.progettoingsoft.HourGlass;
import org.mio.progettoingsoft.advCards.AbandonedShip;
import org.mio.progettoingsoft.components.Housing;
import org.mio.progettoingsoft.components.HousingColor;

import java.util.List;

/*
{
    "advCard" : "ABANDONED_SHIP"
    "color" : " "
    "acceptEffect" : "true" o "false"
    "crewDeleted" [1, 2, ...]
}
 */

public class AbandonedShipResponse extends Response{
    @JsonProperty("acceptEffect")
    private boolean acceptEffect;

    @JsonProperty("crewDeleted")
    private List<Integer> crewDeleted;

    public AbandonedShipResponse(){
    }

    public boolean isAcceptEffect() {
        return acceptEffect;
    }

    public List<Integer> getCrewDeleted() {
        return crewDeleted;
    }
}
