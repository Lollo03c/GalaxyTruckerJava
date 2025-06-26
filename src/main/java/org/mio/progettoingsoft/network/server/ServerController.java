package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.advCards.CannonPenalty;
import org.mio.progettoingsoft.advCards.Meteor;
import org.mio.progettoingsoft.advCards.sealed.*;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.GuestType;
import org.mio.progettoingsoft.exceptions.BadParameterException;
import org.mio.progettoingsoft.exceptions.IncorrectFlyBoardException;
import org.mio.progettoingsoft.exceptions.NotYourTurnException;
import org.mio.progettoingsoft.advCards.sealed.SldAbandonedShip;
import org.mio.progettoingsoft.advCards.sealed.SldAdvCard;
import org.mio.progettoingsoft.advCards.sealed.SldOpenSpace;
import org.mio.progettoingsoft.exceptions.*;
import org.mio.progettoingsoft.model.enums.CannonType;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.model.enums.MeteorType;
import org.mio.progettoingsoft.model.events.*;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.utils.Logger;

import java.util.*;

/**
 * The {@code ServerController} acts as the central point for handling all game-related
 * requests received from clients. It is a Singleton to ensure a single point of control
 * for managing game instances and client interactions on the server side.
 * It delegates specific game logic operations to the {@link GameManager} and
 * individual {@link GameServer} instances.
 */
public class ServerController {
    /**
     * SINGLETON IMPLEMENTATION
     */
    private static ServerController instance;

    /**
     * Returns the singleton instance of the {@code ServerController}.
     * If the instance does not exist, it creates one.
     * @return The single instance of {@code ServerController}.
     */
    public static ServerController getInstance() {
        if (instance == null) {
            instance = new ServerController();
        }
        return instance;
    }

    /**
     * Adds a new client's {@link VirtualClient} proxy to the {@link GameManager} for
     * initial acceptance and potential game assignment.
     * @param client The {@link VirtualClient} object representing the connected client.
     * @return A temporary ID assigned to the client.
     */
    public int addClientToAccept(VirtualClient client) {
        GameManager gameManager = GameManager.getInstance();
        return gameManager.addClientToAccept(client);
    }

    /**
     * Handles the nickname submitted by a client. It delegates to the {@link GameManager}
     * to associate the nickname with the client's temporary ID and potentially add them to a game.
     * @param idClient The temporary ID assigned to the client during connection.
     * @param nickname The nickname chosen by the client.
     */
    public void handleNickname(int idClient, String nickname) {
        GameManager gameManager = GameManager.getInstance();
        gameManager.addPlayerToGame(idClient, nickname);
    }

    /**
     * Handles the game setup information provided by a client (typically the host).
     * It retrieves the waiting game from the {@link GameManager} and configures it
     * with the specified game mode and number of players.
     * @param gameInfo The {@link GameInfo} containing the game mode and number of players.
     * @param nickname The nickname of the player providing the game information.
     */
    public void handleGameInfo(GameInfo gameInfo, String nickname) {
        GameManager gameManager = GameManager.getInstance();
        GameServer game = gameManager.getWaitingGame();
        game.setupGame(gameInfo.mode(), gameInfo.nPlayers());
    }

    /**
     * Handles a request from a client to add a component to their ship board.
     * It updates the player's {@link ShipBoard} with the new component.
     * It broadcasts this update to all other clients in the same game, so their views can be synchronized.
     * @param idGame The ID of the game to which the component is being added.
     * @param nickname The nickname of the player adding the component.
     * @param idComp The ID of the component being added.
     * @param cordinate The {@link Cordinate} on the ship board where the component is placed.
     * @param rotations The number of rotations applied to the component.
     */
    public void addComponent(int idGame, String nickname, int idComp, Cordinate cordinate, int rotations) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);

        ShipBoard shipBoard = game.getFlyboard().getPlayerByUsername(nickname).getShipBoard();
        shipBoard.addComponentToPosition(idComp, cordinate, rotations);

        Logger.debug(nickname + " added component " + idComp + " " + cordinate + " " + rotations);

        if (!game.isTesting()) {
            for (Player player : game.getFlyboard().getPlayers()) {
                if (!player.getNickname().equals(nickname)) {
                    VirtualClient client = game.getClients().get(player.getNickname());

                    try {
                        client.addComponent(nickname, idComp, cordinate, rotations);
                    } catch (Exception e) {
                        handleGameCrash(e, nickname, idGame);
                    }
                }
            }
        }
    }

    /**
     * Handles a request from a client to get a covered component from the FlyBoard.
     * It removes a component from the covered components stack on the {@link FlyBoard}
     * and sends it to the requesting client, then updates the client's state.
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player requesting the component.
     */
    public void getCoveredComponent(int idGame, String nickname) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();

        VirtualClient client = game.getClients().get(nickname);

        try {
            client.setInHandComponent(flyBoard.getCoveredComponents().removeLast());
            client.setState(GameState.COMPONENT_MENU);
        } catch (Exception e) {
            handleGameCrash(e, nickname, idGame);
        }
    }

    /**
     * Handles a request from a client to discard a component.
     * The discarded component is added back to the uncovered components pool on the {@link FlyBoard}.
     * This update is then broadcast to all clients in the game to synchronize their views.
     * @param idGame The ID of the game.
     * @param idComponent The ID of the component to discard.
     */
    public void discardComponent(int idGame, int idComponent) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        flyBoard.addUncoveredComponent(idComponent);

        for (String nickname : game.getClients().keySet()) {
            try {
                game.getClients().get(nickname).addUncoveredComponent(idComponent);
            } catch (Exception e) {
                handleGameCrash(e, nickname, idGame);
            }
        }
    }

    /**
     * Handles a request from a client to draw an uncovered component.
     * If the component exists in the uncovered components pool, it is removed,
     * broadcasted as removed to all other clients, and then set in the requesting
     * client's hand, and their state is updated. If the component is not found,
     * the client's state is set to {@link GameState#UNABLE_UNCOVERED_COMPONENT}.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player requesting to draw the component.
     * @param idComponent The ID of the component to draw.
     */
    public void drawUncovered(int idGame, String nickname, Integer idComponent) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();

        boolean removed = flyBoard.getUncoveredComponents().remove(idComponent);

        if (removed) {
            for (VirtualClient client : game.getClients().values()) {
                try {
                    client.removeUncovered(idComponent);
                } catch (Exception e) {
                    handleGameCrash(e, nickname, idGame);
                }
            }

            try {
                game.getClients().get(nickname).setInHandComponent(idComponent);
                game.getClients().get(nickname).setState(GameState.COMPONENT_MENU);
            } catch (Exception e) {
                handleGameCrash(e, nickname, idGame);
            }
        } else {
            try {
                game.getClients().get(nickname).setState(GameState.UNABLE_UNCOVERED_COMPONENT);
            } catch (Exception e) {
                handleGameCrash(e, nickname, idGame);
            }
        }
    }

    /**
     * Handles a request from a client to book a specific deck of cards.
     * It attempts to remove the requested deck from the list of available decks.
     * If successful, it broadcasts the removal to all clients, sets the deck in the
     * requesting client's hand, and updates their state. If the deck is not available,
     * the client's state is set to {@link GameState#UNABLE_DECK}.
     * Synchronization on {@code flyBoard.getAvailableDecks()} ensures thread safety
     * when modifying the list of available decks.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player attempting to book the deck.
     * @param deckNumber The number of the deck to book.
     */
    public void bookDeck(int idGame, String nickname, Integer deckNumber) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();

        synchronized (flyBoard.getAvailableDecks()) {
            List<Integer> availableDecks = flyBoard.getAvailableDecks();

            boolean removed = availableDecks.remove(deckNumber);

            if (removed) {
                for (VirtualClient client : game.getClients().values()) {
                    try {
                        client.removeDeck(deckNumber);
                        Logger.debug("removed deck " + deckNumber + " from client " + client);
                    } catch (Exception e) {
                        handleGameCrash(e, nickname, idGame);
                    }
                }

                try {
                    game.getClients().get(nickname).setInHandDeck(deckNumber);
                    game.getClients().get(nickname).setState(GameState.VIEW_DECK);
                    Logger.debug("Set deck " + deckNumber + " to " + nickname);
                } catch (Exception e) {
                    handleGameCrash(e, nickname, idGame);
                }
            } else {
                try {
                    game.getClients().get(nickname).setState(GameState.UNABLE_DECK);
                } catch (Exception e) {
                    handleGameCrash(e, nickname, idGame);
                }
            }
        }
    }

    /**
     * Frees a previously booked deck, making it available again.
     * The deck is added back to the pool of available decks on the {@link FlyBoard}.
     * This update is then broadcast to all clients in the game to synchronize their views.
     * The requesting client's state is updated to {@link GameState#BUILDING_SHIP}.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player freeing the deck.
     * @param deckNumber The number of the deck to free.
     */
    public void freeDeck(int idGame, String nickname, Integer deckNumber) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();

        synchronized (flyBoard.getAvailableDecks()) {
            List<Integer> availableDecks = flyBoard.getAvailableDecks();
            availableDecks.add(deckNumber);
            Logger.debug("Free deck " + deckNumber + ".");

            for (VirtualClient client : game.getClients().values()) {
                try {
                    client.addAvailableDeck(deckNumber);
                } catch (Exception e) {
                    handleGameCrash(e, nickname, idGame);
                }
            }

            try {
                game.getClients().get(nickname).setState(GameState.BUILDING_SHIP);
            } catch (Exception e) {
                handleGameCrash(e, nickname, idGame);
            }
        }
    }

    /**
     * Handles a player's request to "take build," meaning they claim a constructed ship.
     * The player is associated with a constructed ship on the {@link FlyBoard}.
     * This action is then broadcast to all clients to update their game state.
     * If the ship cannot be taken due to game rules (e.g., no available ship),
     * the player's state is updated to reflect an invalid choice.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player taking the build.
     */
    public void takeBuild(int idGame, String nickname) {

        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        Player player = flyBoard.getPlayerByUsername(nickname);

        Logger.debug(nickname + " assigned to ship " + player.getColor());
        try {
            flyBoard.takeCostructedShip(player);

            for (VirtualClient client : game.getClients().values()) {
                try {
                    client.setBuiltShip(nickname);
                } catch (Exception e) {
                    handleGameCrash(e, nickname, idGame);
                }
            }

            Event event = new SetStateEvent(nickname, GameState.BUILDING_SHIP);
            game.addEvent(event);

        } catch (IncorrectFlyBoardException e) {
            Event event = new SetStateEvent(nickname, GameState.INVALID_SHIP_CHOICE);
            game.addEvent(event);
        }


    }

    /**
     * Handles the completion of the ship building phase and initiates validation.
     * After a player indicates they've finished building, their ship's validity is checked.
     * If the ship is invalid, the player's state is updated to prompt re-validation.
     * If valid, guests are added to the ship. If all players have validated their ships
     * (and no adventure card has been played yet), the game proceeds to the "add crew" phase.
     * If an adventure card was played, the logic branches based on the card type.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player who ended validation.
     * @param usedBattery A boolean indicating whether the player used a battery during validation (relevant for some card effects).
     */
    public void endValidation(int idGame, String nickname, boolean usedBattery) {
        Logger.info(nickname + " ended ship building, has removed incorrect components and now needs to validate his ship.");
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        ShipBoard shipBoard = game.getFlyboard().getPlayerByUsername(nickname).getShipBoard();
        VirtualClient client = game.getClients().get(nickname);

        if (!shipBoard.isShipValid()) {
            Logger.error("Ship " + nickname + " is not valid.");

            Event event1 = new SetStateEvent(nickname, GameState.WAITING);
            Event event2 = new SetStateEvent(nickname, GameState.VALIDATION);
            game.addEvent(event1);
            game.addEvent(event2);
        } else {
            if (!flyBoard.isPlayedFirstCard()) {
                synchronized (game.getLock()) {
                    Logger.info("Ship " + nickname + " is valid.");
                    Player player = flyBoard.getPlayerByUsername(nickname);
                    flyBoard.getValidationPlayers().remove(player);
                    shipBoard.addGuestToShip();
                }

                if (flyBoard.getValidationPlayers().isEmpty()) {
                    flyBoard.setAddCrewPlayers(flyBoard.getScoreBoard());
                    for (Player p : flyBoard.getScoreBoard()) {
                        Event event = new SetStateEvent(p.getNickname(), GameState.ADD_CREW);
                        game.addEvent(event);
                    }
                }
            } else {
                switch (flyBoard.getPlayedCard()) {
                    case SldPirates pirates -> {
                        advanceCannon(idGame, nickname, false, usedBattery);
                    }

                    case SldCombatZone combatZone -> {
                        advanceCannon(idGame, nickname, false, usedBattery);
                    }

                    case SldMeteorSwarm meteorSwarm ->{
                        advanceMeteor(idGame, nickname, false, usedBattery);
                    }

                    default -> {

                    }
                }
            }

        }
    }

    /**
     * Retrieves the available starting positions for a player in a specific game and sends them to the client.
     * Sets the client's state to {@code CHOOSE_POSITION}.
     * If an error occurs, the game crash is handled.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player.
     */
    public void getStartingPosition(int idGame, String nickname) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        List<Integer> availablePlaces = flyBoard.getAvailableStartingPositions();
        VirtualClient client = game.getClients().get(nickname);
        try {
            client.setAvailablePlaces(availablePlaces);
            client.setState(GameState.CHOOSE_POSITION);
        } catch (Exception e) {
            handleGameCrash(e, nickname, idGame);
        }
    }

    /**
     * Allows a player to choose a starting position on the game board.
     * Adds the player to the circuit and broadcasts an {@code AddPlayerCircuit} event.
     * If the chosen place is invalid, the client is informed and their state is set to {@code WRONG_POSITION}.
     * If the board is ready for the adventure, all players' states are set to {@code VALIDATION}.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player.
     * @param place The chosen starting position.
     */
    public void choosePlace(int idGame, String nickname, int place) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        GameState state;
        VirtualClient client = game.getClients().get(nickname);
        try {
            flyBoard.addPlayerToCircuit(nickname, place);

            Event event = new AddPlayerCircuit(nickname, place);
            game.addEvent(event);

            state = GameState.END_BUILDING;
        } catch (BadParameterException e) {
            try {
                client.setAvailablePlaces(flyBoard.getAvailableStartingPositions());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            state = GameState.WRONG_POSITION;
        }

        try {
            Event event = new SetStateEvent(nickname, state);
            game.addEvent(event);

            if (flyBoard.isReadyToAdventure()) {

                for (Player player : flyBoard.getScoreBoard()) {
                    flyBoard.setValidationPlayers(flyBoard.getScoreBoard());

                    Event eve = new SetStateEvent(player.getNickname(), GameState.VALIDATION);
                    game.addEvent(eve);
                }
            }
        } catch (Exception e) {
            handleGameCrash(e, nickname, idGame);
        }

    }

    /**
     * Handles the end of the building phase for a player in a game.
     * If the game is not in testing mode, it sends the available starting positions to the client
     * and sets their state to {@code CHOOSE_POSITION}.
     * If an error occurs, the game crash is handled.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player.
     */
    public void endBuild(int idGame, String nickname) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        List<Integer> availablePlaces = flyBoard.getAvailableStartingPositions();

        if (!game.isTesting()) {
            VirtualClient client = game.getClients().get(nickname);
            try {
                client.setAvailablePlaces(availablePlaces);
                client.setState(GameState.CHOOSE_POSITION);
            } catch (Exception e) {
                handleGameCrash(e, nickname, idGame);
            }
        }

    }

    /**
     * Allows the leader player to draw a SldAdvCard from the deck.
     * If the current player is not the leader, a {@code NotYourTurnException} is thrown.
     * If the drawn card is a special card (ID 16 or 36) and there's only one player,
     * it repeatedly draws until a different card is found or the deck is empty.
     * If the deck is empty, the game ends.
     * The drawn card is then set as the played card, and relevant events are broadcast to all players.
     * The card's effect is initialized and applied, potentially setting the next meteor or player.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player drawing the card.
     * @throws NotYourTurnException If the player attempting to draw is not the leader.
     */
    public void drawCard(int idGame, String nickname) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        //controllo per vedere se il giocatore è il Leader
        if (!flyBoard.getScoreBoard().getFirst().equals(flyBoard.getPlayerByUsername(nickname))) {
            throw new NotYourTurnException();
        }
        flyBoard.setPlayedFirstCard(true);
//        SldAdvCard card = flyBoard.getSldAdvCardByID(29);
//
////        SldAdvCard card = flyBoard.drawSldAdvCard();
//        int id = 16;
//
//        //int id = 999;
//        //if id is 999 the deck is empty
////        id = ;
//        if(id == 999){
//            setEndGame(idGame);
//            return;
//        }
//        card = flyBoard.getSldAdvCardByID(id);
        int id = flyBoard.drawCard();
        SldAdvCard card = flyBoard.getSldAdvCardByID(id);
        if (id == 999) {
            setEndGame(idGame);
            return;
        }
        Logger.debug(nickname + " draws card " + card.getCardName());
        flyBoard.setPlayedCard(card);

        card.disegnaCard();
        Event first = new SetCardPlayedEvent(null, card.getId());
        game.addEvent(first);

        for (Player player : flyBoard.getPlayers()) {

            Event second = new SetStateEvent(player.getNickname(), GameState.NEW_CARD);
            game.addEvent(second);

        }
        card.init(game);

        switch (card) {
            case SldMeteorSwarm meteorSwarm -> {
                meteorSwarm.setNextMeteor();
            }

            default -> card.setNextPlayer();
        }


    }

    /**
     * Ends the game, calculates final scores, and notifies all clients of the game's end.
     * This includes assigning credits for positions, beautiful ships, and remaining goods,
     * as well as applying penalties for discarded components.
     *
     * @param idGame The ID of the game to end.
     */
    public void setEndGame(int idGame){
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        flyBoard.assignCreditsForPositions();
        flyBoard.assignCreditsForBeautifulShip();
        flyBoard.assignCreditsForRemainingGoods();
        flyBoard.penaltyForDiscardedComponents();

        for (String nick : game.getClients().keySet()) {
            Event event = new SetStateEvent(nick, GameState.ENDGAME);
            game.addEvent(event);
        }
    }

    /**
     * Allows the leader player to draw a specific SldAdvCard by its ID for testing purposes.
     * If the current player is not the leader, a {@code NotYourTurnException} is thrown.
     * The drawn card is then set as the played card.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player drawing the card.
     * @param idCard The ID of the card to draw.
     * @throws NotYourTurnException If the player attempting to draw is not the leader.
     */
    public void drawCardTest(int idGame, String nickname, int idCard) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        //controllo per vedere se il giocatore è il Leader
        if (!flyBoard.getScoreBoard().getFirst().equals(flyBoard.getPlayerByUsername(nickname))) {
            throw new NotYourTurnException();
        }
//        SldAdvCard card = flyBoard.drawSldAdvCard();
        SldAdvCard card = flyBoard.getSldAdvCardByID(idCard);
        Logger.debug(nickname + " draws card " + card.getCardName());
        flyBoard.setPlayedCard(card);
    }

    /**
     * Activates the double engine effect for a player based on the currently played SldAdvCard.
     * This method handles the specific logic for "SldOpenSpace" and "SldCombatZone" cards.
     * For "SldOpenSpace", it attempts to apply the card's effect. If an exception occurs during application,
     * a generic error message is sent to the client.
     * For "SldCombatZone", it records the player's engine power and sets the next player for engine activation.
     * Logs an error if the played card is not valid for this effect.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player activating the double engine.
     * @param number The value associated with the double engine activation (e.g., engine power).
     */
    public void activateDoubleEngine(int idGame, String nickname, int number) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        SldAdvCard card = flyBoard.getPlayedCard();
        Player player = flyBoard.getPlayerByUsername(nickname);


        switch (card) {
            case SldOpenSpace openSpace -> {

                try {
                    openSpace.applyEffect(player, number);
                } catch (IllegalStateException | BadParameterException | BadPlayerException |
                         NotEnoughBatteriesException e) {
                    VirtualClient client = game.getClients().get(nickname);
                    try {
                        client.genericChoiceError(e.getMessage());
                    } catch (Exception ex) {
                        handleGameCrash(ex, nickname, idGame);
                    }
                }
            }

            case SldCombatZone combatZone -> {
                combatZone.getEnginePower().put(player, number);

                combatZone.setNextPlayerEngine();
            }

            default -> {
                Logger.error("carta non valida per effetto activeDoubleEngine");
            }
        }
    }

    /**
     * Manages a player's decision to leave or stay in the current flight phase of a game.
     * If the player chooses to leave, they are removed from the scoreboard, a {@code LeavePlayerEvent} is broadcast,
     * and if no players remain, the game ends.
     * Regardless of their choice to leave or stay, the player is removed from the list of waiting players.
     * If all waiting players have made their choice, the game transitions to the card drawing phase,
     * with the leader being notified that they can draw a card, and other players being notified to expect a card draw.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player.
     * @param leave A boolean indicating whether the player chooses to leave ({@code true}) or stay ({@code false}).
     */
    public void leaveFlight(int idGame, String nickname, boolean leave) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        Logger.info(nickname + " is leaving " + leave);
        synchronized (game.getLock()) {
            FlyBoard flyBoard = game.getFlyboard();

            Player player = flyBoard.getPlayerByUsername(nickname);
            List<Player> watingPlayers = flyBoard.getWaitingPlayers();
            watingPlayers.remove(player);

            if (leave) {
                Logger.info(nickname + " is actually leaving");
                flyBoard.getScoreBoard().remove(player);
                for (int i = 0; i < flyBoard.getCircuit().size(); i++) {
                    Optional<Player> optionalPlayer = flyBoard.getCircuit().get(i);

                    if (optionalPlayer.isEmpty())
                        continue;

                    if (optionalPlayer.get().getNickname().equals(nickname)) {
                        flyBoard.getCircuit().set(i, Optional.empty());
                        return;
                    }
                }
                Event event = new LeavePlayerEvent(nickname);
                game.addEvent(event);
                if (flyBoard.getScoreBoard().isEmpty()) {
                    setEndGame(idGame);
                }
            }

            if (watingPlayers.isEmpty()) {
                Set<String> nicknames = game.getClients().keySet();
                String leader = game.getFlyboard().getScoreBoard().getFirst().getNickname();
                for (String n : nicknames) {
                    if (n.equals(leader)) {
                        Event event = new SetStateEvent(n, GameState.YOU_CAN_DRAW_CARD);
                        game.addEvent(event);
                    } else {
                        Event event = new SetStateEvent(n, GameState.DRAW_CARD);
                        game.addEvent(event);
                    }

                }
            }
        }
    }

    /**
     * Skips the effect of the currently played SldAdvCard for a specific player if it's their turn and the card matches.
     * The method handles different card types by invoking their respective "skip effect" or "set next player/planet" logic.
     * If the card is not implemented for skipping effects, an error is logged.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player attempting to skip the effect.
     * @param idCard The ID of the card whose effect is to be skipped.
     */
    public void skipEffect(int idGame, String nickname, int idCard) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        SldAdvCard card = flyBoard.getPlayedCard();

        if (idCard == card.getId() && nickname.equals(card.getActualPlayer().getNickname())) {
            Logger.debug("Salto effetto carta " + idCard);
            switch (card) {
                case SldAbandonedShip abandonedShip -> {
                    abandonedShip.applyEffect(nickname, false, null);
                    card.setNextPlayer();
                }

                case SldAbandonedStation abandonedStation -> {
                    abandonedStation.setNextPlayer();
                }

                case SldSmugglers sldSmugglers -> {
                    sldSmugglers.setNextPlayer();
                }

                case SldPlanets sldPlanets -> {
                    sldPlanets.setNextPlanet();
                }

                case SldSlavers sldSlavers -> {
                    sldSlavers.skipEffect();
                }

                default -> Logger.error("carta non implementata - per salto effetto");
            }
            //if(!(card instanceof SldPlanets))
            //card.setNextPlayer();
        }
    }

    /**
     * Applies the effect of the currently played SldAdvCard for a specific player.
     * This method handles the application of effects for "SldAbandonedStation" and "SldSlavers" cards.
     * If the card's effect is not applicable through this method, an error is logged.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player for whom the effect is being applied.
     */
    public void applyEffect(int idGame, String nickname) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        SldAdvCard card = game.getFlyboard().getPlayedCard();
        Player player = game.getFlyboard().getPlayerByUsername(nickname);

        switch (card) {
            case SldAbandonedStation abandonedStation -> abandonedStation.applyEffect(player, true);

            case SldSlavers sldSlavers -> {
                sldSlavers.takeCredits();
            }
            default -> Logger.error("effetto carta non applicabile");
        }
    }

    /**
     * Removes crew members from the game board based on the effect of the currently played SldAdvCard.
     * This method handles crew removal for "SldAbandonedShip," "SldSlavers," and "SldCombatZone" cards.
     * If the card's effect does not involve crew removal, an error is logged.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player whose crew members are being removed.
     * @param cordToRemove A list of coordinates indicating the positions of the crew members to remove.
     */
    public void removeCrew(int idGame, String nickname, List<Cordinate> cordToRemove) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();

        SldAdvCard card = flyBoard.getPlayedCard();

        switch (card) {
            case SldAbandonedShip abandonedShip -> {
                abandonedShip.applyEffect(nickname, true, cordToRemove);

            }

            case SldSlavers sldSlavers -> {
                sldSlavers.removeCrew(nickname, cordToRemove);
            }

            case SldCombatZone combatZone -> {
                combatZone.removeCrew(nickname, cordToRemove);
            }
            default -> Logger.error("Effetto carta non consentito");
        }
    }

    /**
     * Adds a good of a specific type to a component on the game board.
     * This method also broadcasts events to update all clients about the added good,
     * removes the good from pending status for the player, and changes the player's card state.
     * If the good cannot be added to the specified component due to an {@code IncorrectShipBoardException},
     * an error message is sent to the client, and game crash handling is initiated if further errors occur.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player adding the good.
     * @param idComp The ID of the component to which the good is being added.
     * @param type The type of good to add.
     */
    public void addGood(int idGame, String nickname, int idComp, GoodType type) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        try {
            Logger.debug("Adding good " + type + " to " + idComp);
            game.getFlyboard().getComponentById(idComp).addGood(type);

            Event event = new AddGoodEvent(null, idComp, type);
            game.addEvent(event);

            Event addPending = new RemovePendingGoodEvent(nickname, type);
            game.addEvent(addPending);

            Event changeState = new SetCardStateEvent(nickname, CardState.GOODS_PLACEMENT);
            game.addEvent(changeState);

        } catch (IncorrectShipBoardException e) {
            try {
                game.getClients().get(nickname).genericChoiceError("Cannot add e good in depop with id " + idComp);
            } catch (Exception ex) {
                handleGameCrash(ex, nickname, idGame);
            }
        }
    }

    /**
     * Removes a good of a specific type from a component on the game board.
     * This method broadcasts events to update all clients about the removed good,
     * adds the good back to pending status for the player, and changes the player's card state.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player removing the good.
     * @param idComp The ID of the component from which the good is being removed.
     * @param type The type of good to remove.
     */
    public void removeGood(int idGame, String nickname, int idComp, GoodType type) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        game.getFlyboard().getComponentById(idComp).removeGood(type);

        Event event = new RemoveGoodEvent(null, idComp, type);
        game.addEvent(event);

        Event addPending = new AddPendingGoodEvent(nickname, type);
        game.addEvent(addPending);

        Event changeState = new SetCardStateEvent(nickname, CardState.GOODS_PLACEMENT);
        game.addEvent(changeState);
    }

    /**
     * Handles a player's action of landing on a planet during a "SldPlanets" card effect.
     * Logs the player's choice and updates all clients about the player's new position.
     * If all players have landed or all planets have been landed on, the card's effect is applied.
     * Otherwise, the card's effect moves to the next player.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player landing on a planet.
     * @param choice The chosen planet number to land on, or -1 if no planet is chosen.
     */
    public void landOnPlanet(int idGame, String nickname, int choice) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        Player player = game.getFlyboard().getPlayerByUsername(nickname);
        Logger.debug("player " + nickname + " land on planet number " + choice);
        SldAdvCard card = game.getFlyboard().getPlayedCard();
        card.land(player, choice);
        int passedPlayers = card.getPassedPlayers();

        VirtualClient c = game.getClients().get(nickname);

        if (choice != -1) {
            for (VirtualClient client : game.getClients().values()) {
                try {
                    client.setPlayerOnPlanet(nickname, choice);
                } catch (Exception e) {
                    handleGameCrash(e, nickname, idGame);
                }
            }
        }
//        if (choice != -1) {
//            try {
//                c.setState(GameState.GOODS_PLACEMENT);
//            } catch (Exception e) {
//                handleGameCrash(e, nickname, idGame);
//            }
//        }
        Logger.debug("numero giocatori passati " + passedPlayers);
        if (passedPlayers == game.getFlyboard().getScoreBoard().size() || card.getLandedPlayers().size() == card.getPlanets().size()) {
            card.applyEffect();
        } else {
            card.setNextPlayer();
        }
//        if (game.getFlyboard().getScoreBoard().getLast().equals(player) && choice == -1 && card.allPlayersPlacedGoods()) {
//            card.applyEffect();
//        }

    }

    /**
     * Activates the double drills effect for a player based on the currently played SldAdvCard.
     * This method handles the specific logic for "SldSmugglers," "SldPirates," "SldSlavers," and "SldCombatZone" cards,
     * applying their respective effects related to drill coordinates.
     * Logs an error if the played card is not valid for this effect.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player activating the double drills.
     * @param drillCordinates A list of coordinates representing the drill positions.
     */
    public void activateDoubleDrills(int idGame, String nickname, List<Cordinate> drillCordinates) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();

        SldAdvCard card = game.getFlyboard().getPlayedCard();

        switch (card) {
            case SldSmugglers sldSmugglers -> {
                Logger.debug(nickname + drillCordinates);
                Player player = game.getFlyboard().getPlayerByUsername(nickname);
                sldSmugglers.applyEffect(player, drillCordinates);
            }

            case SldPirates sldPirates -> {
                Logger.debug(nickname + drillCordinates);
                Player player = flyBoard.getPlayerByUsername(nickname);

                sldPirates.loadPower(player, drillCordinates);
            }

            case SldSlavers sldSlavers -> {
                Player player = flyBoard.getPlayerByUsername(nickname);
                sldSlavers.applyEffect(player, drillCordinates);
            }

            case SldCombatZone combatZone -> {
                Player player = flyBoard.getPlayerByUsername(nickname);
                combatZone.setDrills(player, drillCordinates);
            }

            default -> Logger.error("effetto carta non consentito");
        }
    }


//    private void stealGoods(GameServer game, Player player, int numberStolenGoods) {
//        Logger.debug(player.getNickname() + "steal goods");
//        Map<Integer, List<GoodType>> stolenGoods = player.getShipBoard().stoleGood(numberStolenGoods);
//
//        for (Integer idComp : stolenGoods.keySet()) {
//            for (GoodType type : stolenGoods.get(idComp)) {
//                for (VirtualClient client : game.getClients().values()) {
//                    try {
//                        client.removeGood(idComp, type);
//                    } catch (Exception e) {
//                        handleGameCrash(e, nickname, idGame);
//                    }
//                }
//            }
//        }
//    }

    /**
     * Sets the result of a dice roll and applies its effect based on the currently played SldAdvCard.
     * This method handles the logic for "SldMeteorSwarm," "SldPirates," and "SldCombatZone" cards.
     * For "SldMeteorSwarm," it calculates the hit value, applies it to players, and determines the next meteor.
     * For "SldPirates" and "SldCombatZone," it calculates cannon hit events and broadcasts them to relevant players.
     * Logs an error if no effect is defined for the given card for a roll result.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player who rolled the dice.
     * @param first The result of the first die.
     * @param second The result of the second die.
     */
    public void setRollResult(int idGame, String nickname, int first, int second) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        SldAdvCard card = game.getFlyboard().getPlayedCard();

        switch (card) {
            case SldMeteorSwarm meteorSwarm -> {
                Logger.info("e' uscito " + first + " e " + second);

                Meteor meteor = meteorSwarm.getActualMeteor();
                meteor.setNumber(first + second);
                Direction direction = meteor.getDirection();
                MeteorType type = meteor.getType();

                for (Player player : game.getFlyboard().getPlayers())
                    meteor.hit(game, player, first + second);

                if (meteor.getNickHit().isEmpty())
                    meteorSwarm.setNextMeteor();
            }

            case SldPirates sldPirates -> {
                CannonPenalty cannon = sldPirates.getActualCannon();
                cannon.setNumber(first + second);
                Direction direction = cannon.getDirection();
                CannonType type = cannon.getCannonType();
                List<String> nicknameToHit = sldPirates.getPenaltyPlayers().stream().map(Player::getNickname).toList();

                for (Player player : sldPirates.getPenaltyPlayers()) {
                    String nick = player.getNickname();
                    Event event = new CannonHitEvent(nick, type, direction, first + second);
                    game.addEvent(event);
                }
            }

            case SldCombatZone combatZone -> {
                CannonPenalty cannon = combatZone.getActualCannon();
                cannon.setNumber(first + second);
                Direction direction = cannon.getDirection();
                CannonType type = cannon.getCannonType();

                Player player = combatZone.getActualPlayer();

                Event event = new CannonHitEvent(nickname, type, direction, first + second);
                game.addEvent(event);
            }

            default -> Logger.error("No effect for setRollResult");

        }
    }

//    public void removeBattery(int idGame, String nickname, int quantity) {
//        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
//
//        ShipBoard shipBoard = game.getFlyboard().getPlayerByUsername(nickname).getShipBoard();
//        if (shipBoard.getQuantBatteries() < quantity) {
//            throw new IncorrectShipBoardException("not enough batteries");
//        }
//
//        List<Integer> removedId = shipBoard.removeEnergy(quantity);
//
//        for (VirtualClient client : game.getClients().values()) {
//            try {
//                client.removeBattery(removedId);
//            } catch (Exception e) {
//                handleGameCrash(e, nickname, idGame);
//            }
//        }
//    }

    /**
     * Advances the state of a meteor effect on the game board, typically in response to a player's action (e.g., repairing damage).
     * This method is specifically designed to work with {@code SldMeteorSwarm} cards.
     * Logs an error if the played card is not a {@code SldMeteorSwarm}.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player interacting with the meteor.
     * @param destroyed A boolean indicating if a component was destroyed.
     * @param energy A boolean indicating if energy was used.
     */
    public void advanceMeteor(int idGame, String nickname, boolean destroyed, boolean energy) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        SldAdvCard card = game.getFlyboard().getPlayedCard();

        switch (card) {
            case SldMeteorSwarm meteorSwarm -> {
                meteorSwarm.setNextMeteor(nickname, destroyed, energy);
            }

            default -> Logger.error("Effect not taken");
        }
    }

    /**
     * Advances the state of a cannon effect on the game board, typically in response to a player's action (e.g., repairing damage).
     * This method is specifically designed to work with {@code SldPirates} and {@code SldCombatZone} cards.
     * Logs an error if the played card is not a {@code SldPirates} or {@code SldCombatZone}.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player interacting with the cannon.
     * @param destroyed A boolean indicating if a component was destroyed.
     * @param energy A boolean indicating if energy was used.
     */
    public void advanceCannon(int idGame, String nickname, boolean destroyed, boolean energy) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        SldAdvCard card = game.getFlyboard().getPlayedCard();

        switch (card) {
            case SldPirates pirates -> {
                pirates.setNextCannon(nickname, destroyed, energy);
            }

            case SldCombatZone combatZone -> {
                combatZone.setNextCannon(nickname, destroyed, energy);
            }

            default -> Logger.error("Effect not taken");
        }
    }

    /**
     * Starts the hourglass timer for a game and notifies all clients that the hourglass has started.
     * This typically indicates a timed phase of the game has begun.
     * If a client crashes during notification, the game crash is handled.
     *
     * @param idGame The ID of the game.
     */
    public void startHourglass(int idGame) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard fly = game.getFlyboard();
        fly.startHourglass(idGame);
        for (String nickname : game.getClients().keySet()) {
            try {
                game.getClients().get(nickname).startedHourglass(idGame);
            } catch (Exception e) {
                handleGameCrash(e, nickname, idGame);
            }
        }
    }

    /**
     * Removes a component from a player's ship board in a game.
     * If no card is currently played, the removal is directly broadcast to all other clients.
     * If a card is played, a {@code RemoveComponentEvent} is added to the game's event queue.
     * Throws an {@code IncorrectFlyBoardException} if the provided nickname does not correspond to a player in the game.
     * If a client crashes during component removal notification, the game crash is handled.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player whose component is being removed.
     * @param cord The coordinates of the component to remove.
     * @throws IncorrectFlyBoardException if the nickname does not belong to a player in the game.
     */
    public void removeComponent(int idGame, String nickname, Cordinate cord) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();

        if (!flyBoard.getNicknameList().contains(nickname)) {
            throw new IncorrectFlyBoardException("Not player with this nickname");
        }

        ShipBoard shipBoard = flyBoard.getPlayerByUsername(nickname).getShipBoard();
        shipBoard.removeComponent(cord);

        Map<String, VirtualClient> clients = game.getClients();

        Logger.info("RemoveComponentFromAllExcept " + nickname);

        if (flyBoard.getPlayedCard() == null) {
            for (String nick : clients.keySet()) {
                try {
                    if (!nick.equals(nickname)) {
                        clients.get(nick).removeComponent(nickname, cord);
                    }
                } catch (Exception e) {
                    handleGameCrash(e, nickname, idGame);
                }
            }
        } else {
            Event event = new RemoveComponentEvent(nickname, cord);
            game.addEvent(event);
        }
    }

    /**
     * Removes a component from a player's ship board in a game and broadcasts this removal to all clients.
     * Throws an {@code IncorrectFlyBoardException} if the provided nickname does not correspond to a player in the game.
     * If a client crashes during component removal notification, the game crash is handled.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player whose component is being removed.
     * @param cord The coordinates of the component to remove.
     * @throws IncorrectFlyBoardException if the nickname does not belong to a player in the game.
     */
    public void removeComponentToAll(int idGame, String nickname, Cordinate cord) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();

        if (!flyBoard.getNicknameList().contains(nickname)) {
            throw new IncorrectFlyBoardException("Not player with this nickname");
        }

        ShipBoard shipBoard = flyBoard.getPlayerByUsername(nickname).getShipBoard();
        shipBoard.removeComponent(cord);

        Logger.info("RemoveComponentFromAll");

        Map<String, VirtualClient> clients = game.getClients();
        for (String nick : clients.keySet()) {
            try {
                clients.get(nick).removeComponent(nickname, cord);
            } catch (Exception e) {
                handleGameCrash(e, nickname, idGame);
            }
        }
    }

    /**
     * Adds crew members to a player's ship board in a game.
     * It validates if the crew can be added to the specified components and if the total number of certain guest types is valid.
     * If the addition is valid, the crew is added to the components and {@code AddCrewEvent}s are broadcast.
     * If invalid, the player's state is reset to {@code ADD_CREW} to allow them to re-attempt.
     * After processing, the player is removed from the list of players needing to add crew.
     * If all players have added their crew, the game transitions to the card drawing phase,
     * with the leader allowed to draw a card and others waiting for a card draw.
     *
     * @param idGame The ID of the game.
     * @param nickname The nickname of the player adding crew.
     * @param addedCrew A map where keys are coordinates and values are lists of {@code GuestType} to add at those coordinates.
     */
    public void addCrew(int idGame, String nickname, Map<Cordinate, List<GuestType>> addedCrew) {
        System.out.println(nickname + " " + addedCrew);
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();

        Player player = flyBoard.getPlayerByUsername(nickname);
        if (!flyBoard.getAddCrewPlayers().contains(player)) {
            return;
        }
        ShipBoard ship = flyBoard.getPlayerByUsername(nickname).getShipBoard();
        List<GuestType> flatInserted = new ArrayList<>();

        boolean valid = true;
        for (Cordinate cord : addedCrew.keySet()) {
            for (GuestType type : addedCrew.get(cord)) {
                int idComp = ship.getOptComponentByCord(cord).get().getId();
                Component comp = flyBoard.getComponentById(idComp);

                if (!comp.canAddGuest(type))
                    valid = false;

                flatInserted.add(type);
            }
        }

        if (Collections.frequency(flatInserted, GuestType.BROWN) > 1)
            valid = false;

        if (Collections.frequency(flatInserted, GuestType.PURPLE) > 1)
            valid = false;

        if (valid) {
            for (Cordinate cord : addedCrew.keySet()) {
                for (GuestType type : addedCrew.get(cord)) {
                    int idComp = ship.getOptComponentByCord(cord).get().getId();
                    Component comp = flyBoard.getComponentById(idComp);
                    comp.addGuest(type);

                    Event event = new AddCrewEvent(nickname, cord, type);
                    game.addEvent(event);
                }
            }
        } else {
            Event event1 = new SetStateEvent(nickname, GameState.IDLE);
            game.addEvent(event1);

            Event event = new SetStateEvent(nickname, GameState.ADD_CREW);
            game.addEvent(event);
        }


        flyBoard.getAddCrewPlayers().remove(flyBoard.getPlayerByUsername(nickname));

        if (flyBoard.getAddCrewPlayers().isEmpty()) {
            flyBoard.buildAdventureDeck();
            String nickLeader = flyBoard.getScoreBoard().getFirst().getNickname();

            for (String n : game.getClients().keySet()) {
                if (n.equals(nickLeader)) {
                    Event event1 = new SetStateEvent(n, GameState.YOU_CAN_DRAW_CARD);
                    game.addEvent(event1);
                } else {
                    Event event1 = new SetStateEvent(n, GameState.DRAW_CARD);
                    game.addEvent(event1);
                }
            }
        }
    }

    /**
     * Handles a game crash scenario.
     * Logs the crash details, broadcasts a {@code CrashEvent} to all players in the affected game,
     * and then removes the ongoing game from the game manager.
     * If the game has already been removed, a warning is logged.
     *
     * @param e The exception that caused the crash.
     * @param nickname The nickname of the client involved in the crash.
     * @param idGame The ID of the game where the crash occurred.
     */
    public void handleGameCrash(Exception e, String nickname, int idGame) {
        Logger.error("Client " + nickname + "in game #" + idGame + " crashed with exception " + e);

        // send crash message to all players of the game
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        if (game == null) {
            Logger.warning("Game #" + idGame + " already deleted.");
            return;
        }
        Event crashEvent = new CrashEvent(nickname);
        game.addEvent(crashEvent);

        // delete all game info
        GameManager.getInstance().removeOnGoingGame(idGame);
    }
}