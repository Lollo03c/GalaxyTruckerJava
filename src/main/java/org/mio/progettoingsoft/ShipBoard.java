package org.mio.progettoingsoft;

import org.mio.progettoingsoft.advCards.CombatLine;
import org.mio.progettoingsoft.components.*;
import org.mio.progettoingsoft.exceptions.*;
import org.mio.progettoingsoft.model.ShipBoardEasy;
import org.mio.progettoingsoft.model.ShipBoardNormal;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.model.events.RemoveEnergyEvent;
import org.mio.progettoingsoft.model.events.RemoveGoodEvent;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ShipBoard {
    private final Optional<Component>[][] shipComponents;
    private final Optional<Integer> [][] rotationMatrix;

    private List<Optional<Integer>> bookedComponents;

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
    private HousingColor housingColor;
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

    public void drawShipboard(){}

    public HousingColor getHousingColor(){
        return housingColor;
    }

    protected ShipBoard(HousingColor color, FlyBoard flyBoard) {
        this.flyBoard = flyBoard;
        housingColor = color;
        rows = 5;
        columns = 7;

        offsetRow = 5;
        offsetCol = 4;

        shipComponents = new Optional[rows][columns];
        rotationMatrix = new Optional[rows][columns];
        bookedComponents = new ArrayList<>();
        bookedComponents.add(Optional.empty());
        bookedComponents.add(Optional.empty());

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++) {
                shipComponents[i][j] = Optional.empty();
                rotationMatrix[i][j] = Optional.empty();
            }


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

    public void addBookedComponent(Integer bookedComponent) throws IncorrectShipBoardException {
        long counted = bookedComponents.stream()
                .filter(Optional::isPresent).count();

        if (counted == 0){
            shipComponents[0][5] = Optional.of(flyBoard.getComponentById(bookedComponent));
            bookedComponents.set(0, Optional.of(bookedComponent));
        }
        else if (counted == 1) {
            shipComponents[0][6] = Optional.of(flyBoard.getComponentById(bookedComponent));
            bookedComponents.set(1, Optional.of(bookedComponent));
        }
        else
            throw new IncorrectShipBoardException("");
    }

    public void swapBookComponent(int bookedComponent, int position){
        shipComponents[0][5 + position] = Optional.of(flyBoard.getComponentById(bookedComponent));
        bookedComponents.set(position, Optional.of(bookedComponent));
    }


    /**
     *
     * @return the list of the id of the booked Component
     */
    public List<Optional<Integer>> getBookedComponents() {
        return bookedComponents;
    }

    public void removedBookedComponent(int position){
        shipComponents[0][5 + position] = Optional.empty();
        bookedComponents.set(position, Optional.empty());
    }

    // activated firepower: tmp property to store the firepower after activating double drills.
    // How to use: askDoubleDrill, set activFP = base + activated, do whatever you need, then set activated = base
    public double getActivatedFirePower() {
        return activatedFirePower;
    }

    public void setActivatedFirePower(double activatedFirePower) {
        this.activatedFirePower = activatedFirePower;
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
        int enginePower = getCompStream().mapToInt(comp -> comp.getEnginePower(false)).sum();;
        if (enginePower > 0){
            boolean alienPresent = getCompStream().anyMatch(comp -> comp.getGuests().contains(GuestType.BROWN));
            if (alienPresent)
                enginePower += 2;
        }

        return enginePower;

    }

    /**
     *
     * @return the total fire power when all {@link DoubleDrill} are not activated
     */
    public double getBaseFirePower() {
        double firePower = getCompStream().mapToDouble (comp -> comp.getFirePower(false)).sum();
        if (firePower > 0){
            boolean alienPresent = getCompStream().anyMatch(comp -> comp.getGuests().contains(GuestType.PURPLE));
            if (alienPresent)
                firePower += 2;
        }
        return firePower;
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

    /// add the [Component] with given id, to the given rotation, after rotates it
    ///
    /// @param componentId the id of the [Component] to insert
    /// @param cordinate the position where to insert id
    /// @param angle the angle to rotate
    /// @throws IncorrectShipBoardException if the cordinate is invalid
    public void addComponentToPosition(int componentId, Cordinate cordinate, int angle) throws IncorrectShipBoardException {
        if (bannedCoordinates.contains(cordinate))
            throw new IncorrectShipBoardException("Cannot insert in this cordinate");

        if (getOptComponentByCord(cordinate).isPresent())
            throw new IncorrectShipBoardException("Cordinate already full");

        Component component = flyBoard.getComponentById(componentId);
        component.rotate(angle);
        /*
        boolean validPosition = cordinate.equals(new Cordinate(2, 3)) ||
                !getAdjacent(cordinate).values().isEmpty();

        if (!validPosition)
            throw new IncorrectShipBoardException("Not adjacent components");*/

        shipComponents[cordinate.getRow()][cordinate.getColumn()] = Optional.of(component);
        rotationMatrix[cordinate.getRow()][cordinate.getColumn()] = Optional.of(angle);
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

        shipComponents[cordinate.getRow()][cordinate.getColumn()] = Optional.empty();

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

            Event event = new RemoveEnergyEvent(null, id);
            flyBoard.getSupport().firePropertyChange("removeBattery", null, event);

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


    public Optional<Component>[][] getComponentsMatrix() {
        return shipComponents;
    }

    /**
     * @return a NEW matrix of optionals: empty if the position doesn't contain a component, the id of the component if present
     */
    public Optional<Integer>[][] getComponentIdsMatrix() {
        Optional<Integer>[][] matrix = new Optional[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if(shipComponents[i][j].isPresent()){
                    matrix[i][j] = Optional.of(shipComponents[i][j].get().getId());
                }else{
                    matrix[i][j] = Optional.empty();
                }
            }
        }
        return matrix;
    }


    /**
     * used only for the GUI rendering
     * @return a NEW matrix of optional: empty if the position doesn't contain a component, the rotation of the component (related to the image) if present
     */
    public Optional<Integer>[][] getComponentRotationsMatrix(){
        Optional<Integer>[][] matrix = new Optional[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if(rotationMatrix[i][j].isPresent()){
                    matrix[i][j] = Optional.of(rotationMatrix[i][j].get());
                }else{
                    matrix[i][j] = Optional.empty();
                }
            }
        }
        return matrix;
    }


    /**
     *
     * @return the total number of energy left
     */
    public int getQuantBatteries() {
        return getCompStream().mapToInt(Component::getEnergyQuantity).sum();
    }

    public List<Integer> getDoubleEngine() {
        return getCompStream()
                .filter(comp -> comp.getEnginePower(true) == 2)
                .map(comp -> comp.getId())
                .toList();
    }

    /**
     *
     * @return the list of {@link Cordinate} of the {@link Engine} and {@link DoubleEngine} placed incorrectly (not pointaing BACK-WARDS)
     */
    public List<Cordinate> getIncorrectEngines() {
        List<Cordinate> result = new ArrayList<>();

        Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();
        while (cordinateIterator.hasNext()){
            Cordinate cord = cordinateIterator.next();

            //empty tile
            if (getOptComponentByCord(cord).isEmpty())
                continue;

            //not an engine
            if (getOptComponentByCord(cord).get().getEnginePower(true) == 0)
                continue;


            if (! getOptComponentByCord(cord).get().getDirection().equals(Direction.BACK))
                result.add(cord);
            else{
                try{
                    Cordinate toCheck = new Cordinate(cord.getRow() + 1, cord.getColumn());

                    if (getOptComponentByCord(toCheck).isPresent())
                        result.add(cord);
                } catch (InvalidCordinate e) {

                }
            }
        }
        return result;
    }

    /**
     *
     * @return the list {@link Cordinate} of the {@link Drill} and {@link DoubleDrill} place incorrectly -> if it points in a not empty tile
     */
    public List<Cordinate> getIncorrectDrills() {
        List<Cordinate> incorrect = new ArrayList<>();

        Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();
        while (cordinateIterator.hasNext()){
            Cordinate cord = cordinateIterator.next();

            Optional<Component> optComp = getOptComponentByCord(cord);
            if (optComp.isEmpty() || optComp.get().getFirePower(true) == 0)
                continue;

            Direction dir = optComp.get().getDirection();

            try {
                Cordinate toCheck =new Cordinate(cord.getRow() + dir.offsetRow(), cord.getColumn() + dir.offsetCol());
                if (! getOptComponentByCord(toCheck).isEmpty())
                    incorrect.add(cord);

            }
            catch (InvalidCordinate e){

            }
        }

        return incorrect;
    }

    /**
     *
     * @return the set of all the {@link Cordinate} relative to the {@link Component} which are not correctly configured in the shipboard
     */
    private Set<Cordinate> getIncorrectComponents() {
        Set<Cordinate> incorrect = new HashSet<>();

        incorrect.addAll(getIncorrectDrills());
        incorrect.addAll(getIncorrectEngines());

        Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();
        while (cordinateIterator.hasNext()){
            Cordinate cordinate = cordinateIterator.next();

            //empty tile
            if (getOptComponentByCord(cordinate).isEmpty())
                continue;

            //incorrect by connectors
            Component comp = getOptComponentByCord(cordinate).get();
            Map<Direction, Component> adjacent = getAdjacent(cordinate);
            for (Direction dir : adjacent.keySet()){
                Component other = adjacent.get(dir);
                if (! comp.getConnector(dir).isCompatible(other.getConnector(dir.getOpposite()))){
                    incorrect.add(cordinate);
                }
            }

        }
        return incorrect;
    }

    /**
     * metod called once the {@link ShipBoard} has being built to validate it
     * @throws IncorrectShipBoardException if the {@link ShipBoard} is not valid
     */
    public void validateShip() throws IncorrectShipBoardException{
        Set<Cordinate> incorrect = getIncorrectComponents();
        if (!getIncorrectComponents().isEmpty())
            throw new IncorrectShipBoardException("shipboard is not valid");

        //set the allowedGuest to all the housing, based on the neaby AlienHousing
        Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();
        while (cordinateIterator.hasNext()){
            Cordinate cordinate = cordinateIterator.next();

            if (getOptComponentByCord(cordinate).isEmpty())
                continue;

            Component component = getOptComponentByCord(cordinate).get();
            if (component.getType().equals(ComponentType.HOUSING)){
                Map<Direction, Component> adjacents = getAdjacent(cordinate);

                for (Component comp : adjacents.values()){
                    try {
                        component.addAllowedGuest(comp.getColorAlien());
                    } catch (IncorrectShipBoardException e) {

                    }
                }
            }
        }
    }

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

    /**
     *
     * @return the number of exposed connectors of the shipboard
     */
    public int getExposedConnectors() {
        int numExposedConnectors = 0;

        Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();
        while (cordinateIterator.hasNext()){
            Cordinate cordinate = cordinateIterator.next();

            if (getOptComponentByCord(cordinate).isEmpty())
                continue;

            Component comp = getOptComponentByCord(cordinate).get();
            Map<Direction, Component> adjacents = getAdjacent(cordinate);

            for (Direction dir : Direction.values()){
                if (!comp.getConnector(dir).equals(Connector.FLAT) && !adjacents.containsKey(dir))
                    numExposedConnectors++;
            }
        }

        return numExposedConnectors;
    }

    /**
     *
     * @return the number of guests hosted in the shipBoard
     */
    public int getQuantityGuests() {
        return getCompStream().
                mapToInt(comp -> comp.getGuests().size())
                .sum();
    }

    /**
     *
     * @param quantity int : quantity of goods / batteries to stole
     */
    public Map<Integer, List<GoodType>> stoleGood(int quantity) {
        Map<Integer, List<GoodType>> result = new HashMap<>();

        Iterator<GoodType> typeIterator = GoodType.sortedList.iterator();


        while (quantity > 0 && typeIterator.hasNext()){
            GoodType type = typeIterator.next();
            Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();

            while (quantity > 0 && cordinateIterator.hasNext()){
                Cordinate cord = cordinateIterator.next();
                if (getOptComponentByCord(cord).isEmpty())
                    continue;

                Component comp = getOptComponentByCord(cord).get();
                if (comp.getStoredGoods().contains(type)){
                    int contained = (int) comp.getStoredGoods().stream().filter(t -> t.equals(type)).count();
                    quantity -= Integer.min(quantity, contained);

                    if (result.containsKey(comp.getId()))
                        result.get(comp.getId()).add(type);
                    else{
                        result.put(comp.getId(), new ArrayList<>());
                        result.get(comp.getId()).add(type);
                    }
                }
            }
        }

        for (Integer idComp : result.keySet()){
            for (GoodType type : result.get(idComp)){
                flyBoard.getComponentById(idComp).removeGood(type);

                Event event = new RemoveGoodEvent("", idComp, type);
                flyBoard.getSupport().firePropertyChange("removeGood", null, event);
            }
        }

        if (quantity > 0){
            try {
                removeEnergy(Integer.min(quantity, getQuantBatteries()));
            } catch (IncorrectShipBoardException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
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

//    public String toString() {
//        String out = "";
//        for(Component c : getCompStream().toList()){
//            out += c.toString() + "\n";
//        }
//        return out;
//    }


    public void keepPart(int row, int col){
//        List<Set<Component>> multiple = Collections.emptyList(); // getMultiplePieces();
//        int partToKeep = -1;
//        for(int i = 0; i < multiple.size(); i++){
//            Set<Component> set = multiple.get(i);
//            for(Component c : set){
//                if(c.getRow() == row && c.getColumn() == col){
//                    partToKeep = i;
//                    break;
//                }
//            }
//            if(partToKeep != -1){
//                break;
//            }
//        }
//        if(partToKeep == -1){
//            throw new BadParameterException("No components at row " + row + " and column " + col);
//        }
//        for(int i = 0; i < multiple.size(); i++){
//            if(i != partToKeep){
//                Set<Component> set = multiple.get(i);
//                for(Component c : set){
//                    this.removeComponent(new Cordinate(c.getRow(), c.getColumn()));
//                }
//            }
//        }
    }

    /**
     *
     * @return the {@link List} of {@link Optional} of {@link Component} of the components in the {@link ShipBoard}
     */
    public List<Optional<Component>> getComponents(){
        return Stream.of(shipComponents)
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());
    }

    /**
     *
     * @param type : the {@link GoodType} to search for availability
     * @return a {@link List} of {@link Cordinate} for the valid spots of the {@link Depot}
     */
    public List<Cordinate> getAvailableDepots(GoodType type){
        List<Cordinate> cordinates = new ArrayList<>();
        Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();

        while (cordinateIterator.hasNext()){
            Cordinate cord = cordinateIterator.next();

            if (shipComponents[cord.getRow()][cord.getColumn()].isEmpty())
                continue;

            Component comp = shipComponents[cord.getRow()][cord.getColumn()].get();
            if (comp.canContainsGood(type)){
                cordinates.add(cord);
            }
        }

        return cordinates;
    }

    /**
     *
     * @return a {@link List} of {@link Cordinate} for the valid spots of {@link DoubleDrill}
     */
    public List<Cordinate> getDoubleDrills(){
        List<Cordinate> drillCords = new ArrayList<>();
        Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();
        while (cordinateIterator.hasNext()){
            Cordinate cord = cordinateIterator.next();
            if (this.getOptComponentByCord(cord).isEmpty())
                continue;

            Component comp = this.getOptComponentByCord(cord).get();
            if (comp.getType().equals(ComponentType.DOUBLE_DRILL))
                drillCords.add(cord);
        }

        return drillCords;
    }

    /**
     * check whether exists a {@link Shield} which can cover the given {@link Direction}
     * @param direction : the {@link Direction} to search
     * @return whether a {@link Shield} exists
     */
    public boolean coveredByShield(Direction direction){
        Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();
        while (cordinateIterator.hasNext()){
            Cordinate cord = cordinateIterator.next();

            if (getOptComponentByCord(cord).isEmpty())
                continue;
            Component comp = getOptComponentByCord(cord).get();
            if (comp.getShieldDirections().contains(direction))
                return true;
        }

        return false;
    }

    /**
     *
     * @return a {@link List} of {@link Cordinate} for the valid spots of {@link DoubleDrill}
     */
    public List<Cordinate> getDrills(Direction direction){
        List<Cordinate> result = new ArrayList<>();
        Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();

        while (cordinateIterator.hasNext()){
            Cordinate cord = cordinateIterator.next();

            if (getOptComponentByCord(cord).isEmpty())
                continue;
            Component comp = getOptComponentByCord(cord).get();

            if (comp.getFirePower(true) > 0 && comp.getDirection() != null && comp.getDirection().equals(direction))
                result.add(cord);
        }
        return result;
    }

    public List<Cordinate> possibleDrills(Direction direction, int number){
        List<Cordinate> result = new ArrayList<>();
        List<Cordinate> drills = getDrills(direction);

        switch (direction){
            case FRONT -> {
                for (Cordinate cord : drills){
                    if (cord.getColumn() == number)
                        result.add(cord);
                }
            }

            case BACK -> {
                for (Cordinate cord : drills){
                    if (Math.abs(cord.getColumn() - number) <= 1)
                        result.add(cord);
                }
            }

            case LEFT, RIGHT -> {
                for (Cordinate cord : drills){
                    if (Math.abs(cord.getRow() - number) <= 1)
                        result.add(cord);
                }
            }
        }

        return result;
    }


}


