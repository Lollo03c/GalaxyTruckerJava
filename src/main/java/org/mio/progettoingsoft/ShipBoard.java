package org.mio.progettoingsoft;

import org.mio.progettoingsoft.components.GraveYard;

import java.util.*;
import java.util.stream.Stream;

public class ShipBoard {
    private final Optional<Component>[][] shipComponents;
    private  Component[] bookedComponents;

    private final List<Cordinate> bannedCoordinates;

    private final int rows;
    private final int columns;

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
        rows = 5;
        columns = 7;
        shipComponents = new Optional[rows][columns];
        bannedCoordinates = new ArrayList<>(6);

        for (int i = 0; i < rows; i++)
            for(int j = 0; j <columns; j++)
                shipComponents[i][j] = Optional.empty();

        bannedCoordinates.add(new Cordinate(0, 0));
        bannedCoordinates.add(new Cordinate(0, 1));
        bannedCoordinates.add(new Cordinate(0, 3));
        bannedCoordinates.add(new Cordinate(1, 0));
        bannedCoordinates.add(new Cordinate(1, 6));
        bannedCoordinates.add(new Cordinate(4, 3));
    }

    public int getRows(){
        return rows;
    }

    public int getColumns(){
        return columns;
    }

    public Optional<Component> getComponent(int row, int col){
        return shipComponents[row][col];
    }

    public boolean isEmptyComponent(int row, int column){
        if (!validRow(row) || validColumn(column))
            return false;

        return shipComponents[row][column].isEmpty();
    }

    public boolean addComponentToPosition(Component component, int row, int column){
        if (bannedCoordinates.contains(new Cordinate(row, column)))
            return false;

        if (!validRow(row) || !validColumn(column))
            return false;

        if (shipComponents[row][column].isEmpty()){
            shipComponents[row][column] = Optional.of(component);
            return true;
        }

        return false;
    }

    public boolean removeComponentFromPosition(int row, int column){
        if(!validRow(row) || !validColumn(column) || shipComponents[row][column].isEmpty())
            return false;

        shipComponents[row][column] = Optional.empty();
        return true;
    }

    private Stream<Optional<Component>> getStreamOptComponents(){
        return Arrays.stream(shipComponents).flatMap(Arrays::stream);
    }

    public List<Component> getComponentsList(){
        return getStreamOptComponents()
                .filter(optComp -> optComp.isPresent())
                .map(optComp -> optComp.get())
                .toList();
    }

    public Stream<Component> getComponentsStream(){
        return  getStreamOptComponents()
                .filter(optComp -> optComp.isPresent())
                .map(optComp -> optComp.get());
    }

    public int getQuantBatteries(){
        return getComponentsStream()
                .mapToInt(comp -> comp.getEnergyQuantity())
                .sum();
    }

    public void removeComponent(int row, int column) {
    }

    private boolean validRow(int row){
        return row >= 0 && row < rows;
    }

    private boolean validColumn(int column){
        return column >= 0 && column < columns;
    }


}


