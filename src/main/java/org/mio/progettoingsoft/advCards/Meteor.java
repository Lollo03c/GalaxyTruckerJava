package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.model.enums.MeteorType;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.model.events.RemoveComponentEvent;
import org.mio.progettoingsoft.model.interfaces.GameServer;

import java.util.*;

public class Meteor {
    protected final Direction direction;
    protected final MeteorType type;
    protected int number;
    private Cordinate cordinateHit;
    private final Set<String> nicksAnswered = new HashSet<>();

    protected final Set<String> nickHit = new HashSet<>();

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

    public void hit(GameServer game, Player player, int value){

    }

    public void destroy(Player player, GameServer game){
        Cordinate cord = findHit(player.getShipBoard(), number).get();

        ShipBoard shipBoard = player.getShipBoard();
        shipBoard.removeComponent(cord);

        Event event = new RemoveComponentEvent(player.getNickname(), cord);
        game.addEvent(event);
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
            if (shipBoard.getOptComponentByCord(cord).isEmpty())
                continue;

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

    public Set<String> getNickHit() {
        return nickHit;
    }
}
