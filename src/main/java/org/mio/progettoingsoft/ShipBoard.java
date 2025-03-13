package org.mio.progettoingsoft;

import org.mio.progettoingsoft.components.AlienType;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.Housing;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.*;

import java.util.*;
import java.util.stream.Stream;

public class ShipBoard {
    private final Optional<Component>[][] shipComponents;
    private List<Component> componentList;

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

    public ShipBoard(HousingColor color){
        rows = 5;
        columns = 7;
        shipComponents = new Optional[rows][columns];
        bannedCoordinates = new ArrayList<>(6);

        for (int i = 0; i < rows; i++)
            for(int j = 0; j <columns; j++)
                shipComponents[i][j] = Optional.empty();

        shipComponents[2][3] = Optional.of(new Housing(1, true, color, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE));


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

        baseFirePower = 0;
        baseEnginePower = 0;
    }
    public void setQuantBatteries(int quant) {
        this.availableEnergy = quant;
    }
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

        baseFirePower = 0;
        baseEnginePower = 0;
    }

    public boolean isValidPosition(int row, int column){
        Cordinate coord = new Cordinate(0,0);
        return validRow(row) && validColumn(column) && !bannedCoordinates.contains(coord);
    }

    public int getRows(){
        return rows;
    }

    public int getColumns(){
        return columns;
    }

    public int getBaseEnginePower(){
        return baseEnginePower;
    }

    public float getBaseFirePower(){
        return baseFirePower;
    }

    public Component getComponent(int row, int col) throws EmptyComponentException, InvalidPositionException {
        if (!validRow(row) || !validColumn(col))
            throw new InvalidPositionException(row, col);

        if (shipComponents[row][col].isEmpty())
            throw new EmptyComponentException(row, col);

        return shipComponents[row][col].get();

    }

    public boolean isEmptyComponent(int row, int column) throws InvalidPositionException {
        if (!validRow(row) || validColumn(column))
            throw new InvalidPositionException(row, column);

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

    public void addComponentToPosition(Component component, int row, int column) throws IncorrectPlacementException, InvalidPositionException {
        if (bannedCoordinates.contains(new Cordinate(row, column)))
            throw new InvalidPositionException(row, column);

        if (!validRow(row) || !validColumn(column))
            throw new InvalidPositionException(row, column);

        if (shipComponents[row][column].isPresent()){
            throw new IncorrectPlacementException(row, column, component);
        }


//        Map<Direction, Component> adjacent = getAdjacent(row, column);
//        boolean added = false;
//        for (Direction dir : adjacent.keySet()){
//            added = added || component.isCompatible(adjacent.get(dir), dir);
//        }
//
//        if (!added)
//            throw new IncorrectPlacement(row, column, component);

        shipComponents[row][column] = Optional.of(component);
        availableEnergy += component.getEnergyQuantity();

        for (Component comp : getAdjacent(row, column).values()) {
            component.addAlienType(comp.getColorAlien());
            comp.addAlienType(component.getColorAlien());
        }

        baseFirePower += component.getFirePower() == 2f ? 0 :  component.getFirePower();
        baseEnginePower += component.getEnginePower()== 2 ? 0 : component.getEnginePower();

        getComponentsList();
    }

    public void addRotatedComponentToPosition(Component comp, int row, int column, int angle) throws IncorrectPlacementException, InvalidPositionException {
        while(angle > 0) {
            comp.rotateClockwise();
            angle--;
        }
        addComponentToPosition(comp, row, column);
    }

    public void removeComponent(int row, int column) throws EmptyComponentException {
        if (!validRow(row) || !validColumn(column)){
            throw new InvalidPositionException(row, column);
        }

        if (shipComponents[row][column].isEmpty())
            throw new EmptyComponentException(row, column);

        Component toRemove = shipComponents[row][column].get();
        shipComponents[row][column] = Optional.empty();

        availableEnergy -= toRemove.getEnergyQuantity();
        baseEnginePower -= toRemove.getEnginePower() == 2 ? 0 : toRemove.getEnginePower();
        baseFirePower -= toRemove.getFirePower() == 2f ? 0 : toRemove.getFirePower();
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

    public Stream<Component> getComponentsStream(){
        return getStreamOptComponents()
                .filter(optComp -> optComp.isPresent())
                .map(optComp -> optComp.get());
    }

    public int getQuantBatteries(){
        return availableEnergy;
    }

    public void removeEnergy() throws NotEnoughBatteriesException {
        boolean removed = false;

        for (int i = 0; !removed && i < componentList.size(); i++)
            removed = componentList.get(i).removeOneEnergy();

        if (!removed)
            throw  new NotEnoughBatteriesException();

        availableEnergy--;
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

    /*public void addGood(GoodType type) throws FullGoodDepot {
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
    }*/

    public List<Component> canContainGood(GoodType type){
        return getComponentsStream()
                .filter(comp -> comp.canContainsGood(type))
                .toList();
    }

    public List<Component> canRemoveGoods(){
        return getComponentsStream()
                .filter(comp -> comp.getStoredGoods().values().stream()
                        .anyMatch(val -> val > 0))
                .toList();
    }

    public void addHumanGuest() throws NotEnoughHousingException {
        boolean added = false;

        for (int i = 0; !added && i < componentList.size(); i++)
            added = componentList.get(i).addHumanMember();


        if (!added)
            throw new NotEnoughHousingException();
    }

    public List<Component> canContainsHumanGuest() {
        return getComponentsStream()
                .filter(comp -> comp.canContainsHumanGuest())
                .toList();
    }

    public List<Component> canContainsAlienGuest(AlienType type){
        return getComponentsStream()
                .filter(comp -> comp.canContainsAlien(type))
                .toList();
    }

    public List<Component> canRemoveGuest(){
        return getComponentsStream()
                .filter(comp -> comp.containsGuest())
                .toList();
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

    public List<Component> getIncorrectEngines() {
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

    public List<Component> getIncorrectDrills() {
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

    // checks if the ALIEN_HOUSING components are connected to at least one HOUSING
    // necessary (?) ask the profs
    public Set<Component> getIncorrectAlienHousings(){
        Set<Component> incorrect = new HashSet<>();
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                if(shipComponents[i][j].isPresent()){
                    Component comp = shipComponents[i][j].get();
                    if(comp.getType().equals(ComponentType.ALIEN_HOUSING)){
                        boolean correct = false;
                        Map<Direction, Component> adjacents = getAdjacent(i, j);
                        for(Component component : adjacents.values()){
                            correct = correct ||
                                    (component.getType().equals(ComponentType.HOUSING) &&
                                            !component.isFirstHousing());
                        }
                        if(!correct){
                            incorrect.add(comp);
                        }
                    }
                }
            }
        }
        return incorrect;
    }

    //This method check the correct positioning of the component, except for the direction and nearby placement of drills
    // the method insert in the set both component in case of connector mismatch
    public Set<Component> getIncorrectComponents(){
        Set<Component> incorrect = new HashSet<>();

        for (int i = 0; i < rows; i++){
            for (int j = 0; j < columns; j++){
                if (shipComponents[i][j].isPresent()) {
                    Component comp = shipComponents[i][j].get();

                    //
                    Map<Direction, Component> adjacent = getAdjacent(i, j);
                    boolean correct = true;
                    // Modified by Stefano to check if a component is flying (not connected to any other):
                    if(adjacent.isEmpty()){
                        correct = false;
                    }else{
                        for (Direction dir : adjacent.keySet()) {
                            correct = correct && comp.isCompatible(adjacent.get(dir), dir);
                        }
                    }


                    if (!correct) {
                        incorrect.add(shipComponents[i][j].get());
                    }
                }

            }
        }

        return incorrect;
    }

    //this method count the number of exposed connectors
    public int getExposedConnectors(){
        int numExposedConnectors = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (shipComponents[i][j].isPresent()) {
                    Component comp = shipComponents[i][j].get();
                    Map<Direction, Component> adjacent = getAdjacent(i, j);
                    if(isExposed(comp, adjacent, Direction.FRONT)) numExposedConnectors++;
                    if(isExposed(comp, adjacent, Direction.BACK)) numExposedConnectors++;
                    if(isExposed(comp, adjacent, Direction.LEFT)) numExposedConnectors++;
                    if(isExposed(comp, adjacent, Direction.RIGHT)) numExposedConnectors++;
                }
            }
        }
        return numExposedConnectors;
    }

    public boolean isExposed(Component comp, Map<Direction, Component> adj, Direction dir){
        if(!comp.getConnector(dir).equals(Connector.FLAT)){
            return !adj.containsKey(dir);
        }
        return false;
    }
}


