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
import org.mio.progettoingsoft.utils.Logger;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ShipBoard {
    private final Optional<Component>[][] shipComponents;
    private final Optional<Integer>[][] rotationMatrix;

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
     * @param mode     the {@link GameMode} of the game
     * @param color    the {@link Housing} of the shipboard
     * @param flyBoard of {@link FlyBoard} used to get the {@link Component} and {@link AdventureCard} lists
     * @return
     */
    public static ShipBoard createShipBoard(GameMode mode, HousingColor color, FlyBoard flyBoard) {
        ShipBoard shipBoard = null;

        switch (mode) {
            case EASY -> shipBoard = new ShipBoardEasy(color, flyBoard);
            case NORMAL -> shipBoard = new ShipBoardNormal(color, flyBoard);
        }

        return shipBoard;
    }

    public void drawShipboard() {
    }

    public HousingColor getHousingColor() {
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
     * @return the list of banneed {@link Cordinate} based on the {@link GameMode} of the game
     */
    protected abstract List<Cordinate> getBannedCoordinates();

    public void addBookedComponent(Integer bookedComponent) throws IncorrectShipBoardException {
        long counted = bookedComponents.stream()
                .filter(Optional::isPresent).count();

        if (counted == 0) {
            shipComponents[0][5] = Optional.of(flyBoard.getComponentById(bookedComponent));
            bookedComponents.set(0, Optional.of(bookedComponent));
        } else if (counted == 1) {
            shipComponents[0][6] = Optional.of(flyBoard.getComponentById(bookedComponent));
            bookedComponents.set(1, Optional.of(bookedComponent));
        } else
            throw new IncorrectShipBoardException("");
    }

    public void swapBookComponent(int bookedComponent, int position) {
        shipComponents[0][5 + position] = Optional.of(flyBoard.getComponentById(bookedComponent));
        bookedComponents.set(position, Optional.of(bookedComponent));
    }


    /**
     * @return the list of the id of the booked Component
     */
    public List<Optional<Integer>> getBookedComponents() {
        return bookedComponents;
    }

    public void removedBookedComponent(int position) {
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
     * @return the total engine power when all {@link DoubleEngine} are not activated
     */
    public int getBaseEnginePower() {
        int enginePower = getCompStream().mapToInt(comp -> comp.getEnginePower(false)).sum();
        if (enginePower > 0) {
            boolean alienPresent = getCompStream().anyMatch(comp -> comp.getGuests().contains(GuestType.BROWN));
            if (alienPresent)
                enginePower += 2;
        }

        return enginePower;

    }

    /**
     * @return the total fire power when all {@link DoubleDrill} are not activated
     */
    public double getBaseFirePower() {
        double firePower = getCompStream().mapToDouble(comp -> comp.getFirePower(false)).sum();
        if (firePower > 0) {
            boolean alienPresent = getCompStream().anyMatch(comp -> comp.getGuests().contains(GuestType.PURPLE));
            if (alienPresent)
                firePower += 2;
        }
        return firePower;
    }

    /**
     * @param type the {@link GoodType} to search for
     * @return the number of goods of the given {@link GoodType} contained in the shipboard
     */
    public int getStoredQuantity(GoodType type) {
        return (int) getCompStream().flatMap(comp -> comp.getStoredGoods().stream())
                .filter(t -> t.equals(type))
                .count();
    }

    /**
     * Returns a map of all components adjacent to the given coordinate.
     *
     * @param cord the coordinate for which adjacent components should be found
     * @return a map where the key is the {@link Direction} of the adjacent component relative
     * to the given coordinate, and the value is the corresponding {@link Component}
     */
    public Map<Direction, Component> getAdjacent(Cordinate cord) {
        Map<Direction, Component> adjacent = new HashMap<>();

        for (Direction dir : Direction.values()) {
            try {
                Cordinate cordinate = new Cordinate(cord.getRow() + dir.offsetRow(), cord.getColumn() + dir.offsetCol());
                Optional<Component> optComp = getOptComponentByCord(cordinate);
                optComp.ifPresent(component -> adjacent.put(dir, component));
            } catch (InvalidCordinate e) {
            }
        }
        return adjacent;
    }

    /// add the [Component] with given id, to the given rotation, after rotates it
    ///
    /// @param componentId the id of the [Component] to insert
    /// @param cordinate   the position where to insert id
    /// @param angle       the angle to rotate
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
        component.setRow(cordinate.getRow());
        component.setColumn(cordinate.getColumn());

        shipComponents[cordinate.getRow()][cordinate.getColumn()] = Optional.of(component);
        rotationMatrix[cordinate.getRow()][cordinate.getColumn()] = Optional.of(angle);
    }


    /**
     * return the {@link Component} if present, Optional.of otherwise
     *
     * @param cordinate the {@link Cordinate} for the search
     * @return
     */
    public Optional<Component> getOptComponentByCord(Cordinate cordinate) {
        return shipComponents[cordinate.getRow()][cordinate.getColumn()];
    }


    /**
     * @param cordinate the {@link Cordinate} of the tile to empty
     * @throws IncorrectShipBoardException if the tile is already empty
     */
    public void removeComponent(Cordinate cordinate) throws IncorrectShipBoardException {
        if (getOptComponentByCord(cordinate).isEmpty())
            throw new IncorrectShipBoardException("tile is empty. nothing to remove");

        shipComponents[cordinate.getRow()][cordinate.getColumn()] = Optional.empty();
        discaredComponents++;

    }


    /**
     * remove the given quantity of energy from {@link EnergyDepot} if possible, throws {@link IncorrectShipBoardException} otherwise
     *
     * @param quantiy the quanity to remove
     * @return the list of id {@link Component} from which the energy have been taken
     * @throws IncorrectShipBoardException if not enough energy to remove
     */
    public List<Integer> removeEnergy(int quantiy) throws IncorrectShipBoardException {
        List<Integer> idComps = new ArrayList<>();

        Iterator<Component> iterator = getCompIterator();
        while (iterator.hasNext() && quantiy > 0) {
            Component comp = iterator.next();

            if (comp.getEnergyQuantity() > 0) {
                int toRemove = Integer.min(quantiy, comp.getEnergyQuantity());
                quantiy -= toRemove;

                for (int i = 0; i < toRemove; i++) {
                    idComps.add(comp.getId());
                }
            }
        }

        if (quantiy > 0)
            throw new IncorrectShipBoardException("not enought energy to remove");

        for (int id : idComps) {
            flyBoard.getComponentById(id).removeOneEnergy();

            Event event = new RemoveEnergyEvent(null, id);
            flyBoard.getSupport().firePropertyChange("removeBattery", null, event);

        }

        return idComps;
    }


    /**
     * @return the iterator which enable to iterator over all the components in the shipboard
     */
    private Iterator<Component> getCompIterator() {
        return Stream.of(shipComponents).flatMap(Stream::of).filter(Optional::isPresent).map(Optional::get).iterator();
    }

    /**
     * @return the stream of the components in the shipboard
     */
    private Stream<Component> getCompStream() {
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
                if (shipComponents[i][j].isPresent()) {
                    matrix[i][j] = Optional.of(shipComponents[i][j].get().getId());
                } else {
                    matrix[i][j] = Optional.empty();
                }
            }
        }
        return matrix;
    }


    /**
     * used only for the GUI rendering
     *
     * @return a NEW matrix of optional: empty if the position doesn't contain a component, the rotation of the component (related to the image) if present
     */
    public Optional<Integer>[][] getComponentRotationsMatrix() {
        Optional<Integer>[][] matrix = new Optional[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (rotationMatrix[i][j].isPresent()) {
                    matrix[i][j] = Optional.of(rotationMatrix[i][j].get());
                } else {
                    matrix[i][j] = Optional.empty();
                }
            }
        }
        return matrix;
    }


    /**
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
     * Returns a list of coordinates of incorrect engines.
     * <p>
     * An engine is considered incorrect if:
     * <ul>
     *   <li>It is not facing {@link Direction#BACK}</li>
     *   <li>There is at least one component placed below it</li>
     * </ul>
     * </p>
     *
     * @return a list of {@link Cordinate} objects representing the positions of incorrect engines.
     */
    public List<Cordinate> getIncorrectEngines() {
        List<Cordinate> incorrect = new ArrayList<>();

        // Itereo sui components
        Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();
        while (cordinateIterator.hasNext()) {
            Cordinate cord = cordinateIterator.next();
            Optional<Component> optComponent = getOptComponentByCord(cord);

            // empty tile or not engine
            if (optComponent.isEmpty() || optComponent.get().getEnginePower(true) == 0)
                continue;

            // if engine is not pointing back is incorrect
            if (!optComponent.get().getDirection().equals(Direction.BACK)) {
                incorrect.add(cord);
                continue;
            }

            try {
                // if engine has a component below is incorrect
                if (getOptComponentByCord(new Cordinate(cord.getRow() + 1, cord.getColumn())).isPresent())
                    incorrect.add(cord);
            } catch (InvalidCordinate e) {
                // Se esce dalla griglia senza trovare nulla, il cannone è corretto
            }
        }
        return incorrect;
    }

    /**
     * Returns a list of coordinates of incorrect drills.
     * <p>
     * A drill is considered incorrect if there is at least one other component
     * in the direction it is facing. If there are no components in that direction
     * up to the edge of the grid, the drill is considered correct.
     * </p>
     *
     * @return a list of {@link Cordinate} objects representing the positions of incorrect drills.
     */
    public List<Cordinate> getIncorrectDrills() {
        List<Cordinate> incorrect = new ArrayList<>();

        // Itero sui components
        Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();
        while (cordinateIterator.hasNext()) {
            Cordinate cord = cordinateIterator.next();

            Optional<Component> optComp = getOptComponentByCord(cord);
            if (optComp.isEmpty() || optComp.get().getFirePower(true) == 0)
                continue;

            Direction dir = optComp.get().getDirection();
            int row = cord.getRow() + dir.offsetRow();
            int col = cord.getColumn() + dir.offsetCol();

            try {
                if (getOptComponentByCord(new Cordinate(row, col)).isPresent())
                    incorrect.add(cord);
            } catch (InvalidCordinate e) {
                // Se esce dalla griglia senza trovare nulla, il cannone è corretto
            }
        }

        return incorrect;
    }

    /**
     * Returns the set of coordinates of components that are incorrectly connected on the shipboard.
     *
     * @return a {@link Set} of {@link Cordinate} objects representing the positions of components
     * that are not correctly connected based on their connectors.
     */
    public List<Cordinate> getIncorrectConnectedComponents() {
        List<Cordinate> incorrect = new ArrayList<>();

        Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();
        while (cordinateIterator.hasNext()) {
            Cordinate cord = cordinateIterator.next();
            Optional<Component> optComp = getOptComponentByCord(cord);

            //empty tile
            if (optComp.isEmpty())
                continue;

            //incorrect by connectors
            Component comp = optComp.get();
            Map<Direction, Component> adjacent = getAdjacent(cord);
            for (Direction dir : adjacent.keySet()) {
                Component other = adjacent.get(dir);

                if (!comp.getConnector(dir).isCompatible(other.getConnector(dir.getOpposite()))) {
                    incorrect.add(cord);
                    Logger.debug("Incorrect connected: " + cord + " id " + getOptComponentByCord(cord).get().getId());
                    break;

                }
            }
        }
        return incorrect;
    }

    /**
     * Returns a list of groups of {@link Component}s that are not connected to each other
     * through compatible connectors. Each group represents a disconnected "piece" of the ship.
     * <p>
     * If the ship is fully connected, this method returns a list with a single set.
     * Otherwise, it returns multiple sets representing disconnected parts.
     * </p>
     *
     * @return a list of sets of components that are not mutually connected.
     */
    public List<Set<Component>> getMultiplePieces() {


        List<Set<Component>> multiple = new ArrayList<>();

        List<Component> allComponents = this.getComponents().stream()
                .flatMap(Optional::stream)
                .toList();

        Set<Component> visited = new HashSet<>();

        for (Component c : allComponents) {
            if (visited.contains(c)) continue;

            Set<Component> part = new HashSet<>();
            Queue<Component> queue = new LinkedList<>();
            queue.add(c);
            part.add(c);
            visited.add(c);

            while (!queue.isEmpty()) {
                Component comp = queue.poll();
                Cordinate coord = new Cordinate(comp.getRow(), comp.getColumn());
                Map<Direction, Component> adj = getAdjacent(coord);

                for (Map.Entry<Direction, Component> entry : adj.entrySet()) {
                    Direction dir = entry.getKey();
                    Component neighbor = entry.getValue();

                    if (!visited.contains(neighbor) &&
                            comp.getConnector(dir).isCompatible(neighbor.getConnector(dir.getOpposite()))) {

                        queue.add(neighbor);
                        part.add(neighbor);
                        visited.add(neighbor);
                    }
                }
            }

            multiple.add(part);
        }

        return multiple;
    }

    public List<Cordinate> getIncorrectComponents() {
        // delete all booked components
        this.shipComponents[0][5] = Optional.empty();
        this.shipComponents[0][6] = Optional.empty();
        List<Cordinate> incorrectComponents = getIncorrectConnectedComponents();
        incorrectComponents.addAll(getIncorrectEngines());
        incorrectComponents.addAll(getIncorrectDrills());

        return new ArrayList<>(incorrectComponents.stream().distinct().toList());
    }

    public boolean isShipValid() {
        if (!getIncorrectComponents().isEmpty())
            return false;

        if (getMultiplePieces().size() > 1)
            return false;

        return true;
    }

    public void addGuestToShip() {
        //set the allowedGuest to all the housing, based on the neaby AlienHousing
        Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();
        while (cordinateIterator.hasNext()) {
            Cordinate cordinate = cordinateIterator.next();

            if (getOptComponentByCord(cordinate).isEmpty())
                continue;

            Component component = getOptComponentByCord(cordinate).get();
            if (component.getType().equals(ComponentType.HOUSING)) {
                Map<Direction, Component> adjacents = getAdjacent(cordinate);

                for (Direction dir : adjacents.keySet()) {
                    Component other = adjacents.get(dir);
                    if (component.getConnector(dir).isConnected(other.getConnector(dir.getOpposite()))) {
                        try {
                            component.addAllowedGuest(other.getColorAlien());
                        } catch (IncorrectShipBoardException e) {

                        }
                    }
                }
            }
        }
    }

    /**
     * @return the number of exposed connectors of the shipboard
     */
    public int getExposedConnectors() {
        int numExposedConnectors = 0;

        Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();
        while (cordinateIterator.hasNext()) {
            Cordinate cordinate = cordinateIterator.next();

            if (getOptComponentByCord(cordinate).isEmpty())
                continue;

            Component comp = getOptComponentByCord(cordinate).get();
            Map<Direction, Component> adjacents = getAdjacent(cordinate);

            for (Direction dir : Direction.values()) {
                if (!comp.getConnector(dir).equals(Connector.FLAT) && !adjacents.containsKey(dir))
                    numExposedConnectors++;
            }
        }

        return numExposedConnectors;
    }

    /**
     * @return the number of guests hosted in the shipBoard
     */
    public int getQuantityGuests() {
        return getCompStream().
                mapToInt(comp -> comp.getGuests().size())
                .sum();
    }

    /**
     * @param quantity int : quantity of goods / batteries to stole
     */
    public Map<Integer, List<GoodType>> stoleGood(int quantity) {
        Map<Integer, List<GoodType>> result = new HashMap<>();

        Iterator<GoodType> typeIterator = GoodType.sortedList.iterator();


        while (quantity > 0 && typeIterator.hasNext()) {
            GoodType type = typeIterator.next();
            Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();

            while (quantity > 0 && cordinateIterator.hasNext()) {
                Cordinate cord = cordinateIterator.next();
                if (getOptComponentByCord(cord).isEmpty())
                    continue;

                Component comp = getOptComponentByCord(cord).get();
                if (comp.getStoredGoods().contains(type)) {
                    int contained = (int) comp.getStoredGoods().stream().filter(t -> t.equals(type)).count();
                    quantity -= Integer.min(quantity, contained);

                    if (result.containsKey(comp.getId()))
                        result.get(comp.getId()).add(type);
                    else {
                        result.put(comp.getId(), new ArrayList<>());
                        result.get(comp.getId()).add(type);
                    }
                }
            }
        }

        for (Integer idComp : result.keySet()) {
            for (GoodType type : result.get(idComp)) {
                flyBoard.getComponentById(idComp).removeGood(type);

                Event event = new RemoveGoodEvent("", idComp, type);
                flyBoard.getSupport().firePropertyChange("removeGood", null, event);
            }
        }

        if (quantity > 0) {
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


    public void keepPart(int row, int col) {
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
     * @return the {@link List} of {@link Optional} of {@link Component} of the components in the {@link ShipBoard}
     */
    public List<Optional<Component>> getComponents() {
        return Stream.of(shipComponents)
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());
    }

    /**
     * @param type : the {@link GoodType} to search for availability
     * @return a {@link List} of {@link Cordinate} for the valid spots of the {@link Depot}
     */
    public List<Cordinate> getAvailableDepots(GoodType type) {
        List<Cordinate> cordinates = new ArrayList<>();
        Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();

        while (cordinateIterator.hasNext()) {
            Cordinate cord = cordinateIterator.next();

            if (shipComponents[cord.getRow()][cord.getColumn()].isEmpty())
                continue;

            Component comp = shipComponents[cord.getRow()][cord.getColumn()].get();
            if (comp.canContainsGood(type)) {
                cordinates.add(cord);
            }
        }

        return cordinates;
    }

    /**
     * @return a {@link List} of {@link Cordinate} for the valid spots of {@link DoubleDrill}
     */
    public List<Cordinate> getDoubleDrills() {
        List<Cordinate> drillCords = new ArrayList<>();
        Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();
        while (cordinateIterator.hasNext()) {
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
     *
     * @param direction : the {@link Direction} to search
     * @return whether a {@link Shield} exists
     */
    public boolean coveredByShield(Direction direction) {
        Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();
        while (cordinateIterator.hasNext()) {
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
     * @return a {@link List} of {@link Cordinate} for the valid spots of {@link DoubleDrill}
     */
    public List<Cordinate> getDrills(Direction direction) {
        List<Cordinate> result = new ArrayList<>();
        Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();

        while (cordinateIterator.hasNext()) {
            Cordinate cord = cordinateIterator.next();

            if (getOptComponentByCord(cord).isEmpty())
                continue;
            Component comp = getOptComponentByCord(cord).get();

            if (comp.getFirePower(true) > 0 && comp.getDirection() != null && comp.getDirection().equals(direction))
                result.add(cord);
        }
        return result;
    }

    public List<Cordinate> possibleDrills(Direction direction, int number) {
        List<Cordinate> result = new ArrayList<>();
        List<Cordinate> drills = getDrills(direction);

        switch (direction) {
            case FRONT -> {
                for (Cordinate cord : drills) {
                    if (cord.getColumn() == number)
                        result.add(cord);
                }
            }

            case BACK -> {
                for (Cordinate cord : drills) {
                    if (Math.abs(cord.getColumn() - number) <= 1)
                        result.add(cord);
                }
            }

            case LEFT, RIGHT -> {
                for (Cordinate cord : drills) {
                    if (Math.abs(cord.getRow() - number) <= 1)
                        result.add(cord);
                }
            }
        }

        return result;
    }

    public int getNumberHumans() {
        long sum = 0;
        Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();
        while (cordinateIterator.hasNext()) {
            Cordinate cord = cordinateIterator.next();

            if (getOptComponentByCord(cord).isEmpty())
                continue;

            Component comp = getOptComponentByCord(cord).get();
            sum += comp.getGuests().stream().filter(guest -> guest.equals(GuestType.HUMAN)).count();
        }

        return (int) sum;
    }

    public List<Cordinate> getAvailableHousing(GuestType type) {
        Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();
        List<Cordinate> availableCord = new ArrayList<>();

        while (cordinateIterator.hasNext()) {
            Cordinate cord = cordinateIterator.next();

            if (getOptComponentByCord(cord).isEmpty())
                continue;

            Component comp = getOptComponentByCord(cord).get();
            if (comp.canAddGuest(type))
                availableCord.add(cord);
        }

        return availableCord;
    }

}


