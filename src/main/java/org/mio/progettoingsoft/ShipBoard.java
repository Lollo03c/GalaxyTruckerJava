package org.mio.progettoingsoft;

import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.GraveYard;
import org.mio.progettoingsoft.exceptions.FullGoodDepot;
import org.mio.progettoingsoft.exceptions.NotEnoughBatteries;

import java.util.*;
import java.util.stream.Stream;

public class ShipBoard {
    private final Optional<Component>[][] shipComponents;
    List<Component> componentList;
    Stream<Component> componentStream;

    private  Component[] bookedComponents;

    private final List<Cordinate> bannedCoordinates;

    private final int rows;
    private final int columns;

    private  int availableEnergy;
    private Map<GoodType, Integer> goods;

    private  int exposedConnectors;
    private  int maxEnergy;
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

        goods = new HashMap<>();
        for (GoodType type : GoodType.values()){
            goods.put(type, 0);
        }
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

        if (shipComponents[row][column].isPresent()){
            return false;
        }

        shipComponents[row][column] = Optional.of(component);
        availableEnergy += component.getEnergyQuantity();

        getComponentsStream();
        getComponentsList();

        return true;
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

    private void getComponentsList(){
        componentList =  getStreamOptComponents()
                .filter(optComp -> optComp.isPresent())
                .map(optComp -> optComp.get())
                .toList();
    }

    private void getComponentsStream(){
        componentStream =  getStreamOptComponents()
                .filter(optComp -> optComp.isPresent())
                .map(optComp -> optComp.get());
    }

    public int getQuantBatteries(){
        return availableEnergy;
    }

    public void removeEnergy() throws NotEnoughBatteries{
        boolean removed = false;

        for (int i = 0; !removed && i < componentList.size(); i++)
            removed = componentList.get(i).removeOneEnergy();

        if (!removed)
            throw  new NotEnoughBatteries();

        availableEnergy--;
    }

    public void removeComponent(int row, int column) {
    }

    private boolean validRow(int row){
        return row >= 0 && row < rows;
    }

    private boolean validColumn(int column){
        return column >= 0 && column < columns;
    }

    private Map<GoodType, Integer> getStoredGoods() {
        return goods;
    }

    public Integer getStoredQuantityGoods(GoodType type){
        return goods.getOrDefault(type, 0);
    }

    public void addGood(GoodType type) throws FullGoodDepot {
        boolean added = false;

        for (int i = 0; !added && i < componentList.size(); i++){
            added = componentList.get(i).addGood(type);
        }

        if (!added)
            throw new FullGoodDepot(type);

        goods.put(type, goods.get(type) + 1);
    }


}


