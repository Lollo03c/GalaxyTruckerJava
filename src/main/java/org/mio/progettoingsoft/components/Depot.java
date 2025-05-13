package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Depot extends Component {

    private final Boolean isBig;
    public Boolean getBig() {
        return isBig;
    }

    @Override
    public Boolean getHazard() {
        return isHazard;
    }

    private final Boolean isHazard;
    private final int maxQuantity;
    private List<GoodType> storedGoods;


    public Depot(int id, boolean isBig, boolean isHazard, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn) {
        super(id, ComponentType.DEPOT, topConn, bottomConn, rightConn, leftConn);
        this.isBig = isBig;
        this.isHazard = isHazard;

        storedGoods = new ArrayList<>();

        if (isBig && !isHazard)
            maxQuantity = 3;
        else if (isHazard && !isBig)
            maxQuantity = 1;
        else
            maxQuantity = 2;
    }


    @Override
    public void addGood(GoodType type) throws IncorrectShipBoardException {
        if (storedGoods.size() >= maxQuantity)
            throw new IncorrectShipBoardException("Depot already full");

        if (type.equals(GoodType.RED) && !isHazard)
            throw new IncorrectShipBoardException("Depot is not hazard");

        storedGoods.add(type);
    }

    @Override
    public void removeGood(GoodType type) throws IncorrectShipBoardException{
        if (!storedGoods.contains(type))
            throw new IncorrectShipBoardException("GoodType not found in depot");

        storedGoods.remove(type);
    }

    @Override
    public List<GoodType> getStoredGoods(){
        return storedGoods;
    }

    @Override
    public boolean canContainsGood(GoodType type) throws IncorrectShipBoardException{
        if (type.equals(GoodType.RED) && !isHazard)
            return false;

        return storedGoods.size() < maxQuantity;
    }


}
