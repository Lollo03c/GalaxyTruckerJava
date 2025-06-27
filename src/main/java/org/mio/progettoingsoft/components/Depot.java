package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a "Depot" component on a spaceship, used for storing goods.
 * Depots can vary in size (big or not) and hazard status, which affects their
 * maximum storage capacity and ability to store hazardous (red) goods.
 * It extends the base {@link Component} class.
 */
public class Depot extends Component {
    private final Boolean isBig;

    private final Boolean isHazard;
    private final int maxQuantity;
    private List<GoodType> storedGoods;


    /**
     * Constructs a new Depot component.
     * The maximum quantity of goods the depot can store is determined by its
     * {@code isBig} and {@code isHazard} properties:
     * - Big and Not Hazard: 3 goods
     * - Hazard and Not Big: 1 good
     * - Otherwise (Big and Hazard, or Not Big and Not Hazard): 2 goods
     *
     * @param id The unique identifier for this component.
     * @param isBig A boolean indicating if this is a large depot.
     * @param isHazard A boolean indicating if this depot can store hazardous goods.
     * @param topConn The connector on the top side of the component.
     * @param bottomConn The connector on the bottom side of the component.
     * @param rightConn The connector on the right side of the component.
     * @param leftConn The connector on the left side of the component.
     */
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

    /**
     * Indicates if this depot is a hazard depot.
     *
     * @return {@code true} if the depot can store hazardous goods, {@code false} otherwise.
     */
    @Override
    public boolean getHazard(){
        return isHazard;
    }

    /**
     * Indicates if this depot is a big depot.
     *
     * @return {@code true} if the depot is large, {@code false} otherwise.
     */
    @Override
    public boolean getBig(){
        return isBig;
    }

    /**
     * Retrieves the list of goods currently stored in the depot.
     *
     * @return A {@link List} of {@link GoodType} representing the stored goods.
     */
    @Override
    public List<GoodType> getStoredGoods(){
        return storedGoods;
    }

    /**
     * Adds a {@link GoodType} to the depot.
     *
     * @param type The type of good to add.
     * @throws IncorrectShipBoardException if the depot is full or if a red (hazardous) good
     * is attempted to be stored in a non-hazard depot.
     */
    @Override
    public void addGood(GoodType type) throws IncorrectShipBoardException {
        if (storedGoods.size() >= maxQuantity)
            throw new IncorrectShipBoardException("Depot already full");

        if (type.equals(GoodType.RED) && !isHazard)
            throw new IncorrectShipBoardException("Depot is not hazard");
        storedGoods.add(type);
    }

    /**
     * Removes a {@link GoodType} from the depot.
     *
     * @param type The type of good to remove.
     * @throws IncorrectShipBoardException if the specified good type is not found in the depot.
     */
    @Override
    public void removeGood(GoodType type) throws IncorrectShipBoardException{
        if (!storedGoods.contains(type))
            throw new IncorrectShipBoardException("GoodType not found in depot");

        storedGoods.remove(type);
    }

    /**
     * Checks if the depot can contain a specific type of good.
     * This considers both capacity and whether it's a hazard depot for red goods.
     *
     * @param type The type of good to check.
     * @return {@code true} if the good can be added, {@code false} otherwise.
     */
    @Override
    public boolean canContainsGood(GoodType type) throws IncorrectShipBoardException{
        if (type.equals(GoodType.RED) && !isHazard)
            return false;

        return storedGoods.size() < maxQuantity;
    }
}