package org.mio.progettoingsoft;

import org.jetbrains.annotations.NotNull;
import org.mio.progettoingsoft.components.DoubleEngine;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.GraveYard;
import org.mio.progettoingsoft.exceptions.*;

import java.util.*;
import java.util.stream.Stream;

public class ShipBoard {
    private final Optional<Component>[][] shipComponents;
    List<Component> componentList;

    private  Component[] bookedComponents;

    private final List<Cordinate> bannedCoordinates;

    private final int rows;
    private final int columns;

    private  int availableEnergy;
    private float baseFirePower;
    private int baseEnginePower;

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

        baseFirePower = 0f;
        baseEnginePower = 0;
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

    public Map<Direction, Component> getAdjacent(int row, int column){
        Map<Direction, Component> adjacents = new HashMap();

        if (validRow(row-1) && shipComponents[row-1][column].isPresent() ){
            adjacents.put(Direction.FRONT, shipComponents[row-1][column].get());
        }
        if (validRow(row+1) && shipComponents[row+1][column].isPresent() ){
            adjacents.put(Direction.BACK, shipComponents[row+1][column].get());
        }
        if (validColumn(column + 1) && shipComponents[row][column+1].isPresent() ){
            adjacents.put(Direction.RIGHT, shipComponents[row][column+1].get());
        }
        if (validColumn(column - 1) && shipComponents[row][column-1].isPresent() ){
            adjacents.put(Direction.LEFT, shipComponents[row][column-1].get());
        }

        return adjacents;
    }

    public void addComponentToPosition(Component component, int row, int column) throws IncorrectPlacement{
        if (bannedCoordinates.contains(new Cordinate(row, column)))
            throw new IncorrectPlacement(row, column, component);

        if (!validRow(row) || !validColumn(column))
            throw new IncorrectPlacement(row, column, component);

        if (shipComponents[row][column].isPresent()){
            throw new IncorrectPlacement(row, column, component);
        }

        Map<Direction, Component> adjacent = getAdjacent(row, column);
        boolean added = false;
        for (Direction dir : adjacent.keySet()){
            added = added || component.isCompatible(adjacent.get(dir), dir);
        }

        if (!added)
            throw new IncorrectPlacement(row, column, component);

        shipComponents[row][column] = Optional.of(component);
        availableEnergy += component.getEnergyQuantity();

        for (Component comp : getAdjacent(row, column).values()) {
            component.addAlienType(comp.getColorAlien());
            comp.addAlienType(component.getColorAlien());
        }

        baseFirePower += component.getFirePower();
        baseEnginePower += component.getEnginePower();

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

    private Stream<Component> getComponentsStream(){
        return getStreamOptComponents()
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

    public void removeGood(GoodType type) throws NotEnoughGoods{
        boolean removed = false;

        for (int i = 0; !removed && i < componentList.size(); i++){
            removed = componentList.get(i).removeGood(type);
        }

        if(!removed)
            throw new NotEnoughGoods(type);

        goods.put(type, goods.get(type) - 1);
    }

    public void addHumanGuest() throws NotEnoughHousing{
        boolean added = false;

        for (int i = 0; !added && i < componentList.size(); i++)
            added = componentList.get(i).addHumanMember();


        if (!added)
            throw new NotEnoughHousing();
    }



    public List<Component> getDoubleEngine(){
        return getComponentsStream()
                .filter(comp -> comp.getType().equals(ComponentType.DOUBLE_ENGINE))
                .toList();
    }

    public List<Component> getDoubleDrill(Direction dir){
        return getComponentsStream()
                .filter(comp -> comp.getType().equals(ComponentType.DOUBLE_DRILL))
                .filter(comp -> comp.getDirection() != null)
                .filter(comp -> comp.getDirection().equals(dir))
                .toList();
    }

    private List<Component> getIncorrectEngines() {
        List<Component> incorrect = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++)
                if (shipComponents[i][j].isPresent()){
                    Component comp = shipComponents[i][j].get();
                    if (comp.getType().equals(ComponentType.ENGINE) || comp.getType().equals(ComponentType.DOUBLE_ENGINE)){
                        if (! comp.getDirection().equals(Direction.BACK)){
                            incorrect.add(comp);
                        }
                        else if (validRow(i+1) && shipComponents[i + 1][j].isPresent()){
                            incorrect.add(shipComponents[i][j].get());
                        }


                    }

                }
        }
        return incorrect;
    }

    private List<Component> getIncorrectDrill() {
        List<Component> incorrect = new ArrayList<>();

        for (int i = 0; i < rows; i++){
            for (int j = 0; j < columns; j++){
                if (shipComponents[i][j].isPresent()) {
                    Component comp = shipComponents[i][j].get();

                    if (comp.getType().equals(ComponentType.DRILL) || comp.getType().equals(ComponentType.DOUBLE_DRILL)) {
                        int row = i;
                        int col = j;

                        switch (comp.getDirection()){
                            case FRONT -> row--;
                            case BACK -> row++;
                            case LEFT -> col--;
                            case RIGHT -> col++;
                        }

                        if (validRow(row) && validColumn(col) && shipComponents[row][col].isPresent())
                            incorrect.add(shipComponents[row][col].get());


                    }
                }

            }
        }

        return incorrect;
    }

    public Set<Component> getIncorrectComponents(){
        Set<Component> incorrect = new HashSet<>();

        for (int i = 0; i < rows; i++){
            for (int j = 0; j < columns; j++){
                if (shipComponents[i][j].isPresent()) {
                    Component comp = shipComponents[i][j].get();

                    //
                    Map<Direction, Component> adjcent = getAdjacent(i, j);
                    boolean correct = true;
                    for (Direction dir : adjcent.keySet()) {
                        correct = correct && comp.isCompatible(adjcent.get(dir), dir);
                    }

                    if (!correct) {
                        incorrect.add(shipComponents[i][j].get());
                    }
                }

            }
        }

        return incorrect;
    }
}


