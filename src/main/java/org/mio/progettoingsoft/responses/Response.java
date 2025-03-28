package org.mio.progettoingsoft.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.components.Housing;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.BadCardException;
import org.mio.progettoingsoft.exceptions.BadParameterException;

import java.io.Serializable;

public abstract class Response implements Serializable {
    @JsonProperty("advCard")
    private AdvCardType advCard;

    @JsonProperty("color")
    private HousingColor colorPlayer;

    public Response(){

    }

    protected Response(AdvCardType advCard, HousingColor colorPlayer){
        this.advCard = advCard;
        this.colorPlayer = colorPlayer;
    }

    public AdvCardType getAdvCard() {
        return advCard;
    }

    public HousingColor getColorPlayer() {
        return colorPlayer;
    }

}