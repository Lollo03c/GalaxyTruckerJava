package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;

import java.util.HashMap;
import java.util.Map;

public class Depot extends Component {

    private final Boolean isBig;
    private final Boolean isHazard;
    private int storedQuantity;
    private final int maxQuantity;
    private final Map<GoodType, Integer> storedGoods;


    public Depot(int id, boolean isBig, boolean isHazard, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn) {
        super(id, ComponentType.DEPOT, topConn, bottomConn, rightConn, leftConn);
        this.isBig = isBig;
        this.isHazard = isHazard;

        storedQuantity = 0;
        storedGoods = new HashMap<>();
        storedGoods.put(GoodType.BLUE, 0);
        storedGoods.put(GoodType.GREEN, 0);
        storedGoods.put(GoodType.YELLOW, 0);

        if (isHazard)
            storedGoods.put(GoodType.RED, 0);

        if (isBig && !isHazard)
            maxQuantity = 3;
        else if (isHazard && !isBig)
            maxQuantity = 1;
        else
            maxQuantity = 2;
    }

    public int getStoredQuantity() {
        return storedQuantity;
    }

    public int getStoredQuantityType(GoodType type){
        return storedGoods.getOrDefault(type, 0);
    }

    public Boolean addGood(GoodType type){
        if (storedGoods.containsKey(type) && storedQuantity < maxQuantity){
            storedQuantity++;

            storedGoods.put(type, storedGoods.get(type) + 1);
            return true;
        }

        return false;
    }

    public Boolean replaceGood(GoodType toDecrement, GoodType toIncrement){
        if (storedGoods.containsKey(toDecrement) && storedGoods.containsKey(toIncrement) && storedGoods.get(toDecrement) > 0){
            storedGoods.put(toDecrement, storedGoods.get(toDecrement) - 1);
            storedGoods.put(toIncrement, storedGoods.get(toIncrement) + 1);
        }

        return false;
    }

    public Boolean removeGood(GoodType type){
        if (storedGoods.getOrDefault(type, 0) > 0){
            storedGoods.put(type, storedGoods.get(type) - 1);

            return true;
        }
        return false;


    }
}
