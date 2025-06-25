package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;

import java.util.*;

public class Housing extends Component {

    private final Set<GuestType> guestAllowed;
    private final List<GuestType> guests;

    private HousingColor color;

    public Housing(int id, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(id, ComponentType.HOUSING, topConn, bottomConn, rightConn, leftConn);

        guestAllowed = new HashSet<>();
        guestAllowed.add(GuestType.HUMAN);

        guests = new ArrayList<>();
    }


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
    @Override
    public List<GuestType> getGuests(){
        return guests;
    }

    @Override
    public void removeGuest() throws IncorrectShipBoardException{
        if (guests.isEmpty())
            throw new IncorrectShipBoardException("housing is empty");

        guests.removeLast();
    }

    @Override
    public void addAllowedGuest(GuestType type){
        guestAllowed.add(type);
    }

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

