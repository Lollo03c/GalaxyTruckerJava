package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.model.enums.MeteorType;

import java.util.*;

public class Meteor {
    protected final Direction direction;
    protected final MeteorType type;
    private int number;
    private Cordinate cordinateHit;
    private final Set<String> nicksAnswered = new HashSet<>();

    public Meteor(Direction direction, MeteorType type) {
        this.direction = direction;
        this.type = type;
    }

    public static Meteor stringToMeteor(String type, String direction) {
        if(type.equals("SMALL")){
            return new SmallMeteor(Direction.stringToDirection(direction));
        }else{
            return new BigMeteor(Direction.stringToDirection(direction));
        }
    }

    public Direction getDirection(){
        return direction;
    }

    public MeteorType getType() {
        return type;
    }

    public void hit(Player player, int value){

    }

    public Optional<Cordinate> findHit(ShipBoard shipBoard, int value){
        Optional<Cordinate> cordinate = Optional.empty();
        List<Cordinate> validCords = new ArrayList<>();

        switch (direction){
            case LEFT, RIGHT -> value -= shipBoard.getOffsetCol();
            case FRONT, BACK -> value -= shipBoard.getOffsetRow();
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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Cordinate getCordinateHit() {
        return cordinateHit;
    }

    public void setCordinateHit(Cordinate cordinateHit) {
        this.cordinateHit = cordinateHit;
    }

    public void addPlayerResponses(String nick){
        nicksAnswered.add(nick);
    }

    public Set<String> getPlayerResponses() {
        return nicksAnswered;
    }
}
