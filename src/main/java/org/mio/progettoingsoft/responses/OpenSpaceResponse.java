package org.mio.progettoingsoft.responses;

import org.mio.progettoingsoft.AdvCardType;
import org.mio.progettoingsoft.components.HousingColor;

public class OpenSpaceResponse extends Response{
    private final int totalEnginePower;

    public OpenSpaceResponse(HousingColor color, int totalEnginePower){
        super(AdvCardType.OPEN_SPACE, color);
        this.totalEnginePower = totalEnginePower;
    }

    public int getTotalEnginePower(){
        return totalEnginePower;
    }
}
