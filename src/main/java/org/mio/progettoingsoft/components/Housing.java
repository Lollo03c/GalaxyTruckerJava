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
    public HousingColor getHousingColorById(int id){
        return switch (id) {
            case 33 -> HousingColor.BLUE;
            case 34 -> HousingColor.GREEN;
            case 52 -> HousingColor.RED;
            case 61 -> HousingColor.YELLOW;
            default -> HousingColor.BLUE;
        };
    }
    @Override
    public boolean canAddGuest(GuestType type){
        if (!guestAllowed.contains(type))
            return false;

        if (type.equals(GuestType.HUMAN))
            return guests.size() <= 1;

        return guests.isEmpty();
    }

    @Override
    public List<GuestType> getGuests(){
        return guests;
    }

    @Override
    public void removeGuest(GuestType type) throws IncorrectShipBoardException{
        if (!guests.contains(type))
            throw new IncorrectShipBoardException("guest not hosted in the housing");

        guests.remove(type);
    }

    @Override
    public void addAllowedGuest(GuestType type){
        guestAllowed.add(type);
    }

    @Override
    public HousingColor getHousingColorById(int id){
        return switch (id) {
            case 33 -> HousingColor.BLUE;
            case 34 -> HousingColor.GREEN;
            case 52 -> HousingColor.RED;
            case 61 -> HousingColor.YELLOW;
            default -> HousingColor.BLUE;
        };
    }
}

