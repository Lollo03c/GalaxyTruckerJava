package org.mio.progettoingsoft.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
{
    "advCard" : "PIRATE",
    "color" : "(player's color)"
    "strength" : (total fire power)
    "energyUsed" :
}
 */

public class PirateResponse extends Response{

    @JsonProperty("strength")
    private float strength;

    @JsonProperty("component")
    private int componentPosition;

    @JsonProperty("energyUsed")
    private int energyUsed;



    public PirateResponse(){

    }

    public float getStrength(){
        return strength;
    }

    public int getEnergyUsed(){
        return energyUsed;
    }
}
