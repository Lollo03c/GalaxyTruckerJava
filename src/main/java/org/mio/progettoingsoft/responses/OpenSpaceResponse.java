package org.mio.progettoingsoft.responses;

import org.mio.progettoingsoft.AdvCardType;
import org.mio.progettoingsoft.advCards.OpenSpace;
import org.mio.progettoingsoft.components.HousingColor;

public class OpenSpaceResponse extends Response{
    private final int totalEnginPower;

    public OpenSpaceResponse(HousingColor color, int totalEnginePower){
        super(AdvCardType.OPEN_SPACE, color);
        this.totalEnginPower = totalEnginePower;
    }

    public int getTotalEnginPower(){
        return totalEnginPower;
    }
}
