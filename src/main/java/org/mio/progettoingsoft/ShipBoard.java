package org.mio.progettoingsoft;

import org.mio.progettoingsoft.components.GraveYard;

import java.util.Set;

public class ShipBoard {
    private final Component[][] shipComponents;
    private  Component[] bookedComponents;
    private  int exposedConnectors;
    private  int maxEnergy;
    private  int availableEnergy;
    private  int maxSpecialGoods;
    private  int numSpecialGoods;
    private  int maxNormalGoods;
    private  int numNormalGoods;
    private  int numAliens;
    private  int numAstronauts;
    private  boolean completedBuild;


    public ShipBoard(){
        shipComponents = new Component[5][7];
        shipComponents[0][0] = new GraveYard();
        shipComponents[0][1] = new GraveYard();
        shipComponents[0][3] = new GraveYard();

        shipComponents[1][0] = new GraveYard();
        shipComponents[1][6] = new GraveYard();

        shipComponents[4][3] = new GraveYard();

    }

    public boolean addComponentToPosition(Component component, int row, int column){
        if (shipComponents[row][column] == null){
            shipComponents[row][column] = component;
            return true;
        }
        return false;
    }

    public void removeComponent(int row, int column) {
    }
}


