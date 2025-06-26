package org.mio.progettoingsoft;

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

/**
 * An abstract representation of a player's spaceship board in the Galaxy Trucker game.
 * This class provides the core logic for managing ship components, their placement,
 * rotation, connections, and interactions. It serves as a base for concrete
 * implementations specific to different game modes (e.g., Easy, Normal).
 */
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
     * Static factory method used to create an instance of {@link ShipBoard} based on the specified {@link GameMode}.
     *
     * @param mode The {@link GameMode} of the game (e.g., EASY, NORMAL).
     * @param color The {@link HousingColor} of the player's starting cabin.
     * @param flyBoard The {@link FlyBoard} instance, used to retrieve component definitions and interact with game events.
     * @return A concrete {@link ShipBoard} instance (e.g., {@link ShipBoardEasy}, {@link ShipBoardNormal}).
     */
    public static ShipBoard createShipBoard(GameMode mode, HousingColor color, FlyBoard flyBoard) {
        ShipBoard shipBoard = null;

        switch (mode) {
            case EASY -> shipBoard = new ShipBoardEasy(color, flyBoard);
            case NORMAL -> shipBoard = new ShipBoardNormal(color, flyBoard);
        }

        return shipBoard;
    }

    /**
     * Placeholder method for drawing the shipboard, intended for GUI implementations.
     * Concrete subclasses or external rendering classes would implement this.
     */
    public void drawShipboard() {
        // Implementation for drawing would go here in a concrete UI context.
    }

    /**
     * Returns the {@link HousingColor} associated with this shipboard.
     *
     * @return The {@link HousingColor} of the ship.
     */
    public HousingColor getHousingColor() {
        return housingColor;
    }

    /**
     * Protected constructor for initializing the common properties of a {@code ShipBoard}.
     * Sets up the grid dimensions, initializes matrices, and places the starting cabin.
     *
     * @param color The {@link HousingColor} of the player's cabin.
     * @param flyBoard The {@link FlyBoard} instance this ship belongs to.
     */
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
     * Returns a list of {@link Cordinate} objects that are banned for component placement
     * on this shipboard. This method must be implemented by concrete subclasses
     * to define mode-specific banned areas.
     *
     * @return A {@link List} of banned {@link Cordinate}s.
     */
    protected abstract List<Cordinate> getBannedCoordinates();

    /**
     * Attempts to add a component to one of the two "booked" slots.
     * A booked component is a component a player has selected but not yet placed on the ship grid.
     *
     * @param bookedComponent The ID of the component to be booked.
     * @throws IncorrectShipBoardException If both booked slots are already occupied.
     */
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

    /**
     * Swaps an existing booked component with a new one at a specified position.
     * This is useful for players deciding to replace a previously booked component.
     *
     * @param bookedComponent The ID of the new component to place in the booked slot.
     * @param position The index of the booked slot to swap (0 or 1).
     */
    public void swapBookComponent(int bookedComponent, int position) {
        shipComponents[0][5 + position] = Optional.of(flyBoard.getComponentById(bookedComponent));
        bookedComponents.set(position, Optional.of(bookedComponent));
    }


    /**
     * Returns a list of {@link Optional} integers, representing the IDs of the booked components.
     * An {@link Optional#empty()} indicates an empty booked slot.
     *
     * @return A {@link List} of {@link Optional<Integer>} for the booked components.
     */
    public List<Optional<Integer>> getBookedComponents() {
        return bookedComponents;
    }

    /**
     * Removes a component from a specified booked slot, making that slot empty.
     *
     * @param position The index of the booked slot to clear (0 or 1).
     */
    public void removedBookedComponent(int position) {
        shipComponents[0][5 + position] = Optional.empty();
        bookedComponents.set(position, Optional.empty());
    }

    /**
     * Returns the currently activated firepower of the ship. This value can be
     * temporarily increased by activating {@link DoubleDrill} components.
     *
     * @return The activated firepower.
     */
    public double getActivatedFirePower() {
        return activatedFirePower;
    }

    /**
     * Sets the activated firepower of the ship. This is typically used to
     * update the firepower value after a {@link DoubleDrill} has been activated or deactivated.
     *
     * @param activatedFirePower The new value for activated firepower.
     */
    public void setActivatedFirePower(double activatedFirePower) {
        this.activatedFirePower = activatedFirePower;
    }

    /**
     * Returns the column offset used for internal grid calculations or visual rendering.
     *
     * @return The column offset.
     */
    public int getOffsetCol() {
        return offsetCol;
    }

    /**
     * Returns the row offset used for internal grid calculations or visual rendering.
     *
     * @return The row offset.
     */
    public int getOffsetRow() {
        return offsetRow;
    }

    /**
     * Calculates and returns the base engine power of the ship. This is the sum of
     * engine power from all installed engine components, without considering
     * the activation of {@link DoubleEngine}s. Brown aliens (Cosmic Tourists)
     * add +2 engine power if any engine is present.
     *
     * @return The total base engine power.
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
     * Calculates and returns the base firepower of the ship. This is the sum of
     * firepower from all installed drill components, without considering
     * the activation of {@link DoubleDrill}s. Purple aliens (Cosmic Pirates)
     * add +2 firepower if any drill is present.
     *
     * @return The total base firepower.
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
     * Returns the number of goods of a specific type currently stored in the ship's cargo holds.
     *
     * @param type The {@link GoodType} to search for.
     * @return The total quantity of the specified good type.
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

    /**
     * Add the [Component] with given id, to the given rotation, after rotates it
     *
     * @param componentId the id of the [Component] to insert
     * @param cordinate the position where to insert id
     * @param angle the angle to rotate
     * @throws IncorrectShipBoardException if the cordinate is invalid
     *
     */
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
     * Returns the total number of components that have been discarded from the shipboard.
     *
     * @return The count of discarded components.
     */
    public int getDiscaredComponents() {
        return discaredComponents;
    }

    /**
     * Remove the given quantity of energy from {@link EnergyDepot} if possible, throws {@link IncorrectShipBoardException} otherwise
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
     * Returns an iterator that allows sequential access to all {@link Component} objects
     * currently placed on the shipboard.
     *
     * @return An {@link Iterator} of {@link Component}s.
     */
    private Iterator<Component> getCompIterator() {
        return Stream.of(shipComponents).flatMap(Stream::of).filter(Optional::isPresent).map(Optional::get).iterator();
    }

    /**
     * Returns a stream of all {@link Component} objects currently placed on the shipboard.
     * This is a convenient way to perform stream operations (filter, map, collect) on components.
     *
     * @return A {@link Stream} of {@link Component}s.
     */
    private Stream<Component> getCompStream() {
        return Stream.of(shipComponents).flatMap(Stream::of).filter(Optional::isPresent).map(Optional::get);
    }

    /**
     * Returns the 2D array representing the ship's grid, containing {@link Optional} of {@link Component}s.
     * This method exposes the internal structure, which should be used carefully to avoid direct modification.
     *
     * @return The 2D array of {@link Optional<Component>}.
     */
    public Optional<Component>[][] getComponentsMatrix() {
        return shipComponents;
    }

    /**
     * Returns a new 2D matrix representing the ship's grid, where each cell contains
     * the ID of the component if present, or {@link Optional#empty()} if the position is empty.
     * This is useful for communicating component layout to clients without sending full component objects.
     *
     * @return A new 2D array of {@link Optional<Integer>} representing component IDs.
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
     * Returns a new 2D matrix representing the rotation of components on the shipboard.
     * Each cell contains the rotation angle (in degrees) if a component is present,
     * or {@link Optional#empty()} if the position is empty. This is primarily used
     * for GUI rendering to correctly display component orientation.
     *
     * @return A new 2D array of {@link Optional<Integer>} representing component rotations.
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
     * Returns the total number of energy units (batteries) currently stored across
     * all {@link EnergyDepot} components on the ship.
     *
     * @return The total quantity of batteries.
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
                            comp.getConnector(dir).isConnected(neighbor.getConnector(dir.getOpposite()))) {

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

    /**
     * Returns a list of coordinates of all ship components that are considered invalid.
     * <p>
     * This includes:
     * <ul>
     *   <li>Components that are not properly connected to the ship</li>
     *   <li>Engines placed in invalid positions</li>
     *   <li>Drills placed in invalid positions</li>
     * </ul>
     * Before performing the checks, it clears two reserved component slots (positions [0][5] and [0][6]).
     *
     * @return a list of distinct coordinates of all invalid components
     */
    public List<Cordinate> getIncorrectComponents() {
        // delete all booked components
        this.shipComponents[0][5] = Optional.empty();
        this.shipComponents[0][6] = Optional.empty();
        List<Cordinate> incorrectComponents = getIncorrectConnectedComponents();
        incorrectComponents.addAll(getIncorrectEngines());
        incorrectComponents.addAll(getIncorrectDrills());

        return new ArrayList<>(incorrectComponents.stream().distinct().toList());
    }

    /**
     * Checks whether the current ship configuration is valid.
     * <p>
     * A ship is considered valid if:
     * <ul>
     *   <li>It has no invalid components (disconnected, misoriented, or misplaced parts)</li>
     *   <li>All components form a single connected structure (no multiple isolated pieces)</li>
     * </ul>
     *
     * @return {@code true} if the ship is valid, {@code false} otherwise
     */
    public boolean isShipValid() {
        if (!getIncorrectComponents().isEmpty())
            return false;

        if (getMultiplePieces().size() > 1)
            return false;

        return true;
    }

    /**
     * Updates all housing components on the ship by setting their allowed guests.
     * <p>
     * For each housing component, it examines adjacent components and adds
     * their alien colors as allowed guests, based on proximity.
     */
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
     * Calculates and returns the total number of exposed connectors on the perimeter of the ship.
     * An exposed connector is a connector (not FLAT) of a component that is not connected
     * to another component.
     *
     * @return The total count of exposed connectors.
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
     * Returns the total number of guests (aliens) currently hosted across all
     * {@link Housing} components on the ship.
     *
     * @return The total quantity of guests.
     */
    public int getQuantityGuests() {
        return getCompStream().
                mapToInt(comp -> comp.getGuests().size())
                .sum();
    }

    /**
     * Simulates stealing a given quantity of goods and/or batteries from the ship.
     * It prioritizes stealing goods from highest value to lowest value, then moves
     * to batteries if the quantity still needs to be met.
     *
     * @param quantity The total quantity of goods/batteries to steal.
     * @return A {@link Map} where keys are component IDs and values are lists of {@link GoodType}
     * that were stolen from that component.
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

    /**
     * Sets the currently activated engine power of the ship. This value is temporary
     * and used during specific adventure card effects where {@link DoubleEngine}s might be activated.
     *
     * @param activatedEnginePower The new value for activated engine power.
     */
    public void setActivatedEnginePower(int activatedEnginePower) {
        this.activatedEnginePower = activatedEnginePower;
    }

    /**
     * Returns the currently activated engine power of the ship.
     *
     * @return The activated engine power.
     */
    public int getActivatedEnginePower() {
        return activatedEnginePower;
    }

    /**
     * Compares this ship's crew size with another ship's crew size.
     *
     * @param other the other ShipBoard to compare with
     * @return 1 if this ship has more guests, -1 if fewer, 0 if equal
     */
    public int compareCrew(ShipBoard other) {
        if (this.getQuantityGuests() > other.getQuantityGuests())
            return 1;
        else if (this.getQuantityGuests() < other.getQuantityGuests())
            return -1;
        return 0;
    }

    /**
     * Compares this ship's firepower with another ship's firepower.
     *
     * @param other the other ShipBoard to compare with
     * @return 1 if this ship has more firepower, -1 if fewer, 0 if equal
     */
    public int compareActivatedFirePower(ShipBoard other) {
        if (this.getActivatedFirePower() > other.getActivatedFirePower())
            return 1;
        else if (this.getActivatedFirePower() < other.getActivatedFirePower())
            return -1;
        return 0;
    }

    /**
     * Compares this ship's engine power with another ship's engine power.
     *
     * @param other the other ShipBoard to compare with
     * @return 1 if this ship has more engine power, -1 if fewer, 0 if equal
     */
    public int compareActivatedEnginePower(ShipBoard other) {
        if (this.getActivatedEnginePower() > other.getActivatedEnginePower())
            return 1;
        else if (this.getActivatedEnginePower() < other.getActivatedEnginePower())
            return -1;
        return 0;
    }


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
     * Returns a flat list of all {@link Optional} {@link Component} objects
     * currently on the {@link ShipBoard}, including empty slots represented by {@link Optional#empty()}.
     *
     * @return A {@link List} of {@link Optional<Component>}.
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
     * Returns a list of {@link Cordinate} objects for all {@link DoubleDrill} components
     * currently placed on the shipboard.
     *
     * @return A {@link List} of {@link Cordinate}s where Double Drills are located.
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
     * Checks whether there exists a {@link Shield} component on the ship that can cover
     * the specified {@link Direction}.
     *
     * @param direction The {@link Direction} (e.g., FRONT, LEFT) to check for shield coverage.
     * @return {@code true} if a shield exists that covers the given direction, {@code false} otherwise.
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
     * Returns a {@link List} of {@link Cordinate} objects for all {@link Drill} components
     * (including {@link DoubleDrill}s) on the ship that are facing the given {@link Direction}.
     * This is useful for identifying drills that can be used to mitigate threats from a specific direction.
     *
     * @param direction The {@link Direction} a drill must be facing to be included in the result.
     * @return A {@link List} of {@link Cordinate}s where drills facing the specified direction are located.
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


    /**
     * Returns a list of drill coordinates in the specified direction
     * that are aligned or near the given row or column number.
     *
     * @param direction the direction to consider (FRONT, BACK, LEFT, RIGHT)
     * @param number the reference row or column number
     * @return list of coordinates of drills matching the criteria
     */
    public List<Cordinate> possibleDrills(Direction direction, int number){
        List<Cordinate> result = new ArrayList<>();
        List<Cordinate> drills = getDrills(direction);

        switch (direction) {
            case FRONT -> {
                for (Cordinate cord : drills) {
                    if (cord.getColumn() == number - 4)
                        result.add(cord);
                }
            }

            case BACK -> {
                for (Cordinate cord : drills) {
                    if (Math.abs((4 + cord.getColumn()) - number) <= 1)
                        result.add(cord);
                }
            }

            case LEFT, RIGHT -> {
                for (Cordinate cord : drills) {
                    if (Math.abs((5 + cord.getRow()) - number) <= 1)
                        result.add(cord);
                }
            }
        }

        return result;
    }

    /**
     * Counts the total number of human guests present across all ship components.
     *
     * @return the total count of human guests
     */
    public int getNumberHumans(){
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

    /**
     * Returns a list of coordinates of housing components that can accommodate
     * a guest of the specified type.
     *
     * @param type the guest type to check for availability
     * @return list of coordinates of available housing components
     */
    public List<Cordinate> getAvailableHousing(GuestType type){
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