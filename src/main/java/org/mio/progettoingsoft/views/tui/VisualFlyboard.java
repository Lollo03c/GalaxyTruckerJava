package org.mio.progettoingsoft.views.tui;

import org.mio.progettoingsoft.model.FlyBoard;
import org.mio.progettoingsoft.model.Player;
import org.mio.progettoingsoft.model.components.HousingColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class VisualFlyboard {
    protected List<CircuitCell> visualCircuit = new ArrayList<>();
    private List<Player> scoreBoard;
    public static final String RESET = "\u001B[0m";

    public VisualFlyboard(FlyBoard flyboard) {
        List<Optional<Player>> circ = flyboard.getCircuit();
        scoreBoard = flyboard.getScoreBoard();
        int index = 0;
        for (Optional<Player> p : circ) {
            if (p.isPresent()) {
                visualCircuit.add(new CircuitCell(p.get().getShipBoard().getHousingColor().colorToString(),posToChar(index)));
            }
            else{
                visualCircuit.add(new CircuitCell());
            }
            index++;
        }
    }

    public char posToChar(int index) {
        return 'c';
    }

    public void drawCircuit() {}

    public void drawScoreboard(){
        int index = 1;
        System.out.println();
        for(Player p : scoreBoard){
            HousingColor color = p.getColor();
            System.out.println(color.colorToString() + index + " : "+ p.getNickname() + RESET);
            index++;
        }
    }
}
