package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;

import java.util.*;

/**
 * Represents a "Housing" component on a spaceship, designed to accommodate guests.
 * Standard housings can hold up to two guests, primarily humans. Some rules apply
 * regarding the placement of alien guests (Purple or Brown).
 * It extends the base {@link Component} class.
 */
public class Housing extends Component {
    private final Set<GuestType> guestAllowed;
    private final List<GuestType> guests;
    private HousingColor color;

    /**
     * Constructs a new Housing component.
     * Initially, only {@link GuestType#HUMAN} guests are allowed, and the housing is empty.
     *
     * @param id The unique identifier for this component.
     * @param topConn The connector on the top side of the component.
     * @param bottomConn The connector on the bottom side of the component.
     * @param rightConn The connector on the right side of the component.
     * @param leftConn The connector on the left side of the component.
     */
    public Housing(int id, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(id, ComponentType.HOUSING, topConn, bottomConn, rightConn, leftConn);
        guestAllowed = new HashSet<>();
        guestAllowed.add(GuestType.HUMAN);
        guests = new ArrayList<>();
    }

    /**
     * Retrieves the list of guests currently residing in this housing.
     *
     * @return A {@link List} of {@link GuestType} representing the guests.
     */
    @Override
    public List<GuestType> getGuests(){
        return guests;
    }

    /**
     * Adds a guest of the specified type to the housing.
     * This method enforces various rules for guest placement:
     * <ul>
     * <li>Alien guests (BROWN or PURPLE) cannot be added to "initial housings" (pre-defined by HousingColor IDs).</li>
     * <li>The housing cannot be already full (max 2 guests).</li>
     * <li>The guest type must be explicitly allowed.</li>
     * <li>If the housing already contains an alien, no more guests (human or alien) can be added.</li>
     * <li>If an alien is being added, and the housing is not empty, it's considered full (only one alien per housing).</li>
     * </ul>
     *
     * @param type The {@link GuestType} of the guest to add.
     * @throws IncorrectShipBoardException if the guest cannot be added due to various rules (full, not allowed, alien conflict).
     */
    @Override
    public void addGuest(GuestType type) throws IncorrectShipBoardException {
        if (type.equals(GuestType.BROWN) || type.equals(GuestType.PURPLE)){
            List<Integer> initialHousing = new ArrayList<>();
            for (HousingColor housingColor : HousingColor.values()){
                initialHousing.add(housingColor.getIdByColor());
            }

            if (initialHousing.contains(getId()))
                throw new IncorrectShipBoardException("initial housing");
        }

        if (guests.size() >= 2)
            throw new IncorrectShipBoardException("housing already full");

        if (!guestAllowed.contains(type))
            throw new IncorrectShipBoardException("guest type not allowed");

        if (guests.isEmpty()){
            guests.add(type);
            return;
        }

        if (guests.contains(GuestType.PURPLE) || guests.contains(GuestType.BROWN) || type.equals(GuestType.BROWN) || type.equals(GuestType.PURPLE))
            throw new IncorrectShipBoardException("Housing already full");

        guests.add(GuestType.HUMAN);
    }

    /**
     * Removes the last added guest from the housing.
     *
     * @throws IncorrectShipBoardException if the housing is already empty.
     */
    @Override
    public void removeGuest() throws IncorrectShipBoardException{
        if (guests.isEmpty())
            throw new IncorrectShipBoardException("housing is empty");

        guests.removeLast();
    }

    /**
     * Adds a {@link GuestType} to the set of allowed guests for this housing.
     * This allows specific types of aliens to be housed in this component.
     *
     * @param type The {@link GuestType} to allow.
     */
    @Override
    public void addAllowedGuest(GuestType type){
        guestAllowed.add(type);
    }

    /**
     * Checks if a guest of the specified type can be added to this housing,
     * without actually adding it. This method applies the same logic as {@code addGuest}.
     *
     * @param type The {@link GuestType} to check.
     * @return {@code true} if the guest can be added, {@code false} otherwise.
     */
    @Override
    public boolean canAddGuest(GuestType type){
        if (type.equals(GuestType.BROWN) || type.equals(GuestType.PURPLE)){
            List<Integer> initialHousing = new ArrayList<>();
            for (HousingColor housingColor : HousingColor.values()){
                initialHousing.add(housingColor.getIdByColor());
            }

            if (initialHousing.contains(getId()))
                return false;
        }
        if (guests.size() >= 2)
            return false;

        if (!guestAllowed.contains(type))
            return false;

        if (guests.isEmpty()){
            return true;
        }

        if (guests.contains(GuestType.PURPLE) || guests.contains(GuestType.BROWN) || type.equals(GuestType.BROWN) || type.equals(GuestType.PURPLE))
            return false;

        return true;
    }
}

