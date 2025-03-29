package org.mio.progettoingsoft.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CannonResponse extends Response{
    @JsonProperty("energyUses")
    private int energyUsed;

    @JsonProperty("destroyed")
    private boolean destroyed;

    @JsonProperty("componentPosition")
    private int componentPosition;

    public CannonResponse(){

    }

    public CannonResponse(int energy, boolean destroyed, int componentPosition){
        energyUsed = energy;
        this.destroyed = destroyed;
        this.componentPosition = componentPosition;
    }

    public int getEnergyUsed(){
        return energyUsed;
    }

    public boolean isDestroyed(){
        return destroyed;
    }

    public int getComponentPosition(){
        return componentPosition;
    }
}
