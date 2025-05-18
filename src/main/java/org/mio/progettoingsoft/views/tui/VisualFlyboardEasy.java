package org.mio.progettoingsoft.views.tui;

import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.model.FlyBoardEasy;
import org.mio.progettoingsoft.model.FlyBoardNormal;
import org.mio.progettoingsoft.model.enums.GameMode;

import java.util.*;

public class VisualFlyboardEasy extends VisualFlyboard {
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public VisualFlyboardEasy(FlyBoardEasy fly) {
        super(fly);
        visualCircuit.set(0,visualCircuit.get(0).modifyFlag('4'));
        visualCircuit.set(1,visualCircuit.get(1).modifyFlag('3'));
        visualCircuit.set(2,visualCircuit.get(2).modifyFlag('2'));
        visualCircuit.set(4,visualCircuit.get(4).modifyFlag('1'));
    }

    @Override
    public char posToChar(int index) {
        if (index >= 0 && index <= 5) {
            return 't';
        } else if (index >= 6 && index <= 8) {
            return 'r';
        }
        else if (index >= 9 && index <= 14) {
            return 'b';
        }
        else if (index >= 15 && index <= 17) {
            return 'l';
        }
        return 'c';
    }
    @Override
    public void drawCircuit() {
        if (visualCircuit.size() != 18) {
            throw new IllegalArgumentException("The circuit must have size 18");
        }

        List<CircuitCell> top = visualCircuit.subList(0, 7);
        List<CircuitCell> right = visualCircuit.subList(7, 9);
        List<CircuitCell> bottom = visualCircuit.subList(9, 16).reversed();
        List<CircuitCell> left = visualCircuit.subList(16, 18).reversed();

        for (int row = 0; row < 3; row++) {
            for (CircuitCell cell : top) {
                System.out.print(cell.getRow(row));
            }
            System.out.println();
        }

        for (int i = 0; i < 2; i++) {
            for (int row = 0; row < 3; row++) {
                System.out.print(left.get(i).getRow(row));
                for (int s = 0; s < 5 * 5; s++) System.out.print(" ");
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
    // just for debugging purposes
    public void setVisualCircuit(List<CircuitCell> visualCircuit) {
        this.visualCircuit = visualCircuit;
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
        Set<String> mySet = new HashSet<>();
        mySet.add("A");
        mySet.add("B");
        mySet.add("C");
        FlyBoardEasy fly = new FlyBoardEasy(mySet);
        VisualFlyboardEasy visual = new VisualFlyboardEasy(fly);
        visual.setVisualCircuit(circ);
        //visual.drawCircuit();
        fly.circuit.set(3, Optional.of(new Player("lorenzo", HousingColor.BLUE, GameMode.NORMAL,fly)));
        fly.circuit.set(8,Optional.of(new Player("antonio", HousingColor.GREEN,GameMode.NORMAL,fly)));
        fly.circuit.set(11,Optional.of(new Player("andrea", HousingColor.RED,GameMode.NORMAL,fly)));
        fly.circuit.set(16, Optional.of(new Player("stefano", HousingColor.YELLOW, GameMode.NORMAL, fly)));

        List<Player> scoreboard = new ArrayList<>(4);

        scoreboard.add(0,new Player("lorenzo", HousingColor.BLUE, GameMode.NORMAL,fly));
        scoreboard.add(0,new Player("antonio", HousingColor.GREEN,GameMode.NORMAL,fly));
        scoreboard.add(0,new Player("andrea", HousingColor.RED,GameMode.NORMAL,fly));
        scoreboard.add(0,new Player("stefano", HousingColor.YELLOW, GameMode.NORMAL, fly));
        fly.setScoreboard(scoreboard);
        VisualFlyboardEasy visual2 = new VisualFlyboardEasy(fly);
        visual2.drawCircuit();
        visual2.drawScoreboard();
    }
}
