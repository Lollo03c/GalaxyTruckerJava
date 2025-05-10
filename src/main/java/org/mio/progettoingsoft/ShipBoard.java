package org.mio.progettoingsoft;

import org.mio.progettoingsoft.components.*;
import org.mio.progettoingsoft.exceptions.*;
import org.mio.progettoingsoft.model.ShipBoardEasy;
import org.mio.progettoingsoft.model.ShipBoardNormal;
import org.mio.progettoingsoft.model.enums.GameMode;

import java.util.*;
import java.util.stream.Stream;

public abstract class ShipBoard {
    private final Optional<Component>[][] shipComponents;

    private Component[] bookedComponents;

    private final List<Cordinate> bannedCoordinates;

    private final int rows;
    private final int columns;

    private int discaredComponents = 0;

    private double activatedFirePower;
    private int activatedEnginePower;
    private int exposedConnectors;
    private boolean completedBuild;

    private final int offsetCol;
    private final int offsetRow;

    private final FlyBoard flyBoard;

    /**
     * static method used to create an istance of ShipBoard based on the {@link GameMode}
     *
     * @param mode the {@link GameMode} of the game
     * @param color the {@link Housing} of the shipboard
     * @param flyBoard of {@link FlyBoard} used to get the {@link Component} and {@link AdventureCard} lists
     * @return
     */
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

        exposedConnectors = 0;
        completedBuild = false;

        // Add the starting cabin to the ship, the id identifies the correct tile image, so it's related with the color
        try {
            this.addComponentToPosition(color.getIdByColor(), new Cordinate(2, 3), 0);
        } catch (IncorrectShipBoardException e) {

        }
    }

    /**
     *
     * @return the list of banneed {@link Cordinate} based on the {@link GameMode} of the game
     */
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

    // activated firepower: tmp property to store the firepower after activating double drills.
    // How to use: askDoubleDrill, set activFP = base + activated, do whatever you need, then set activated = base
    public double getActivatedFirePower() {
        return activatedFirePower;
    }

    public void setActivatedFirePower(double activatedFirePower) {
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

    /**
     *
     * @return the total engine power when all {@link DoubleEngine} are not activated
     */
    public int getBaseEnginePower() {
        return getCompStream().mapToInt(comp -> comp.getEnginePower()).sum();
    }

    /**
     *
     * @return the total fire power when all {@link DoubleDrill} are not activated
     */
    public double getBaseFirePower() {
        return getComponentsStream().mapToDouble (comp -> comp.getFirePower()).sum();
    }

    /**
     *
     * @param type the {@link GoodType} to search for
     * @return the number of goods of the given {@link GoodType} contained in the shipboard
     */
    public int getStoredQuantity(GoodType type){
        return (int) getCompStream().flatMap(comp -> comp.getStoredGoods().stream())
                .filter(t -> t.equals(type))
                .count();
    }

    /**
     *
     * @param cord the {@link Cordinate} of the link to search the adjacent {@link Component}s
     * @return a Map<{@link Direction}, {@link Component}> consisting the adjacent {@link Component} and the relative {@link Direction}
     */
    public Map<Direction, Component> getAdjacent(Cordinate cord) {
        Map<Direction, Component> adjacents = new HashMap();

        for (Direction dir : Direction.values()){
            try{
                Cordinate cordinate = new Cordinate(cord.getRow() + dir.offsetRow(), cord.getColumn() + dir.offsetCol());
                Optional<Component> optComp = getOptComponentByCord(cordinate);
                if (optComp.isPresent())
                    adjacents.put(dir, optComp.get());
            }
            catch (InvalidCordinate e){
            }

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

        if (getOptComponentByCord(cordinate).isPresent())
            throw new IncorrectShipBoardException("Cordinate already full");

        Component component = flyBoard.getComponentById(componentId);
        component.rotate(angle);

        boolean validPosition = cordinate.equals(new Cordinate(2, 3)) ||
                !getAdjacent(cordinate).values().isEmpty();

        if (!validPosition)
            throw new IncorrectShipBoardException("Not adjacent components");

        shipComponents[cordinate.getRow()][cordinate.getColumn()] = Optional.of(component);
    }


    /**
     * return the {@link Component} if present, Optional.of otherwise
     * @param cordinate the {@link Cordinate} for the search
     * @return
     */
    public Optional<Component> getOptComponentByCord(Cordinate cordinate){
        return shipComponents[cordinate.getRow()][cordinate.getColumn()];
    }


    /**
     *
     * @param cordinate the {@link Cordinate} of the tile to empty
     * @throws IncorrectShipBoardException if the tile is already empty
     */
    public void removeComponent(Cordinate cordinate) throws IncorrectShipBoardException{
        if (getOptComponentByCord(cordinate).isEmpty())
            throw new IncorrectShipBoardException("tile is empty. nothing to remove");

        shipComponents[cordinate.getRow()][cordinate.getRow()] = Optional.empty();
        discaredComponents++;

    }


    /**
     * remove the given quantity of energy from {@link EnergyDepot} if possible, throws {@link IncorrectShipBoardException} otherwise
     * @param quantiy the quanity to remove
     * @return the list of id {@link Component} from which the energy have been taken
     * @throws IncorrectShipBoardException if not enough energy to remove
     */
    public List<Integer> removeEnergy(int quantiy) throws IncorrectShipBoardException{
        List<Integer> idComps = new ArrayList<>();

        Iterator<Component> iterator = getCompIterator();
        while(iterator.hasNext() && quantiy > 0){
            Component comp = iterator.next();

            if (comp.getEnergyQuantity() > 0){
                int toRemove = Integer.min(quantiy, comp.getEnergyQuantity());
                quantiy -= toRemove;

                for (int i = 0; i < toRemove; i++){
                    idComps.add(comp.getId());
                }
            }
        }

        if (quantiy > 0)
            throw new IncorrectShipBoardException("not enought energy to remove");

        for (int id : idComps){
            flyBoard.getComponentById(id).removeOneEnergy();
        }

        return idComps;
    }


    /**
     *
     * @return the iterator which enable to iterator over all the components in the shipboard
     */
    private Iterator<Component> getCompIterator(){
        return Stream.of(shipComponents).flatMap(Stream::of).filter(Optional::isPresent).map(Optional::get).iterator();
    }

    /**
     *
     * @return the stream of the components in the shipboard
     */
    private Stream<Component> getCompStream(){
        return Stream.of(shipComponents).flatMap(Stream::of).filter(Optional::isPresent).map(Optional::get);
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

    /**
     *
     * @return the total number of energy left
     */
    public int getQuantBatteries() {
        return getComponentsStream().mapToInt(Component::getEnergyQuantity).sum();
    }

    private boolean validRow(int row) {
        return row >= 0 && row < rows;
    }

    private boolean validColumn(int column) {
        return column >= 0 && column < columns;
    }


    public List<Component> getDoubleEngine() {
        return getComponentsStream()
                .filter(comp -> comp.getEnginePower(true) == 2)
                .toList();
    }

    public List<Component> getDoubleDrill(Direction dir) {
        return getComponentsStream()
                .filter(comp -> comp.getFirePower(true) == 2)
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
//                    Map<Direction, Component> adjacent = getAdjacent(i, j);
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

//                    for (Direction dir : adjacent.keySet()) {
//                        correct = correct && comp.isCompatible(adjacent.get(dir), dir);
                    }

//                    if (!correct) {
                        incorrect.add(shipComponents[i][j].get());
                    }
                }

        return null;
    }


//        return incorrect;

    // this method returns the disconnected pieces (not only single components but group of them not interconnected)
    // this method uses an algorithm of Breadth-First Search (a big thanks to API)
//    public List<Set<Component>> getMultiplePieces(){
//        List<Set<Component>> multiple = new ArrayList<>();
//        if(this.getComponentsStream().findAny().isPresent()){
//            int[][] visited = new int[rows][columns];
//            for (int i = 0; i < rows; i++) {
//                for (int j = 0; j < columns; j++) {
//                    visited[i][j] = -1;
//                }
//            }
//
//            while(this.getComponentsStream().anyMatch(comp -> visited[comp.getRow()][comp.getColumn()] == -1)){
//                Component c = this.getComponentsStream().filter(comp -> visited[comp.getRow()][comp.getColumn()] == -1).findFirst().get();
//
//                Queue<Component> queue = new LinkedList<>();
//                queue.add(c);
//                visited[c.getRow()][c.getColumn()] = 0;
//
//                Set<Component> part = new HashSet<>();
//
//                while (!queue.isEmpty()) {
//                    Component comp = queue.remove();
//                    part.add(comp);
//                    Map<Direction, Component> adj = getAdjacentConnected(comp.getRow(), comp.getColumn());
//                    for(Component component : adj.values()){
//                        if(visited[component.getRow()][component.getColumn()] == -1){
//                            visited[component.getRow()][component.getColumn()] = 0;
//                            queue.add(component);
//                        }
//                    }
//                    visited[comp.getRow()][comp.getColumn()] = 1;
//                }
//
//                multiple.add(part);
//
//            }
//        }
//        return multiple;
//    }

    //this method count the number of exposed connectors
//    public int getExposedConnectors() {
//        int numExposedConnectors = 0;
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < columns; j++) {
//                if (shipComponents[i][j].isPresent()) {
//                    Component comp = shipComponents[i][j].get();
////                    Map<Direction, Component> adjacent = getAdjacent(i, j);
//                    if (isExposed(comp, adjacent, Direction.FRONT)) numExposedConnectors++;
//                    if (isExposed(comp, adjacent, Direction.BACK)) numExposedConnectors++;
//                    if (isExposed(comp, adjacent, Direction.LEFT)) numExposedConnectors++;
//                    if (isExposed(comp, adjacent, Direction.RIGHT)) numExposedConnectors++;
//                }
//            }
//        }
//        return numExposedConnectors;
//    }

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

    /**
     *
     * @return the number of guests hosted in the shipBoard
     */
    public int getQuantityGuests() {
        return getComponentsStream().
                mapToInt(comp -> comp.getGuests().size())
                .sum();
    }

    /**
     *
     * @param quantity int : quantity of goods / batteries to stole
     */
    public void stoleGood(int quantity) {

        Iterator<GoodType> iter = GoodType.sortedList.iterator();
        Map<GoodType, Long> toRemove = new HashMap<>();

        while (quantity > 0 && iter.hasNext()){
            GoodType type = iter.next();

            long possible =  getComponentsStream().flatMap(
                    comp -> comp.getStoredGoods().stream()
            ).filter(t -> t.equals(type)).count();

            long typeRemoved = Long.min(quantity, possible);
            quantity -= typeRemoved;
            toRemove.put(type, typeRemoved);
        }

        for (GoodType type : toRemove.keySet()){
            for (int i = 0; i < toRemove.get(type); i++){
                Iterator<Component> componentIterator = getCompIterator();
                while (componentIterator.hasNext()){
                    Component comp = componentIterator.next();

                    try{
                        comp.removeGood(type);
                        break;
                    }
                    catch (IncorrectShipBoardException e){
                        continue;
                    }
                }
            }
        }

        if (quantity > 0){
            try {
                removeEnergy(Integer.min(quantity, getQuantBatteries()));
            } catch (IncorrectShipBoardException e) {
                throw new RuntimeException(e);
            }
        }
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

    public float getMaximumFirePower(){
        return (float) getComponentsStream()
                .mapToDouble(Component::getFirePower)
                .sum();
    }

    public void keepPart(int row, int col){
        List<Set<Component>> multiple = Collections.emptyList(); // getMultiplePieces();
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
                    this.removeComponent(new Cordinate(c.getRow(), c.getColumn()));
                }
            }
        }
    }


}


