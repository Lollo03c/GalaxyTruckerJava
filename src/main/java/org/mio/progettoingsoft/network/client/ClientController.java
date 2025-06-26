package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.advCards.CannonPenalty;
import org.mio.progettoingsoft.advCards.Meteor;
import org.mio.progettoingsoft.advCards.sealed.CardState;
import org.mio.progettoingsoft.advCards.sealed.SldAdvCard;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.GuestType;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.CannotRotateHourglassException;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;
import org.mio.progettoingsoft.model.enums.CannonType;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.enums.MeteorType;
import org.mio.progettoingsoft.network.client.rmi.RmiClient;
import org.mio.progettoingsoft.network.client.socket.SocketClient;
import org.mio.progettoingsoft.network.server.VirtualServer;
import org.mio.progettoingsoft.utils.ConnectionInfo;
import org.mio.progettoingsoft.utils.Logger;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the client-side controller in the game, managing interactions between the client and the server.
 * This class follows the Singleton design pattern to ensure only one instance exists.
 */
public class ClientController {
    private static ClientController instance;
    private Client client;
    private int tempIdClient;
    private VirtualServer server;
    private final ConnectionInfo connectionInfo;
    private int hourglassCounter = 0;
    private boolean pendingHourglass = true;
    private boolean finishedBuilding = false;
    private Boolean finishedLastHourglass = false;

    /**
     * Returns whether the client has finished the building phase.
     *
     * @return true if the building phase is finished, false otherwise.
     */
    public boolean getFinishedBuilding() {
        return finishedBuilding;
    }

    /**
     * Returns the current value of the hourglass counter.
     *
     * @return The current hourglass counter value.
     */
    public int getHourglassCounter() {
        return hourglassCounter;
    }

    /**
     * Sets the pending hourglass status.
     *
     * @param pendingHourglass true if an hourglass is pending, false otherwise.
     */
    public void setPendingHourglass(boolean pendingHourglass) {
        this.pendingHourglass = pendingHourglass;
    }

    /**
     * Increments the hourglass counter and fires a property change event.
     */
    public void incrementHourglassCounter() {
        hourglassCounter++;
        support.firePropertyChange("hourglassCounter", null, hourglassCounter);
    }

    /**
     * Private constructor for the ClientController, initializing the logger,
     * setting the initial game state to START, and storing connection information.
     *
     * @param connectionInfo The connection details for the client.
     */
    private ClientController(ConnectionInfo connectionInfo) {
        Logger.setMinLevel(Logger.Level.DEBUG);

        this.setState(GameState.START);
        this.connectionInfo = connectionInfo;
    }

    /**
     * Returns whether there is a pending hourglass action.
     *
     * @return true if an hourglass action is pending; false otherwise
     */
    public boolean getPendingHourglass() {
        return pendingHourglass;
    }

    /**
     * Creates the singleton instance of the ClientController with the given connection information.
     *
     * @param connectionInfo the connection details used to initialize the controller
     * @throws RuntimeException if the instance has already been created
     */
    public static void create(ConnectionInfo connectionInfo) {
        if (instance == null) {
            instance = new ClientController(connectionInfo);
        } else {
            throw new RuntimeException("Client controller already exists");
        }
    }

    /**
     * Returns the singleton instance of the ClientController.
     *
     * @return the ClientController instance
     * @throws RuntimeException if the instance has not been created yet
     */
    public static synchronized ClientController getInstance() {
        if (instance == null)
            throw new RuntimeException("Client controller not created");
        return instance;
    }

    private GameState gameState;
    private CardState cardState;
    private final Object stateLock = new Object();
    private final Object flyboardLock = new Object();
    private final Object shipboardLock = new Object();
    private final Object listLock = new Object();
    private final Object cardLock = new Object();
    private final Object cardStateLock = new Object();
    private final Object hourglassLock = new Object();
    FlyBoard flyBoard;
    ShipBoard shipBoard;

    private String nickname;
    private int idGame;

    private int inHandComponent;
    private int inHandDeck;
    private int tmpRotation;

    private SldAdvCard card;

    private List<Integer> availablePlacesOnCircuit;

    private List<GoodType> goodsToInsert = new ArrayList<>();
    private Meteor meteor;
    private CannonPenalty cannon;

    private String choiceErrorMessage;

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private boolean usedBattery;
    private Cordinate cordinate;

    /**
     * Adds a PropertyChangeListener to be notified of property updates.
     *
     * @param listener the listener to register
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    /**
     * Sets whether the last hourglass action has been completed.
     *
     * @param finishedLastHourglass true if the last hourglass is finished; false otherwise
     */
    public void setFinishedLastHourglass(boolean finishedLastHourglass) {
        synchronized (hourglassLock) {
            this.finishedLastHourglass = finishedLastHourglass;
        }
    }

    /**
     * Returns whether the last hourglass action has been completed.
     *
     * @return true if the last hourglass is finished; false otherwise
     */
    public Boolean getFinishedLastHourglass() {
        synchronized (hourglassLock) {
            return this.finishedLastHourglass;
        }
    }

    /**
     * Sets the current game ID.
     *
     * @param gameId the game ID to assign
     */
    public void setGameId(int gameId) {
        this.idGame = gameId;
    }

    /**
     * Updates the game state and fires a property change event if needed.
     *
     * @param state the new game state
     */
    public void setState(GameState state) {
        GameState oldState;
        if (state.equals(GameState.FINISH_HOURGLASS))
            pendingHourglass = false;
        synchronized (hourglassLock) {
            if (state.equals(GameState.FINISH_LAST_HOURGLASS)) {
                finishedLastHourglass = true;
                pendingHourglass = false;
            }
        }
        synchronized (stateLock) {
            oldState = this.gameState;
            this.gameState = state;
        }

        if (oldState == null || !oldState.equals(state)) {
            support.firePropertyChange("gameState", oldState, state);

        } else {
            support.firePropertyChange("gameState", oldState, GameState.IDLE);
            support.firePropertyChange("gameState", GameState.IDLE, state);
        }
        Logger.debug("GameState: " + oldState + " -> " + state);
    }

    /**
     * Returns the current game state.
     *
     * @return the current GameState
     */
    public GameState getState() {
        synchronized (stateLock) {
            return gameState;
        }
    }

    /**
     * Updates the card state and sets the game state to CARD_EFFECT.
     * Also fires property change events if needed.
     *
     * @param state the new card state
     */
    public void setCardState(CardState state) {
        CardState oldState;
        synchronized (cardStateLock) {
            oldState = this.cardState;
            this.cardState = state;
        }
        synchronized (stateLock) {
            setState(GameState.CARD_EFFECT);
        }
        if (oldState != state) {
            support.firePropertyChange("cardState", oldState, state);
            Logger.debug("CardState: " + oldState + " -> " + state);
        } else {
            support.firePropertyChange("cardState", oldState, CardState.IDLE);
            support.firePropertyChange("cardState", CardState.IDLE, state);
            Logger.debug("CardState: " + oldState + " -> " + state);
        }
    }

    /**
     * Returns the current card state.
     *
     * @return the current CardState
     */
    public CardState getCardState() {
        synchronized (cardStateLock) {
            return cardState;
        }
    }

    /**
     * Returns the nickname of the player.
     *
     * @return the player's nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Returns a list of available places on the circuit.
     *
     * @return a copy of the available places
     */
    public List<Integer> getAvailablePlacesOnCircuit() {
        synchronized (listLock) {
            return new ArrayList<>(availablePlacesOnCircuit);
        }
    }

    /**
     * Returns the lock object used for synchronizing game state changes.
     *
     * @return the state lock object
     */
    public Object getStateLock() {
        return stateLock;
    }

    /**
     * Sets the temporary client ID.
     *
     * @param idClient the client ID to set
     */
    public void setIdClient(int idClient) {
        this.tempIdClient = idClient;
    }

    /**
     * Sets the player's nickname.
     *
     * @param nickname the nickname to assign
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Returns whether the battery has been used.
     *
     * @return true if battery was used; false otherwise
     */
    public boolean isUsedBattery() {
        return usedBattery;
    }

    /**
     * Returns the FlyBoard associated with the current game.
     *
     * @return the FlyBoard
     */
    public FlyBoard getFlyBoard() {
        synchronized (flyboardLock) {
            return flyBoard;
        }
    }

    /**
     * Returns a snapshot of the current game information.
     *
     * @return a GameInfo object with current game details
     */
    public GameInfo getGameInfo() {
        synchronized (flyboardLock) {
            return new GameInfo(idGame, flyBoard.getMode(), flyBoard.getNumPlayers());
        }
    }

    /**
     * Returns the lock object used for FlyBoard synchronization.
     *
     * @return the FlyBoard lock object
     */
    public Object getFlyboardLock() {
        return flyboardLock;
    }

    /**
     * Returns the lock object used for ShipBoard synchronization.
     *
     * @return the ShipBoard lock object
     */
    public Object getShipboardLock() {
        return shipboardLock;
    }

    /**
     * Returns the ID of the currently held component.
     *
     * @return the ID of the component in hand
     */
    public int getInHandComponent() {
        return inHandComponent;
    }

    /**
     * Returns the Component object currently held in hand.
     *
     * @return the in-hand Component
     */
    public Component getInHandComponentObject() {
        return flyBoard.getComponentById(inHandComponent);
    }

    /**
     * Returns the player's ShipBoard.
     *
     * @return the ShipBoard
     */
    public ShipBoard getShipBoard() {
        synchronized (shipboardLock) {
            return shipBoard;
        }
    }

    /**
     * Returns the player's ShipBoard.
     *
     * @return the ShipBoard
     */
    public Meteor getMeteor() {
        return meteor;
    }

    /**
     * Increases the temporary rotation value, cycling from 0 to 3.
     */
    public void increaseTmpRotation() {
        if (tmpRotation < 3) {
            tmpRotation++;
        } else {
            tmpRotation = 0;
        }
    }

    /**
     * Resets the temporary rotation value to 0.
     */
    public void resetTmpRotation() {
        tmpRotation = 0;

    }

    /**
     * Returns the current temporary rotation value.
     *
     * @return the temporary rotation (0–3)
     */
    public int getTmpRotation() {
        return tmpRotation;
    }

    /**
     * Sets the ID of the component currently held in hand.
     *
     * @param idComp the component ID
     */
    public void setInHandComponent(int idComp) {
        this.inHandComponent = idComp;
    }

    /**
     * Returns the ID of the current game.
     *
     * @return the game ID
     */
    public int getIdGame() {
        return idGame;
    }

    /**
     * Sets the index of the deck currently held in hand.
     *
     * @param deckNumber the deck index
     */
    public void setInHandDeck(int deckNumber) {
        inHandDeck = deckNumber;
    }

    /**
     * Returns the index of the deck currently held in hand.
     *
     * @return the in-hand deck index
     */
    public int getInHandDeck() {
        return inHandDeck;
    }

    /**
     * Returns the circuit of players on the FlyBoard.
     *
     * @return a list of optional players in the circuit
     */
    public List<Optional<Player>> getCircuit() {
        synchronized (flyboardLock) {
            return flyBoard.getCircuit();
        }
    }

    /**
     * Returns the current error message related to a choice.
     *
     * @return the error message, or null if none
     */
    public String getErrMessage() {
        return choiceErrorMessage;
    }

    /**
     * Clears the current error message.
     */
    public void resetErrMessage() {
        choiceErrorMessage = null;
    }

    /*
     * methods called by the server to update the game state (and the model)
     */

    /**
     * Initializes the FlyBoard with the specified game mode, players, and decks.
     * Sets up player colors and associates the ShipBoard with the current player.
     *
     * @param mode    the selected game mode
     * @param players a map of player nicknames to their HousingColor
     * @param decks   the list of decks used in the game
     */
    public void setFlyBoard(GameMode mode, Map<String, HousingColor> players, List<List<Integer>> decks) {
        synchronized (flyboardLock) {
            flyBoard = FlyBoard.createFlyBoard(mode, players.keySet());
            for (Player player : flyBoard.getPlayers()) {
                HousingColor color = players.get(player.getNickname());
                player.setHousingColor(color);
            }
            shipBoard = flyBoard.getPlayerByUsername(nickname).getShipBoard();
            if (mode == GameMode.NORMAL) {
                flyBoard.setLittleDecks(decks);
            }
        }
    }

    /**
     * Sets the selected advanced card by ID and prepares its goods for insertion.
     *
     * @param idCard the ID of the selected card
     */
    public void setCard(int idCard) {
        synchronized (cardLock) {
            synchronized (flyboardLock) {
                this.card = flyBoard.getSldAdvCardByID(idCard);
                goodsToInsert = new ArrayList<>(card.getGoods());
                Logger.debug("settata la carta: " + card.getCardName());
            }
        }
    }

    /**
     * Returns the advanced card currently being played.
     *
     * @return the selected SldAdvCard
     */
    public SldAdvCard getPlayedCard() {
        synchronized (cardLock) {
            return card;
        }
    }

    /**
     * Adds a component to the uncovered components list.
     *
     * @param idComp the ID of the uncovered component
     */
    public void addUncoveredComponent(int idComp) {
        synchronized (flyBoard.getUncoveredComponents()) {
            flyBoard.getUncoveredComponents().add(idComp);
        }
    }

    /**
     * Removes a component from the uncovered components list.
     *
     * @param idComp the ID of the component to remove
     */
    public void removeUncovered(Integer idComp) {
        synchronized (flyBoard.getUncoveredComponents()) {
            flyBoard.getUncoveredComponents().remove(idComp);
        }
    }

    /**
     * Adds a component to another player's ShipBoard at a specified position and rotation.
     *
     * @param nickname  the target player's nickname
     * @param idComp    the ID of the component
     * @param cordinate the coordinate on the ShipBoard
     * @param rotations the number of clockwise rotations
     */
    public void addOtherPlayersComponent(String nickname, int idComp, Cordinate cordinate, int rotations) {
        ShipBoard otherShipboard = flyBoard.getPlayerByUsername(nickname).getShipBoard();

        synchronized (otherShipboard) {
            otherShipboard.addComponentToPosition(idComp, cordinate, rotations);
        }
    }

    /**
     * Adds a deck number to the list of available decks.
     *
     * @param deckNumber the number of the deck to add
     */
    public void addAvailableDeck(int deckNumber) {
        synchronized (flyBoard.getAvailableDecks()) {
            flyBoard.getAvailableDecks().add(deckNumber);
        }
    }

    /**
     * Removes a deck number from the list of available decks.
     *
     * @param deckNumber the number of the deck to remove
     */
    public void removeDeck(Integer deckNumber) {
        synchronized (flyBoard.getAvailableDecks()) {
            flyBoard.getAvailableDecks().remove(deckNumber);
        }
    }

    /**
     * Sets the list of currently available positions on the circuit.
     *
     * @param availablePlaces the list of available positions
     */
    public void setAvailablePlaces(List<Integer> availablePlaces) {
        synchronized (listLock) {
            this.availablePlacesOnCircuit = new ArrayList<>(availablePlaces);
        }
    }

    /**
     * Adds another player to the circuit at the specified place.
     *
     * @param nickname the player's nickname
     * @param place    the position on the circuit
     */
    public void addOtherPlayerToCircuit(String nickname, int place) {
        synchronized (flyboardLock) {
            flyBoard.addPlayerToCircuit(nickname, place);
        }
    }

    /**
     * Moves a player forward on the circuit by a given number of steps and notifies listeners.
     *
     * @param nickname the player's nickname
     * @param steps    the number of steps to move
     */
    public void advancePlayer(String nickname, int steps) {
        int oldPos, newPos;
        synchronized (flyboardLock) {
            Player player = flyBoard.getPlayerByUsername(nickname);
            oldPos = flyBoard.getPlayerPositionOnCircuit(nickname);
            flyBoard.moveDays(player, steps);
            newPos = flyBoard.getPlayerPositionOnCircuit(nickname);
        }
        support.firePropertyChange("circuit", oldPos, newPos);
        Logger.debug("Moved " + nickname + " from " + oldPos + " to " + newPos);
    }

    /**
     * Triggers a generic error related to a player's choice, notifying the UI with a temporary state change.
     *
     * @param msg the error message to display
     */
    public void genericChoiceError(String msg) {
        CardState old = getCardState();
        choiceErrorMessage = msg;
        setCardState(CardState.ERROR_CHOICE);
        setCardState(old);
    }

    /*
     * Methods called by the view to handle the input and communicate with the server
     */

    /**
     * Connects to the server using either RMI or Socket, sets initial game state, and initializes the client.
     *
     * @param isRmi true to use RMI, false to use Socket
     */
    public void connectToServer(boolean isRmi) {
        setState(GameState.WAITING);
        try {
            client = isRmi ? new RmiClient(connectionInfo) : new SocketClient(connectionInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            client.connect();
            server = client.getServer();
            setState(GameState.NICKNAME);
            synchronized (this) {
                this.notifyAll();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Starts the hourglass timer by requesting the server to do so.
     */
    public void startHourglass() {
        try {
            pendingHourglass = true;
            server.startHourglass(idGame);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends the player's nickname to the server for validation or registration.
     *
     * @param nickname the player's chosen nickname
     */
    public void handleNickname(String nickname) {
        try {
            server.handleNickname(tempIdClient, nickname);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends the selected game info to the server to create or join a game.
     *
     * @param gameInfo the game configuration to handle
     */
    public void handleGameInfo(GameInfo gameInfo) {
        try {
            server.handleGameInfo(gameInfo, nickname);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles user selection during the ship building phase and updates the game state accordingly.
     *
     * @param chosen the user's selected option
     */
    public void handleBuildingShip(int chosen) {
        if (chosen == 1) {
            try {
                server.getCoveredComponent(idGame, nickname);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (chosen == 2) {
            setState(GameState.DRAW_UNCOVERED_COMPONENTS);
        } else if (chosen == 3) {
            setState(GameState.VIEW_BOOKED);
        } else if (chosen == 4) {
            setState(GameState.VIEW_SHIP_BUILDING);
        } else if (chosen == 5 && flyBoard.getMode().equals(GameMode.NORMAL)) {
            setState(GameState.VIEW_DECKS_LIST);
        } else if (chosen == 5 && flyBoard.getMode().equals(GameMode.EASY)) {
            //server.playerReady()
            setState(GameState.END_BUILDING);
        } else if (chosen == 6) {
            setState(GameState.END_BUILDING);

            try {
                finishedBuilding = true;
                server.endBuild(idGame, nickname);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (chosen == 7) {
            setState(GameState.CHOICE_BUILT);
        } else if (chosen == 8) {
            try {
                if (pendingHourglass) {
                    throw new CannotRotateHourglassException("hourglass timer is already started");
                } else if (hourglassCounter == 2 && !getState().equals(GameState.END_BUILDING)) {
                    throw new CannotRotateHourglassException("you cannot rotate hourglass : you need to finish your ship building first");
                } else if (hourglassCounter == 3) {
                    throw new CannotRotateHourglassException("hourglass cannote be rotated anymore");
                }
                pendingHourglass = true;
                server.startHourglass(idGame);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            setState(GameState.BUILDING_SHIP);
        }
    }

    /**
     * Rotates the hourglass if allowed. Only used by players who have completed their ship building.
     */
    public void rotateHourglass() {
        try {
            if (pendingHourglass) {
                throw new CannotRotateHourglassException("hourglass timer is already started");
            } else if (hourglassCounter == 3) {
                throw new CannotRotateHourglassException("hourglass cannote be rotated anymore");
            }
            pendingHourglass = true;
            server.startHourglass(idGame);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        setState(GameState.END_BUILDING);
    }

    /**
     * Requests to draw an uncovered component from the server. Fails if the component is not available.
     *
     * @param idComp the ID of the uncovered component to draw
     */
    public void drawUncovered(int idComp) {
        if (!flyBoard.getUncoveredComponents().contains(idComp)) {
            setState(GameState.UNABLE_UNCOVERED_COMPONENT);
            return;
        }
        try {
            server.drawUncovered(idGame, nickname, idComp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends the player's landing choice to the server when landing on a planet.
     *
     * @param choice the index of the selected planet or landing action
     */
    public void landOnPlanet(int choice) {
        try {
            server.landOnPlanet(idGame, nickname, choice);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Attempts to place the selected component onto the ShipBoard at the given coordinate and rotation,
     * and notifies the server. Handles placement errors.
     *
     * @param cordinate the position where to place the component
     * @param rotations the number of clockwise rotations to apply
     */
    public void addComponent(Cordinate cordinate, int rotations) {
        try {
            shipBoard.addComponentToPosition(inHandComponent, cordinate, rotations);

            server.addComponent(idGame, nickname, inHandComponent, cordinate, rotations);
            inHandComponent = -1;
            resetTmpRotation();

            setState(GameState.BUILDING_SHIP);
        } catch (IncorrectShipBoardException e) {
            setState(GameState.ERROR_PLACEMENT);
        } catch (Exception e) {
            shipBoard.removeComponent(cordinate);
            throw new RuntimeException(e);
        }
    }

    /**
     * Books the current in-hand component onto the player's ShipBoard.
     * Sets the state to BUILDING_SHIP or SWITCH_BOOKED in case of error.
     */
    public void bookComponent() {
        try {
            flyBoard.getPlayerByUsername(nickname).getShipBoard().addBookedComponent(inHandComponent);
            setState(GameState.BUILDING_SHIP);
        } catch (IncorrectShipBoardException e) {
            setState(GameState.SWITCH_BOOKED);
        }
    }

    /**
     * Chooses a previously booked component from the given position and sets it as the in-hand component.
     *
     * @param pos 1-based index of the booked component to use
     */
    public void choseBookedComponent(int pos) {
        int idComp = shipBoard.getBookedComponents().get(pos - 1).get();
        shipBoard.removedBookedComponent(pos - 1);

        inHandComponent = idComp;
        setState(GameState.COMPONENT_MENU);
    }

    /**
     * Swaps the in-hand component with the one at the specified booked position.
     *
     * @param posToRemove the index of the booked component to replace
     */
    public void bookComponent(int posToRemove) {
        int idComp = shipBoard.getBookedComponents().get(posToRemove).get();
        resetTmpRotation();

        shipBoard.swapBookComponent(inHandComponent, posToRemove);
        inHandComponent = idComp;
        setState(GameState.COMPONENT_MENU);
    }

    /**
     * Discards the currently in-hand component and notifies the server.
     */
    public void discardComponent() {
        Component comp = flyBoard.getComponentById(inHandComponent);
        comp.reinitializeRotations();
        resetTmpRotation();

        try {
            server.discardComponent(idGame, inHandComponent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        setState(GameState.BUILDING_SHIP);
    }

    /**
     * Retrieves a booked component by index and sets it as the in-hand component.
     *
     * @param index index (0 or 1) of the booked component slot
     */
    public void getBooked(int index) {
        int ret = -1;
        if (index == 0 || index == 1) {
            synchronized (shipboardLock) {
                if (shipBoard.getBookedComponents().get(index).isPresent()) {
                    inHandComponent = shipBoard.getBookedComponents().get(index).get();
                    if (index == 0) {
                        shipBoard.getBookedComponents().remove(index);
                        shipBoard.getBookedComponents().add(Optional.empty());
                    } else {
                        shipBoard.getBookedComponents().set(index, Optional.empty());
                    }

                    setState(GameState.COMPONENT_MENU);
                }
            }
        }
    }

    /**
     * Requests to book the specified deck from the server.
     *
     * @param deckNumber the deck number to book
     */
    public void bookDeck(Integer deckNumber) {
        try {
            server.bookDeck(idGame, nickname, deckNumber);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Frees the deck previously booked by the player.
     */
    public void freeDeck() {
        try {
            server.freeDeck(idGame, nickname, inHandDeck);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Requests the server to assign a default build configuration.
     */
    public void builtDefault() {
        try {
            server.takeBuild(idGame, nickname);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Assigns a pre-built ShipBoard to the specified player based on their color.
     *
     * @param nick the nickname of the player to assign the ship to
     */
    public void assignBuild(String nick) {
        synchronized (shipboardLock) {
            HousingColor color = flyBoard.getPlayerByUsername(nick).getColor();

            try {
                if (nick.equals(nickname)) {
                    flyBoard.getPlayerByUsername(nick).setShipBoard(flyBoard.getBuiltShip(color));
                    shipBoard = flyBoard.getPlayerByUsername(nick).getShipBoard();

                    Logger.debug(nick + " " + color + "assegnato");
                } else {
                    //il ramo di else serve per non creare bug
                    Logger.debug(nick + " " + color);
                    flyBoard.getPlayerByUsername(nick).setShipBoard(flyBoard.getBuiltShip(color));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns the list of incorrect component coordinates on the player's ShipBoard.
     *
     * @return list of invalid component coordinates
     */
    public List<Cordinate> getIncorrectComponents() {
        return shipBoard.getIncorrectComponents();
    }

    /**
     * Returns a list of disconnected component blocks (standalone).
     * Also removes any booked components before calculation.
     *
     * @return list of sets of standalone components
     */
    public List<Set<Component>> getStandAloneBlocks() {
        shipBoard.removedBookedComponent(0);
        shipBoard.removedBookedComponent(1);


        return shipBoard.getMultiplePieces();
    }

    /**
     * Removes all standalone component blocks except the one at the given index.
     *
     * @param blockToKeep the index of the block to preserve
     */
    public void removeStandAloneBlocks(int blockToKeep) {
        List<Set<Component>> standAloneBlocks = shipBoard.getMultiplePieces();

        List<Cordinate> componentsToRemove;
        for (int i = 0; i < standAloneBlocks.size(); i++) {
            if (i != blockToKeep) {
                componentsToRemove = standAloneBlocks.get(i).stream().map(Component::getCordinate).toList();
                removeComponents(componentsToRemove);
            }
        }
    }

    /**
     * Notifies the server that the player has finished validation, indicating battery usage.
     *
     * @param usedBattey whether the player used a battery (typo: should be "usedBattery")
     */
    public void endValidation(boolean usedBattey) {
        try {
            server.endValidation(idGame, nickname, usedBattey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Notifies the server that the player has finished building their ship.
     */
    public void endBuild() {
        try {
            server.endBuild(idGame, nickname);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends the selected place in the circuit to the server.
     *
     * @param place the place index the player chose
     */
    public void choosePlace(int place) {
        try {
            server.choosePlace(idGame, nickname, place);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Requests a new advanced card from the server.
     */
    public void drawNewAdvCard() {
        try {
            server.drawCard(idGame, nickname);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Activates a double engine with the specified number.
     *
     * @param number the engine index or power value
     */
    public void activateDoubleEngine(int number) {
        try {
            server.activateDoubleEngine(idGame, nickname, number);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a copy of the list of goods the player needs to insert.
     *
     * @return list of goods to insert
     */
    public List<GoodType> getGoodsToInsert() {
        synchronized (listLock) {
            return new ArrayList<>(goodsToInsert);
        }
    }

    /**
     * Retrieves the list of goods from planets assigned to the current player.
     *
     * @return list of goods available from landed planets
     */
    public List<GoodType> getPlanetGoods() {
        Player player;
        List<GoodType> toInsert = new ArrayList<>();
        synchronized (flyboardLock) {
            player = flyBoard.getPlayerByUsername(nickname);
        }
        synchronized (cardLock) {
            toInsert = card.getPlanets().stream()
                    .filter(p -> p.getPlayer().isPresent() && p.getPlayer().get().equals(player))
                    .map(x -> x.getGoods())
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }
        Logger.debug("getGoodsToInsert: " + toInsert);
        return toInsert;
    }

    /**
     * Sends a request to leave or stay in the flight phase.
     *
     * @param leave true to leave the flight, false to stay
     */
    public void leaveFlight(boolean leave) {
        try {
            server.leaveFlight(idGame, nickname, leave);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds credits to the specified player.
     *
     * @param nick nickname of the player
     * @param credits amount of credits to add
     */
    public void addCredits(String nick, int credits) {
        Logger.info(nick + " added " + credits + "credits");
        int tot;
        synchronized (flyboardLock) {
            flyBoard.getPlayerByUsername(nick).addCredits(credits);
            tot = flyBoard.getPlayerByUsername(nick).getCredits();
        }
        support.firePropertyChange("credits", 0, tot);
    }

    /**
     * Removes one crew member from the specified component.
     *
     * @param idComp ID of the component losing the crew
     */
    public void crewLost(int idComp) {
        synchronized (flyboardLock) {
            Logger.info("lost crew member in " + idComp);
            flyBoard.getComponentById(idComp).removeGuest();
        }
    }


    /**
     * Clears the list of pending goods and skips the current card effect.
     */
    public void skipEffect() {
        goodsToInsert.clear();
        try {
            server.skipEffect(idGame, nickname, card.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Requests to remove crew members from specific coordinates.
     *
     * @param cordinatesToRemove list of coordinates from which to remove crew
     */
    public void removeCrew(List<Cordinate> cordinatesToRemove) {
        try {
            server.crewRemove(idGame, nickname, cordinatesToRemove);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends a request to the server to add a good to a component.
     *
     * @param idComp ID of the component
     * @param type type of the good to add
     */
    public void addGood(int idComp, GoodType type) {
        try {
            server.addGood(idGame, nickname, idComp, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a good of a given type to the specified component (client-side only).
     *
     * @param idComp ID of the component
     * @param type type of the good to add
     */
    public void addGoodToModel(int idComp, GoodType type) {
        synchronized (flyboardLock) {
            flyBoard.getComponentById(idComp).addGood(type);
        }
    }

    /**
     * Removes a pending good for the current player.
     *
     * @param nick player nickname (must match current user)
     * @param type type of the good to remove
     */
    public void removePendingGood(String nick, GoodType type) {
        if (nick.equals(nickname)) {
            synchronized (listLock) {
                goodsToInsert.remove(type);
            }
        }
    }

    /**
     * Sends a request to the server to remove a good from a component.
     *
     * @param idComp ID of the component
     * @param type type of the good to remove
     */
    public void removeGood(int idComp, GoodType type) {
        Logger.debug("Ask the server for remove " + type + " from " + idComp);
        try {
            server.removeGood(idGame, nickname, idComp, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes a good from the model (client-side only).
     *
     * @param idComp ID of the component
     * @param type type of the good to remove
     */
    public void removeGoodFromModel(int idComp, GoodType type) {
        synchronized (flyboardLock) {
            Logger.debug(type + "removed from " + idComp);
            flyBoard.getComponentById(idComp).removeGood(type);
        }
    }

    /**
     * Adds a pending good for the current player.
     *
     * @param nick player nickname (must match current user)
     * @param type type of the good to add
     */
    public void addPendingGood(String nick, GoodType type) {
        if (nick.equals(nickname))
            synchronized (listLock) {
                goodsToInsert.add(type);
            }
        Logger.debug("Added to pending " + nick);
    }

    /**
     * Assigns a player to land on a specified planet.
     *
     * @param nickname player nickname
     * @param choice index of the chosen planet
     */
    public void setPlayerOnPlanet(String nickname, int choice) {
        List<GoodType> tmpList = null;
        synchronized (cardLock) {
            Player player = flyBoard.getPlayerByUsername(nickname);
            card.getPlanets().get(choice).land(player);
        }
    }

    /**
     * Applies the current card's effect.
     */
    public void applyEffect() {
        try {
            server.applyEffect(idGame, nickname);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Activates double drills at the specified coordinates.
     *
     * @param drillsCordinate list of drill coordinates to activate
     */
    public void activateDoubleDrills(List<Cordinate> drillsCordinate) {
        try {
            server.activateDoubleDrills(idGame, nickname, drillsCordinate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    public void activateSlaver(List<Cordinate> activatedDrills, boolean wantsToActivate) {
//        try {
//            server.activateSlaver(idGame, nickname, activatedDrills, wantsToActivate);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    /**
     * Sends the result of the dice roll to the server.
     *
     * @param first first dice value
     * @param second second dice value
     */
    public void setRollResult(int first, int second) {
        try {
            //todo e' da cambiare
            server.setRollResult(idGame, nickname, 3, 3);
        } catch (Exception e) {
        }
    }


//    public void removeBattery(int quantity){
//        try{
//            server.removeBattery(idGame, nickname, quantity);
//        }
//        catch (Exception e){
//            throw new RuntimeException("");
//        }
//    }

    /**
     * Removes energy units from the battery depots with the given IDs.
     *
     * @param batteryDepotId list of component IDs to remove energy from
     */
    public void removeBatteriesFromModel(List<Integer> batteryDepotId) {
        synchronized (flyboardLock) {
            for (int id : batteryDepotId) {
                Logger.debug("Removed battery from component id " + id);
                flyBoard.getComponentById(id).removeOneEnergy();
            }
        }
    }

    /**
     * Requests the server to advance the meteor and apply effects.
     *
     * @param destroyed true if a component was destroyed
     * @param energy true if energy was used to stop the meteor
     */
    public void advanceMeteor(boolean destroyed, boolean energy) {
        try {
            server.advanceMeteor(idGame, nickname, destroyed, energy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Requests to remove a component from the ship, with animation.
     *
     * @param cordinate coordinate of the component to remove
     */
    public void removeComponent(Cordinate cordinate) {
        try {
            server.removeComponent(idGame, nickname, cordinate, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Immediately removes a component locally and notifies the server (no animation).
     *
     * @param cordinate coordinate of the component to remove
     */
    public void removeComponentImmediate(Cordinate cordinate) {
        try {
            shipBoard.removeComponent(cordinate);
            server.removeComponent(idGame, nickname, cordinate, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes a list of components immediately from the ship.
     *
     * @param cordinatesToRemove list of coordinates to remove
     */
    public void removeComponents(List<Cordinate> cordinatesToRemove) {
        for (Cordinate cordinate : cordinatesToRemove) {
            removeComponentImmediate(cordinate);
        }
    }

    /**
     * Removes a component from the specified player's ship at the given coordinate (client-side only).
     *
     * @param nickname nickname of the player
     * @param cord coordinate of the component to remove
     */
    public void removeComponentFromModel(String nickname, Cordinate cord) {
        synchronized (flyboardLock) {
            if (nickname.equals(this.nickname)) {
                Logger.debug("removed component of " + nickname + " from cordinate " + cord);
                ShipBoard otherShip = flyBoard.getPlayerByUsername(nickname).getShipBoard();
                shipBoard.drawShipboard();
                otherShip.removeComponent(cord);
            }
        }
    }

    /**
     * Handles a meteor hit event, storing its data and setting the card state.
     *
     * @param type type of the meteor
     * @param direction direction from which the meteor is coming
     * @param number roll number of the meteor
     * @param cord coordinate where the meteor hits
     */
    public void meteorHit(MeteorType type, Direction direction, int number, Cordinate cord) {
        meteor = new Meteor(direction, type);
        meteor.setNumber(number);
        meteor.setCordinateHit(cord);
        setCardState(CardState.METEOR_HIT);

        Logger.info(type + " " + direction + " " + number);
    }

    /**
     * Handles a cannon hit event, calculating the target and updating the card/game state.
     *
     * @param type type of the cannon (LIGHT or HEAVY)
     * @param direction direction from which the cannon shot is fired
     * @param number cannon roll number
     */
    public void cannonHit(CannonType type, Direction direction, int number) {
        System.out.println(direction + " " + number);
        shipBoard.drawShipboard();


        cannon = new CannonPenalty(direction, type);
        cannon.setNumber(number);
        setCardState(CardState.CANNON_HIT);

        Optional<Cordinate> optCordinateHit = cannon.findHit(shipBoard, number);

        if (optCordinateHit.isEmpty()) {
            advanceCannon(false, false);
            return;
        }

        Cordinate cordinateHit = optCordinateHit.get();
        this.cordinate = cordinateHit;
        Component componentHit = shipBoard.getOptComponentByCord(cordinateHit).get();

        cannon.setCordinateHit(cordinateHit);

        if (type.equals(CannonType.HEAVY)) {
            removeComponentImmediate(cordinateHit);

            boolean valid = shipBoard.isShipValid();
            if (valid) {
                advanceCannon(false, false);
            } else {
                setState(GameState.VALIDATION);
            }
        } else {
            setCardState(CardState.SHIELD_SELECTION);
        }


    }

    /**
     * Sends a request to the server to continue with the cannon phase after a hit.
     *
     * @param destroyed true if a component was destroyed
     * @param energy true if energy was used to shield the shot
     */
    public void advanceCannon(boolean destroyed, boolean energy) {
        try {
            server.advanceCannon(idGame, nickname, destroyed, energy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the currently active cannon penalty event.
     *
     * @return the current CannonPenalty object
     */
    public CannonPenalty getCannon() {
        return cannon;
    }

    /**
     * Removes a battery unit from the given battery depot component (client-side only).
     *
     * @param idBatteryDepot ID of the battery depot component
     */
    public void removeBatteryFromModel(int idBatteryDepot) {
        Logger.debug("remove battery from " + idBatteryDepot);
        synchronized (flyBoard) {
            flyBoard.getComponentById(idBatteryDepot).removeOneEnergy();
        }
    }

    /**
     * Removes the specified player from the scoreboard and the circuit (client-side only).
     *
     * @param nickname nickname of the player who leaves the flight phase
     */
    public void leaveFlightFromModel(String nickname) {
        Logger.info(nickname + " leave flight from model");
        int index = -1;
        synchronized (flyboardLock) {
            Player player = flyBoard.getPlayerByUsername(nickname);
            flyBoard.getScoreBoard().remove(player);


            for (int i = 0; i < flyBoard.getCircuit().size(); i++) {
                Optional<Player> optionalPlayer = flyBoard.getCircuit().get(i);

                if (optionalPlayer.isEmpty())
                    continue;

                if (optionalPlayer.get().getNickname().equals(nickname)) {
                    flyBoard.getCircuit().set(i, Optional.empty());
                    index = i;
                    break;
                }
            }
        }
        support.firePropertyChange("circuit", index, -1);
        Logger.info("Property fired");
    }

    /**
     * Adds guests to each player ship locally and sends the crew addition request to the server.
     *
     * @param addedCrew map of coordinates to guest types for crew placement
     */
    public void addCrew(Map<Cordinate, List<GuestType>> addedCrew) {
        for (Player pl : flyBoard.getScoreBoard()) {
            pl.getShipBoard().addGuestToShip();
        }



        try {
            server.addCrew(idGame, nickname, addedCrew);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a crew member to the model at the specified coordinate for the given player.
     *
     * @param nick nickname of the player
     * @param cordinate coordinate of the component where to add the guest
     * @param guestType type of the guest to add
     */
    public void addCrewToModel(String nick, Cordinate cordinate, GuestType guestType) {
        synchronized (flyboardLock) {

            ShipBoard ship = flyBoard.getPlayerByUsername(nick).getShipBoard();
            ship.addGuestToShip();

            int idComp = ship.getOptComponentByCord(cordinate).get().getId();
            flyBoard.getComponentById(idComp).addGuest(guestType);
        }
    }

    /**
     * Gets the coordinate of the most recent component hit (by cannon or meteor).
     *
     * @return coordinate of the last hit component
     */
    public Cordinate getCordinate() {
        return cordinate;
    }


    /**
     * Notifies that a crash occurred, setting the game state to GAME_CRASH.
     *
     * @param nickname nickname of the player who experienced the crash
     */
    public void notifyCrash(String nickname) {
        setState(GameState.GAME_CRASH);
    }
}