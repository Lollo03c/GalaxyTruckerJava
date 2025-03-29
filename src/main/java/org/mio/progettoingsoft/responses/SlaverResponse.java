package org.mio.progettoingsoft.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mio.progettoingsoft.Component;

import java.util.List;

public class SlaverResponse extends Response{

    @JsonProperty("strength")
    private float stength;

    @JsonProperty("housing")
    private List<Component> housing;

    public SlaverResponse(){

    }

    public float getStength(){
        return stength;
    }

    public List<Component> getHousing(){
        return housing;
    }
}
