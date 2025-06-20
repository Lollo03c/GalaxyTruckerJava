package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.model.enums.CannonType;

import java.util.*;

public class CannonPenalty extends Penalty {
    protected final Direction direction;
    protected final CannonType cannonType;

    protected int number;
    private Cordinate cordinateHit;

    public CannonPenalty(Direction direction, CannonType type) {
        this.direction = direction;
        this.cannonType = type;
    }

    public static CannonPenalty stringToCannonPenalty(String type, String direction) {
        if (type.equals("LIGHT")) {
            return new LightCannon(Direction.stringToDirection(direction));
        } else {
            return new HeavyCannon(Direction.stringToDirection(direction));
        }
    }

    @Override
    public PenaltyType getType(){
        return null;
    }

    @Override
    public void apply(String json, Player player) throws Exception{

    }

    public Optional<Cordinate> findHit(ShipBoard shipBoard, int value){
        Optional<Cordinate> cordinate = Optional.empty();
        List<Cordinate> validCords = new ArrayList<>();

        switch (direction){
            case LEFT, RIGHT -> value -= shipBoard.getOffsetRow();
            case FRONT, BACK -> value -= shipBoard.getOffsetCol();
        }

        Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();
        while (cordinateIterator.hasNext()){
            Cordinate cord = cordinateIterator.next();

            switch (direction){
                case LEFT, RIGHT -> {
                    if (cord.getRow() == value)
                        validCords.add(cord);
                }

                case FRONT, BACK -> {
                    if (cord.getColumn() == value)
                        validCords.add(cord);
                }
            }
        }

        if (validCords.isEmpty())
            return Optional.empty();

        return switch (direction){
            case LEFT ->
                    validCords.stream().min(Comparator.comparingInt(c -> c.getColumn()));

            case RIGHT ->
                    validCords.stream().max(Comparator.comparingInt(c -> c.getColumn()));

            case FRONT ->
                    validCords.stream().min(Comparator.comparingInt(c -> c.getRow()));

            case BACK ->
                    validCords.stream().max(Comparator.comparingInt(c -> c.getRow()));
        };
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    public void setNumber(int number){
        this.number = number;
    }

    public CannonType getCannonType() {
        return cannonType;
    }

    public int getNumber() {
        return number;
    }

    public void setCordinateHit(Cordinate cordinate){
        this.cordinateHit = cordinate;
    }

    public Cordinate getCordinateHit() {
        return cordinateHit;
    }
}

