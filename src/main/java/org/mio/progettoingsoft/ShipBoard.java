package org.mio.progettoingsoft;

import org.mio.progettoingsoft.components.*;
import org.mio.progettoingsoft.exceptions.*;
import org.mio.progettoingsoft.model.ShipBoardEasy;
import org.mio.progettoingsoft.model.ShipBoardNormal;
import org.mio.progettoingsoft.model.enums.GameMode;

import java.util.*;
import java.util.function.DoubleBinaryOperator;
import java.util.stream.Stream;

public abstract class ShipBoard {
    private final Optional<Component>[][] shipComponents;

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
    private boolean completedBuild;

    private final int offsetCol;
    private final int offsetRow;

    private final FlyBoard flyBoard;

    public static ShipBoard createShipBoard(GameMode mode, HousingColor color, FlyBoard flyBoard){
        ShipBoard shipBoard = null;


        switch (mode){
            case EASY -> shipBoard = new ShipBoardEasy(color, flyBoard);
            case NORMAL -> shipBoard = new ShipBoardNormal(color, flyBoard);
        }

        return shipBoard;
    }

    protected ShipBoard(HousingColor color, FlyBoard flyBoard) {
        this.flyBoard = flyBoard;
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
        this.bannedCoordinates = getBannedCoordinates();

        availableEnergy = 0;
        baseFirePower = 0;
        baseEnginePower = 0;
        exposedConnectors = 0;
        maxEnergy = 0;
        maxSpecialGoods = 0;
        numSpecialGoods = 0;
        maxNormalGoods = 0;
        numNormalGoods = 0;
        completedBuild = false;

        // Add the starting cabin to the ship, the id identifies the correct tile image, so it's related with the color
        try {
            this.addComponentToPosition(color.getIdByColor(), new Cordinate(2, 3), 0);
        } catch (IncorrectShipBoardException e) {

        }
    }

    protected abstract List<Cordinate> getBannedCoordinates();

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

    public Component getComponent(int position){
        int[] cord = getCordinate(position);
        return getComponent(cord[0], cord[1]);
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

    public Map<Direction, Component> getAdjacentConnected(int row, int column) {
        if(shipComponents[row][column].isEmpty()){
            throw new EmptyComponentException(row, column);
        }
        Map<Direction, Component> adjacents = new HashMap();

        if (validRow(row - 1) && shipComponents[row - 1][column].isPresent() && shipComponents[row][column].isPresent() && shipComponents[row][column].get().isConnected(shipComponents[row - 1][column].get(), Direction.FRONT)) {
            adjacents.put(Direction.FRONT, shipComponents[row - 1][column].get());
        }
        if (validRow(row + 1) && shipComponents[row + 1][column].isPresent() && shipComponents[row][column].isPresent() && shipComponents[row][column].get().isConnected(shipComponents[row + 1][column].get(), Direction.BACK)) {
            adjacents.put(Direction.BACK, shipComponents[row + 1][column].get());
        }
        if (validColumn(column + 1) && shipComponents[row][column + 1].isPresent() && shipComponents[row][column].isPresent() && shipComponents[row][column].get().isConnected(shipComponents[row][column + 1].get(), Direction.RIGHT)) {
            adjacents.put(Direction.RIGHT, shipComponents[row][column + 1].get());
        }
        if (validColumn(column - 1) && shipComponents[row][column - 1].isPresent() && shipComponents[row][column].isPresent() && shipComponents[row][column].get().isConnected(shipComponents[row][column - 1].get(), Direction.LEFT)) {
            adjacents.put(Direction.LEFT, shipComponents[row][column - 1].get());
        }

        return adjacents;
    }

    /**
     * add the {@link Component} with given id, to the given rotation, after rotates it
     *
     * @param componentId the id of the {@link Component} to insert
     * @param cordinate the position where to insert id
     * @param angle the angle to rotate
     * @throws IncorrectShipBoardException if the cordinate is invalid
     */
    public void addComponentToPosition(int componentId, Cordinate cordinate, int angle) throws IncorrectShipBoardException {
        if (bannedCoordinates.contains(cordinate))
            throw new IncorrectShipBoardException("Cannot insert in this cordinate");

        if (getComponentByCord(cordinate).isPresent())
            throw new IncorrectShipBoardException("Cordinate already full");

        Component component = flyBoard.getComponentById(componentId);
        component.rotate(angle);

        boolean validPosition = cordinate.equals(new Cordinate(2, 3)) ||
                cordinate.getAdjacent().stream().map(cord -> getComponentByCord(cord)).filter(optComp -> optComp.isPresent()).findFirst().equals(Optional.empty());

        if (!validPosition)
            throw new IncorrectShipBoardException("Not adjacent components");

        shipComponents[cordinate.getRow()][cordinate.getColumn()] = Optional.of(component);
        int a = 0;
    }


    /**
     * return the {@link Component} if present, Optional.of otherwise
     * @param cordinate the {@link Cordinate} for the search
     * @return
     */
    private Optional<Component> getComponentByCord(Cordinate cordinate){
        return shipComponents[cordinate.getRow()][cordinate.getColumn()];
    }

//    public void addComponentToPosition(Component component, int row, int column) throws IncorrectPlacementException, InvalidPositionException {
//        if (bannedCoordinates.contains(new Cordinate(row, column)))
//            throw new InvalidPositionException(row, column);
//
//        if (!validRow(row) || !validColumn(column))
//            throw new InvalidPositionException(row, column);
//
//        if (shipComponents[row][column].isPresent()) {
//            throw new IncorrectPlacementException(row, column, component);
//        }
//
//        component.setRow(row);
//        component.setColumn(column);
//
//        shipComponents[row][column] = Optional.of(component);
//
//        availableEnergy += component.getEnergyQuantity();
//
//        for (Component comp : getAdjacent(row, column).values()) {
//            component.addAlienType(comp.getColorAlien());
//            comp.addAlienType(component.getColorAlien());
//        }
//
//        baseFirePower += component.getFirePower() == 2f ? 0 : component.getFirePower();
//        baseEnginePower += component.getEnginePower() == 2 ? 0 : component.getEnginePower();
//    }

//    public void addRotatedComponentToPosition(Component comp, int row, int column, int angle) throws IncorrectPlacementException, InvalidPositionException {
//        while (angle > 0) {
//            comp.rotateClockwise();
//            angle--;
//        }
//        addComponentToPosition(comp, row, column);
//    }

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

    public void removeComponent(int componentPosition){
        int row = componentPosition / columns;
        int col = componentPosition % columns;

        removeComponent(row, col);
    }

    private Stream<Optional<Component>> getStreamOptComponents() {
        return Arrays.stream(shipComponents).flatMap(Arrays::stream);
    }

    public Optional<Component>[][] getComponentsMatrix() {
        return shipComponents;
    }

    public Stream<Component> getComponentsStream() {
        return getStreamOptComponents()
                .filter(optComp -> optComp.isPresent())
                .map(optComp -> optComp.get());
    }

    public List<Component> getComponentsList() {
        return this.getComponentsStream().toList();
    }

    public int getQuantBatteries() {
        return availableEnergy;
    }

    public void removeEnergy() throws NotEnoughBatteriesException {
        boolean removed = false;
        List<Component> componentList = this.getComponentsList();

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

    public List<Component> canRemoveGoods(GoodType type){
        return getComponentsStream()
                .filter(comp -> comp.getStoredGoods().getOrDefault(type, 0) > 0)
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
        List<Component> componentList = this.getComponentsList();

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
                    Map<Direction, Component> adj = getAdjacentConnected(comp.getRow(), comp.getColumn());
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

    //TODO : va testata
    public void stoleGood(int quantity) {

        Iterator<GoodType> iter = GoodType.sortedList.iterator();

        while (quantity > 0 && iter.hasNext()){
            GoodType type = iter.next();
            List<Component> depos = canRemoveGoods(type);

            for (int i = 0; quantity > 0 && i < depos.size(); i++){
                int toRemove = Integer.min(quantity, depos.get(i).getStoredGoods().get(type));

                quantity = quantity -= toRemove;
                depos.get(i).setGoodsDepot(type, depos.get(i).getStoredGoods().get(type) - toRemove);
            }
        }

        if (quantity > 0){
            removeEnergy(Integer.min(quantity, availableEnergy));
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

    public int[] getCordinate(int pos){
        int[] cord = new int[2];

        cord[0] = pos / columns;
        cord[1] = pos % columns;

        return cord;
    }

    public float getMaximumFirePower(){
        return (float) getComponentsStream()
                .mapToDouble(Component::getFirePower)
                .sum();
    }

    public void keepPart(int row, int col){
        List<Set<Component>> multiple = getMultiplePieces();
        int partToKeep = -1;
        for(int i = 0; i < multiple.size(); i++){
            Set<Component> set = multiple.get(i);
            for(Component c : set){
                if(c.getRow() == row && c.getColumn() == col){
                    partToKeep = i;
                    break;
                }
            }
            if(partToKeep != -1){
                break;
            }
        }
        if(partToKeep == -1){
            throw new BadParameterException("No components at row " + row + " and column " + col);
        }
        for(int i = 0; i < multiple.size(); i++){
            if(i != partToKeep){
                Set<Component> set = multiple.get(i);
                for(Component c : set){
                    this.removeComponent(c.getRow(), c.getColumn());
                }
            }
        }
    }

    public void removeGoods(int amount){
        GoodType nowRemoving = GoodType.RED;
        int i = amount;
        List<Component> depots = this.getComponentsStream()
                .filter(c -> c.getType() == ComponentType.DEPOT)
                .toList();
        for (Component component : depots) {
            while (i > 0) {
                if (component.removeGood(nowRemoving)) i--;
                else break;
            }
        }
        if (i > 0) {
            nowRemoving = GoodType.YELLOW;
            for (Component component : depots) {
                while (i > 0) {
                    if (component.removeGood(nowRemoving)) i--;
                    else break;
                }
            }
            if (i > 0) {
                nowRemoving = GoodType.GREEN;
                for (Component component : depots) {
                    while (i > 0) {
                        if (component.removeGood(nowRemoving)) i--;
                        else break;
                    }
                }
                if (i > 0) {
                    nowRemoving = GoodType.BLUE;
                    for (Component component : depots) {
                        while (i > 0) {
                            if (component.removeGood(nowRemoving)) i--;
                            else break;
                        }
                    }
                    if (i > 0) {
                        this.removeEnergy(i);
                    }
                }
            }
        }
    }
}


