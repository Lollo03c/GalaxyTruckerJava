package org.mio.progettoingsoft.responses;

import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.components.Housing;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.BadCardException;
import org.mio.progettoingsoft.exceptions.BadParameterException;

import java.io.Serializable;

public abstract class Response implements Serializable {
    private final AdvCardType advCard;
    private final HousingColor colorPlayer;

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