package org.mio.progettoingsoft.views.tui;

import org.mio.progettoingsoft.model.Player;
import org.mio.progettoingsoft.model.components.HousingColor;
import org.mio.progettoingsoft.model.FlyBoardNormal;
import org.mio.progettoingsoft.model.enums.GameMode;


import java.util.*;
import java.util.List;

public class VisualFlyboardNormal extends VisualFlyboard {
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public VisualFlyboardNormal(FlyBoardNormal flyboard) {
        super(flyboard);
        visualCircuit.set(0,visualCircuit.get(0).modifyFlag('4'));
        visualCircuit.set(1,visualCircuit.get(1).modifyFlag('3'));
        visualCircuit.set(3,visualCircuit.get(3).modifyFlag('2'));
        visualCircuit.set(6,visualCircuit.get(6).modifyFlag('1'));
    }
    @Override
    public char posToChar(int index) {
        if (index >= 0 && index <= 8) {
            return 't';
        } else if (index >= 9 && index <= 11) {
            return 'r';
        } else if (index >= 12 && index <= 20) {
            return 'b';
        } else if (index >= 21 && index <= 23) {
            return 'l';
        } else {
            throw new IllegalArgumentException("Invalid index :  " + index);
        }
    }
    // just for debugging purposes
    public void setVisualCircuit(List<CircuitCell> visualCircuit) {
        this.visualCircuit = visualCircuit;
    }

    @Override
    public void drawCircuit() {
        if (visualCircuit.size() != 24) {
            throw new IllegalArgumentException("The circuit must have size 24");
        }

        List<CircuitCell> top = visualCircuit.subList(0, 10);
        List<CircuitCell> right = visualCircuit.subList(10, 12);
        List<CircuitCell> bottom = visualCircuit.subList(12, 22).reversed();
        List<CircuitCell> left = visualCircuit.subList(22, 24).reversed();

        for (int row = 0; row < 3; row++) {
            for (CircuitCell cell : top) {
                System.out.print(cell.getRow(row));
            }
            System.out.println();
        }

        for (int i = 0; i < 2; i++) {
            for (int row = 0; row < 3; row++) {
                System.out.print(left.get(i).getRow(row));
                for (int s = 0; s < 5 * 8; s++) System.out.print(" ");
                System.out.print(right.get(i).getRow(row));
                System.out.println();
            }
        }

        for (int row = 0; row < 3; row++) {
            for (CircuitCell cell : bottom) {
                System.out.print(cell.getRow(row));
            }
            System.out.println();
        }
    }

    public static void main(String[] args){
        List<CircuitCell> circ = new ArrayList<>();
        circ.add(new CircuitCell());
        circ.add(new CircuitCell());
        circ.add(new CircuitCell());
        circ.add(new CircuitCell());
        circ.add(new CircuitCell());
        circ.add(new CircuitCell(CircuitCell.RED,'t'));
        circ.add(new CircuitCell());
        circ.add(new CircuitCell());
        circ.add(new CircuitCell());
        circ.add(new CircuitCell(CircuitCell.GREEN,'t'));
        circ.add(new CircuitCell());
        circ.add(new CircuitCell());
        circ.add(new CircuitCell());
        circ.add(new CircuitCell());
        circ.add(new CircuitCell(YELLOW,'b'));
        circ.add(new CircuitCell(YELLOW,'b'));
        circ.add(new CircuitCell(YELLOW,'b'));
        circ.add(new CircuitCell());
        circ.add(new CircuitCell(CircuitCell.YELLOW,'b'));
        circ.add(new CircuitCell());
        circ.add(new CircuitCell());
        circ.add(new CircuitCell());
        circ.add(new CircuitCell(CircuitCell.BLUE,'l'));
        circ.add(new CircuitCell(CircuitCell.RED,'l'));
        Set<String> mySet = new HashSet<>();
        mySet.add("A");
        mySet.add("B");
        mySet.add("C");
        FlyBoardNormal fly = new FlyBoardNormal(mySet);
        VisualFlyboardNormal visual = new VisualFlyboardNormal(fly);
        visual.setVisualCircuit(circ);
        //visual.drawCircuit();
        fly.circuit.set(4,Optional.of(new Player("lorenzo", HousingColor.BLUE,GameMode.NORMAL,fly)));
        fly.circuit.set(11,Optional.of(new Player("antonio", HousingColor.GREEN,GameMode.NORMAL,fly)));
        fly.circuit.set(23,Optional.of(new Player("andrea", HousingColor.RED,GameMode.NORMAL,fly)));
        fly.circuit.set(18, Optional.of(new Player("stefano", HousingColor.YELLOW, GameMode.NORMAL, fly)));


        VisualFlyboardNormal visual2 = new VisualFlyboardNormal(fly);
        visual2.drawCircuit();
    }
}
