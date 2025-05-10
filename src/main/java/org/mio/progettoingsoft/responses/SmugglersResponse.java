package org.mio.progettoingsoft.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mio.progettoingsoft.AdvCardType;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.HousingColor;

import java.util.Map;

public class SmugglersResponse extends Response{
    @JsonProperty("depos")
    private Map<Integer, Map<GoodType, Integer>> depos;

    @JsonProperty("result")
    private float stregth;

    public SmugglersResponse(){

    }
    public SmugglersResponse(HousingColor color, int streght, Map<Integer, Map<GoodType, Integer>> depos){
        super(AdvCardType.SMUGGLERS, color);

        this.stregth = streght;
        this.depos = depos;
    }

    public Map<Integer, Map<GoodType, Integer>> getDepos() {
        return depos;
    }

    public float getStregth() {
        return stregth;
    }
}
