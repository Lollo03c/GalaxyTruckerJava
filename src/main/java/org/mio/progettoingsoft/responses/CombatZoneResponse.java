package org.mio.progettoingsoft.responses;

import org.mio.progettoingsoft.AdvCardType;
import org.mio.progettoingsoft.advCards.CombatZone;
import org.mio.progettoingsoft.advCards.Criterion;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.BadParameterException;

import java.util.List;

public class CombatZoneResponse extends Response{
    private final Criterion criterion;
    private final List<Integer> listPosizione;


    public CombatZoneResponse(HousingColor color, Criterion criterion, List<Integer> list) throws BadParameterException {
        super(AdvCardType.COMBAT_ZONE, color);
        this.criterion = criterion;

        if (!(criterion.equals(Criterion.ENGINE_POWER) || criterion.equals(Criterion.FIRE_POWER))){
            throw new BadParameterException("");
        }
        this.listPosizione = list;

    }

    public Criterion getCriterion(){
        return criterion;
    }

    public List<Integer> getPositions(){
        return listPosizione;
    }
}
