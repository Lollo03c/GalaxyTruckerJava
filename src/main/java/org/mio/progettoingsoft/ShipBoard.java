package org.mio.progettoingsoft;

import org.mio.progettoingsoft.components.*;
import org.mio.progettoingsoft.exceptions.*;

import java.util.*;
import java.util.stream.Stream;

public class ShipBoard {
    private final Optional<Component>[][] shipComponents;
    private List<Component> componentList;

    private Component[] bookedComponents;

    private final List<Cordinate> bannedCoordinates;

    private final int rows;
    private final int columns;

    private int availableEnergy;
    private float baseFirePower;
    private int baseEnginePower;
    private float activatedFirePower;
    private int activatedEnginePower;
    private int exposedConnectors;
    private int maxEnergy;
    private int maxSpecialGoods;
    private int numSpecialGoods;
    private int maxNormalGoods;
    private int numNormalGoods;
    private int numAliens;
    private int numAstronauts;
    private boolean completedBuild;

    private final int offsetCol;
    private final int offsetRow;

    public ShipBoard(HousingColor color) {
        rows = 5;
        columns = 7;

        offsetRow = 5;
        offsetCol = 4;

        shipComponents = new Optional[rows][columns];
        bookedComponents = new Component[2];

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                shipComponents[i][j] = Optional.empty();


        // Add to bannedCoordinates all the cells where components cannot be placed
        bannedCoordinates = new ArrayList<>(6);
        bannedCoordinates.add(new Cordinate(0, 0));
        bannedCoordinates.add(new Cordinate(0, 1));
        bannedCoordinates.add(new Cordinate(0, 3));
        bannedCoordinates.add(new Cordinate(1, 0));
        bannedCoordinates.add(new Cordinate(1, 6));
        bannedCoordinates.add(new Cordinate(4, 3));

        availableEnergy = 0;
        baseFirePower = 0;
        baseEnginePower = 0;
        exposedConnectors = 0;
        maxEnergy = 0;
        maxSpecialGoods = 0;
        numSpecialGoods = 0;
        maxNormalGoods = 0;
        numNormalGoods = 0;
        numAliens = 0;
        numAstronauts = 0;
        completedBuild = false;
        // Add the starting cabin to the ship, the id identifies the correct tile image, so it's related with the color
        this.addComponentToPosition(new Housing(HousingColor.getIdByColor(color), true, color, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE), 2, 3);

    }


    public void addBookedComponent(Component bookedComponent) throws NotEnoughSpaceForBookedComponentException{
        if(bookedComponents[0] == null){
            bookedComponents[0] = bookedComponent;
        }else if(bookedComponents[1] == null){
            bookedComponents[1] = bookedComponent;
        }else{
            throw new NotEnoughSpaceForBookedComponentException("ShipBoard: Too many booked components");
        }
    }

    public Component[] getBookedComponents() {
        return bookedComponents;
    }
    public void setQuantBatteries(int quant) {
        this.availableEnergy = quant;
    }

    // activated firepower: tmp property to store the firepower after activating double drills.
    // How to use: askDoubleDrill, set activFP = base + activated, do whatever you need, then set activated = base
    public float getActivatedFirePower() {
        return activatedFirePower;
    }

    public void setActivatedFirePower(float activatedFirePower) {
        this.activatedFirePower = activatedFirePower;
    }

    public boolean isValidPosition(int row, int column) {
        Cordinate coord = new Cordinate(0, 0);
        return validRow(row) && validColumn(column) && !bannedCoordinates.contains(coord);
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getOffsetCol() {
        return offsetCol;
    }

    public int getOffsetRow() {
        return offsetRow;
    }

    public int getBaseEnginePower() {
        return baseEnginePower;
    }

    public float getBaseFirePower() {
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

    public Map<Direction, Component> getAdjacent(int row, int column) {
        Map<Direction, Component> adjacents = new HashMap();

        if (validRow(row - 1) && shipComponents[row - 1][column].isPresent()) {
            adjacents.put(Direction.FRONT, shipComponents[row - 1][column].get());
        }
        if (validRow(row + 1) && shipComponents[row + 1][column].isPresent()) {
            adjacents.put(Direction.BACK, shipComponents[row + 1][column].get());
        }
        if (validColumn(column + 1) && shipComponents[row][column + 1].isPresent()) {
            adjacents.put(Direction.RIGHT, shipComponents[row][column + 1].get());
        }
        if (validColumn(column - 1) && shipComponents[row][column - 1].isPresent()) {
            adjacents.put(Direction.LEFT, shipComponents[row][column - 1].get());
        }

        return adjacents;
    }

    public void addComponentToPosition(Component component, int row, int column) throws IncorrectPlacementException, InvalidPositionException {
        if (bannedCoordinates.contains(new Cordinate(row, column)))
            throw new InvalidPositionException(row, column);

        if (!validRow(row) || !validColumn(column))
            throw new InvalidPositionException(row, column);

        if (shipComponents[row][column].isPresent()) {
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

        // Added by stef: it's useful to store the cordinates of the component in the component itself, in this way it's
        // possible to get the adjacents components (for example from the stream of components)
        component.setRow(row);
        component.setColumn(column);

        shipComponents[row][column] = Optional.of(component);

        availableEnergy += component.getEnergyQuantity();

        for (Component comp : getAdjacent(row, column).values()) {
            component.addAlienType(comp.getColorAlien());
            comp.addAlienType(component.getColorAlien());
        }

        baseFirePower += component.getFirePower() == 2f ? 0 : component.getFirePower();
        baseEnginePower += component.getEnginePower() == 2 ? 0 : component.getEnginePower();
    }

    public void addRotatedComponentToPosition(Component comp, int row, int column, int angle) throws IncorrectPlacementException, InvalidPositionException {
        while (angle > 0) {
            comp.rotateClockwise();
            angle--;
        }
        addComponentToPosition(comp, row, column);
    }

    //non basta
    //bisogna controllare l'eliminazione a cascata
    public void removeComponent(int row, int column) throws EmptyComponentException {
        if (!validRow(row) || !validColumn(column)) {
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

    //non basta
    //bisogna controllare l'eliminazione a cascata
    public void removeComponent(Component component) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (shipComponents[i][j].isPresent() && shipComponents[i][j].get().equals(component))
                    shipComponents[i][j] = Optional.empty();

            }
        }

        availableEnergy -= component.getEnergyQuantity();
        baseEnginePower -= component.getEnginePower() == 2 ? 0 : component.getEnginePower();
        baseFirePower -= component.getFirePower() == 2.0f ? 0 : component.getFirePower();
    }

    private Stream<Optional<Component>> getStreamOptComponents() {
        return Arrays.stream(shipComponents).flatMap(Arrays::stream);
    }

    public Optional<Component>[][] getComponentsList() {
        return shipComponents;
    }

    public Stream<Component> getComponentsStream() {
        return getStreamOptComponents()
                .filter(optComp -> optComp.isPresent())
                .map(optComp -> optComp.get());
    }

    public int getQuantBatteries() {
        return availableEnergy;
    }

    public void removeEnergy() throws NotEnoughBatteriesException {
        boolean removed = false;

        for (int i = 0; !removed && i < componentList.size(); i++)
            removed = componentList.get(i).removeOneEnergy();

        if (!removed)
            throw new NotEnoughBatteriesException();

        availableEnergy--;
    }

    public void removeEnergy(int quant) throws NotEnoughBatteriesException {
        for (int i = 0; i < quant; i++)
            removeEnergy();
    }


    private boolean validRow(int row) {
        return row >= 0 && row < rows;
    }

    private boolean validColumn(int column) {
        return column >= 0 && column < columns;
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

    public List<Component> canContainGood(GoodType type) {
        return getComponentsStream()
                .filter(comp -> comp.canContainsGood(type))
                .toList();
    }

    public List<Component> canRemoveGoods() {
        return getComponentsStream()
                .filter(comp -> comp.getStoredGoods().values().stream()
                        .anyMatch(val -> val > 0))
                .toList();
    }
    //return true if the good has been moved correctly, false otherwise
    //the various controls are inside the methods addGood
    public boolean changeDepot(GoodType good, Depot oldDepot, Depot newDepot) {
        if(newDepot.addGood(good)){
            return oldDepot.removeGood(good);
        }
        return false;
    }

    public boolean discardGood(GoodType good, Depot depot) {
        return depot.removeGood(good);
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

    public List<Component> canContainsAlienGuest(AlienType type) {
        return getComponentsStream()
                .filter(comp -> comp.canContainsAlien(type))
                .toList();
    }

    public List<Component> canRemoveGuest() {
        return getComponentsStream()
                .filter(comp -> comp.containsGuest())
                .toList();
    }


    public List<Component> getDoubleEngine() {
        return getComponentsStream()
                .filter(comp -> comp.getEnginePower() == 2)
                .toList();
    }

    public List<Component> getDoubleDrill(Direction dir) {
        return getComponentsStream()
                .filter(comp -> comp.getFirePower() == 2)
                .filter(comp -> comp.getDirection() != null)
                .filter(comp -> comp.getDirection().equals(dir))
                .toList();
    }

    public List<Component> getIncorrectEngines() {
        List<Component> incorrect = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++){
                if (shipComponents[i][j].isPresent()) {
                    Component comp = shipComponents[i][j].get();
                    if (comp.getEnginePower() > 0) {
                        if (!comp.getDirection().equals(Direction.BACK)) {
                            incorrect.add(comp);
                        } else if (validRow(i + 1) && shipComponents[i + 1][j].isPresent()) {
                            incorrect.add(shipComponents[i][j].get());
                        }
                    }
                }
            }
        }
        return incorrect;
    }

    public List<Component> getIncorrectDrills() {
        List<Component> incorrect = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (shipComponents[i][j].isPresent()) {
                    Component comp = shipComponents[i][j].get();

                    if (comp.getFirePower() > 0) {
                        int row = i;
                        int col = j;

                        switch (comp.getDirection()) {
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

    /*
    // checks if the ALIEN_HOUSING components are connected to at least one HOUSING
    // not necessary
    public Set<Component> getIncorrectAlienHousings() {
        Set<Component> incorrect = new HashSet<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (shipComponents[i][j].isPresent()) {
                    Component comp = shipComponents[i][j].get();
                    if (comp.getType().equals(ComponentType.ALIEN_HOUSING)) {
                        boolean correct = false;
                        Map<Direction, Component> adjacents = getAdjacent(i, j);
                        for (Component component : adjacents.values()) {
                            correct = correct ||
                                    (component.getType().equals(ComponentType.HOUSING) &&
                                            !component.isFirstHousing());
                        }
                        if (!correct) {
                            incorrect.add(comp);
                        }
                    }
                }
            }
        }
        return incorrect;
    }
     */

    // This method check the correct positioning of the component, except for the direction and nearby placement of drills/engines
    // the method insert in the set both component in case of connector mismatch
    public Set<Component> getIncorrectComponents() {
        Set<Component> incorrect = new HashSet<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (shipComponents[i][j].isPresent()) {
                    Component comp = shipComponents[i][j].get();

                    //
                    Map<Direction, Component> adjacent = getAdjacent(i, j);
                    boolean correct = true;

                    /* this part is now made by the getMultiplePieces method
                    // Modified by Stefano to check if a component is flying (not connected to any other):
                    if (adjacent.isEmpty()) {
                        correct = false;
                    } else {
                        for (Direction dir : adjacent.keySet()) {
                            correct = correct && comp.isCompatible(adjacent.get(dir), dir);
                        }
                    }*/

                    for (Direction dir : adjacent.keySet()) {
                        correct = correct && comp.isCompatible(adjacent.get(dir), dir);
                    }

                    if (!correct) {
                        incorrect.add(shipComponents[i][j].get());
                    }
                }

            }
        }

        return incorrect;
    }

    // this method returns the disconnected pieces (not only single components but group of them not interconnected)
    // this method uses an algorithm of Breadth-First Search (a big thanks to API)
    public List<Set<Component>> getMultiplePieces(){
        List<Set<Component>> multiple = new ArrayList<>();
        if(this.getComponentsStream().findAny().isPresent()){
            int[][] visited = new int[rows][columns];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    visited[i][j] = -1;
                }
            }

            while(this.getComponentsStream().anyMatch(comp -> visited[comp.getRow()][comp.getColumn()] == -1)){
                Component c = this.getComponentsStream().filter(comp -> visited[comp.getRow()][comp.getColumn()] == -1).findFirst().get();

                Queue<Component> queue = new LinkedList<>();
                queue.add(c);
                visited[c.getRow()][c.getColumn()] = 0;

                Set<Component> part = new HashSet<>();

                while (!queue.isEmpty()) {
                    Component comp = queue.remove();
                    part.add(comp);
                    // QUESTO METODO USA UN GET ADJACENT CHE NON VA BENE: dovrebbe considerare non adiacenti i
                    Map<Direction, Component> adj = getAdjacent(comp.getRow(), comp.getColumn());
                    for(Component component : adj.values()){
                        if(visited[component.getRow()][component.getColumn()] == -1){
                            visited[component.getRow()][component.getColumn()] = 0;
                            queue.add(component);
                        }
                    }
                    visited[comp.getRow()][comp.getColumn()] = 1;
                }

                multiple.add(part);

            }
        }
        return multiple;
    }

    //this method count the number of exposed connectors
    public int getExposedConnectors() {
        int numExposedConnectors = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (shipComponents[i][j].isPresent()) {
                    Component comp = shipComponents[i][j].get();
                    Map<Direction, Component> adjacent = getAdjacent(i, j);
                    if (isExposed(comp, adjacent, Direction.FRONT)) numExposedConnectors++;
                    if (isExposed(comp, adjacent, Direction.BACK)) numExposedConnectors++;
                    if (isExposed(comp, adjacent, Direction.LEFT)) numExposedConnectors++;
                    if (isExposed(comp, adjacent, Direction.RIGHT)) numExposedConnectors++;
                }
            }
        }
        return numExposedConnectors;
    }

    public boolean isExposed(Component comp, Map<Direction, Component> adj, Direction dir) {
        if (!comp.getConnector(dir).equals(Connector.FLAT)) {
            return !adj.containsKey(dir);
        }
        return false;
    }

//    public int getHumanNumber(){
//        return this.getComponentsStream().filter(c -> c.getType().equals(ComponentType.HOUSING))
//                .map(c -> c.getNumHumanMembers()).reduce(0, Integer::sum);
//    }

    /*
    public int getAlienNumber(){
        int sum = 0;
        sum += (int) this.getComponentsStream().filter(c -> (
                c.getType().equals(ComponentType.HOUSING) &&
                        c.canContainsAlien(AlienType.PURPLE) &&
                        c.getGuestedAlien().get(AlienType.PURPLE)))
                .count();
        sum += (int) this.getComponentsStream().filter(c -> (
                        c.getType().equals(ComponentType.HOUSING) &&
                                c.canContainsAlien(AlienType.BROWN) &&
                                c.getGuestedAlien().get(AlienType.BROWN)))
                .count();
        return sum;
    }*/

    public int getQuantityGuests() {
        return getComponentsStream().
                mapToInt(comp -> comp.getQuantityGuests())
                .sum();
    }

    public String printPosition(Component comp) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (shipComponents[i][j].isPresent()) {
                    if (shipComponents[i][j].get() == comp)
                        return "(" + i + ", " + j + ")";
                }
            }
        }
        return "";
    }

    public void stoleGood() {
        Map<GoodType, Integer> storedGoods = new HashMap<>();

        List<Component> components = getComponentsStream().toList();
        for (Component comp : components) {
            storedGoods.putAll(comp.getStoredGoods());
        }

        GoodType toRemove = GoodType.RED;
        boolean chosen = false;
        if (storedGoods.containsKey(GoodType.RED) && storedGoods.get(GoodType.RED) > 0) {
            toRemove = GoodType.RED;
            chosen = true;
        } else if (storedGoods.containsKey(GoodType.YELLOW) && storedGoods.get(GoodType.YELLOW) > 0) {
            toRemove = GoodType.YELLOW;
            chosen = true;
        } else if (storedGoods.containsKey(GoodType.GREEN) && storedGoods.get(GoodType.GREEN) > 0) {
            toRemove = GoodType.GREEN;
            chosen = true;
        } else if (storedGoods.containsKey(GoodType.BLUE) && storedGoods.get(GoodType.BLUE) > 0) {
            toRemove = GoodType.BLUE;
            chosen = true;
        }

        if (chosen) {
            for (Component comp : components) {
                if (comp.removeGood(toRemove)) {
                    break;
                }
            }
        } else {
            removeEnergy();
        }
    }

    public int getColumnComponent(Component comp) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (shipComponents[i][j].isPresent() && shipComponents[i][j].get() == comp)
                    return j;
            }
        }
        return -1;
    }

    // activated engine power: tmp property to store the engine power after activating double engines.
    // How to use: askDoubleEngine, set activEP = base + activated, do whatever you need, then set activated = base
    public void setActivatedEnginePower(int activatedEnginePower) {
        this.activatedEnginePower = activatedEnginePower;
    }

    public int getActivatedEnginePower() {
        return activatedEnginePower;
    }

    // theese three methods compare a shipboard to another based on the numeber of crew members, the firepower and the
    // engine power, they're useful for the combat zone adv card
    public int compareCrew(ShipBoard other) {
        if (this.getQuantityGuests() > other.getQuantityGuests())
            return 1;
        else if (this.getQuantityGuests() < other.getQuantityGuests())
            return -1;
        return 0;
    }

    public int compareActivatedFirePower(ShipBoard other) {
        if (this.getActivatedFirePower() > other.getActivatedFirePower())
            return 1;
        else if (this.getActivatedFirePower() < other.getActivatedFirePower())
            return -1;
        return 0;
    }

    public int compareActivatedEnginePower(ShipBoard other) {
        if (this.getActivatedEnginePower() > other.getActivatedEnginePower())
            return 1;
        else if (this.getActivatedEnginePower() < other.getActivatedEnginePower())
            return -1;
        return 0;
    }

    public String toString() {
        String out = "";
        for(Component c : getComponentsStream().toList()){
            out += c.toString() + "\n";
        }
        return out;
    }
}


