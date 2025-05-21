package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.components.Drill;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BigMeteor extends Meteor {

    public BigMeteor(Direction direction) {
        super(direction);
    }

    @Override
    public void hit(Player player, int value) {
        Optional<Component> hitComponent = findHit(player, value);

        if (hitComponent.isEmpty())
            return;

        Component component = hitComponent.get();
        ShipBoard board = player.getShipBoard();

        List<Component> cannons = null; //board.getCom()
//                .filter(comp -> comp.getDirection() != null)
//                .filter(comp -> comp.getDirection().equals(direction))
//                .toList();

        List<Component> singleFounded = new ArrayList<>();
        List<Component> doubleFounded = new ArrayList<>();

        List<Component> singles = cannons.stream()
                .filter(comp -> comp.getType().equals(ComponentType.DRILL))
                .toList();

        List<Component> doubles = cannons.stream()
                .filter(comp -> comp.getType().equals(ComponentType.DOUBLE_DRILL))
                .toList();

        if (direction.equals(Direction.FRONT) || direction.equals(Direction.BACK)){
            singles.stream()
                    .filter(comp -> comp.getColumn() == value)
                    .forEach(comp -> singleFounded.add(comp));

            doubles.stream()
                    .filter(comp -> comp.getColumn() == value)
                    .forEach(comp -> doubleFounded.add(comp));

            if (direction.equals(Direction.BACK)){
                singles.stream()
                        .filter(comp -> comp.getColumn() == value + 1 || comp.getColumn() == value - 1)
                        .forEach(comp -> singleFounded.add(comp));

                doubles.stream()
                        .filter(comp -> comp.getColumn() == value + 1 || comp.getColumn() == value - 1)
                        .forEach(comp -> doubleFounded.add(comp));
            }
        }
        else{
            singles.stream()
                    .filter(comp -> comp.getRow() == value + 1 || comp.getRow() == value - 1 || comp.getRow() == value)
                    .forEach(comp -> singleFounded.add(comp));

            doubles.stream()
                    .filter(comp -> comp.getRow() == value + 1 || comp.getRow() == value - 1 || comp.getRow() == value)
                    .forEach(comp -> doubleFounded.add(comp));
        }

        if (singleFounded.isEmpty()){
            if (doubleFounded.isEmpty()){
//                board.removeComponent(component);

            }
            else{
                boolean activated = true;// player.getView().askOneDoubleDrill();

                if (activated){
//                    board.removeEnergy();
                }
                else{
//                    board.removeComponent(component);
                }
            }
        }

    }

    @Override
    public String toString(){
        return "Big meteor";
    }
}
