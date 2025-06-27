package org.mio.progettoingsoft;

import org.mio.progettoingsoft.advCards.sealed.*;
import org.mio.progettoingsoft.model.events.*;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.server.ServerController;
import org.mio.progettoingsoft.utils.Logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * The {@code GameController} is responsible for coordinating the game logic,
 * processing events, updating the game model ({@link FlyBoard}), and communicating
 * state changes and events back to the connected clients.
 * It acts as the central hub for game flow and interactions.
 */
public class GameController {
    private final GameServer game;
    private final BlockingQueue<Event> eventsQueue;
    private final List<String> doubledNicknames;

    /**
     * Constructs a new {@code GameController} in normal operating mode.
     * Starts a daemon thread responsible for sending messages to clients from the event queue.
     *
     * @param game        The {@link GameServer} instance that this controller manages.
     * @param eventsQueue The {@link BlockingQueue} of {@link Event} objects to process and send.
     */
    public GameController(GameServer game, BlockingQueue<Event> eventsQueue) {
        this.game = game;
        this.eventsQueue = eventsQueue;
        this.doubledNicknames = new ArrayList<>();

        Thread thread = new Thread(() -> clientComunication());
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Constructs a new {@code GameController} with an option for testing mode.
     *
     * @param game        The {@link GameServer} instance that this controller manages.
     * @param eventsQueue The {@link BlockingQueue} of {@link Event} objects to process.
     * @param testing     A boolean indicating if the controller is in testing mode.
     */
    public GameController(GameServer game, BlockingQueue<Event> eventsQueue, boolean testing) {
        this.game = game;
        this.eventsQueue = eventsQueue;
        this.doubledNicknames = new ArrayList<>();

        if (!testing) {
            Thread thread = new Thread(this::clientComunication);
            thread.setDaemon(true);
            thread.start();
        } else {
            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        eventsQueue.take();

                        synchronized (eventsQueue) {
                            if (eventsQueue.isEmpty())
                                game.getEventsQueue().notifyAll();
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
        }

    }

    /**
     * The main loop for the client communication thread.
     * It continuously takes {@link Event} objects from the {@code eventsQueue}
     * and sends them to the appropriate clients. After sending, it notifies
     * any waiting threads if the queue becomes empty.
     */
    private void clientComunication() {
        while (true) {
            try {
                Event event = eventsQueue.take();

                event.send(game.getClients());

                synchronized (eventsQueue) {
                    if (eventsQueue.isEmpty())
                        game.getEventsQueue().notifyAll();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Registers a {@link PropertyChangeListener} with the game's {@link FlyBoard}.
     * This listener captures events fired by the FlyBoard (e.g., player movement,
     * battery removal, credit changes, good removal, player leaving) and converts
     * them into generic {@link Event} objects to be added to the {@code eventsQueue}.
     */
    public void registerListener() {
        Logger.debug("Chiamato registerListener");

        game.getFlyboard().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("movePlayer".equals(evt.getPropertyName())) {
                    Logger.debug("event movePlayer gestito");
                    MovePlayerEvent event = (MovePlayerEvent) evt.getNewValue();

                    game.addEvent(event);
                } else if ("removeBattery".equals(evt.getPropertyName())) {
                    Event event = (RemoveEnergyEvent) evt.getNewValue();
                    game.addEvent(event);
                } else if ("addCredits".equals(evt.getPropertyName())) {
                    Event event = (AddCreditsEvent) evt.getNewValue();
                    game.addEvent(event);
                } else if ("removeGood".equals(evt.getPropertyName())) {
                    Event event = (RemoveGoodEvent) evt.getNewValue();
                    game.addEvent(event);
                } else if ("leavePlayer".equals(evt.getPropertyName())) {
                    Event event = (LeavePlayerEvent) evt.getNewValue();
                    game.addEvent(event);
                } else if ("doubled".equals(evt.getPropertyName())) {
                    doubledNicknames.add((String) evt.getOldValue());
                }
            }
        });

        // listener per ogni Player : gestiscono modifiche ai credits

    }

    /**
     * Handles the completion of an hourglass turn.
     * It sends a {@code FINISH_HOURGLASS} or {@code FINISH_LAST_HOURGLASS} state update
     * to all clients, depending on the {@code activation} parameter (e.g., 3 for the last hourglass).
     * If an error occurs during communication, it delegates to {@link ServerController#handleGameCrash}.
     *
     * @param activation An integer indicating the activation count of hourglasses, used to determine if it's the last one.
     */
    public void finishHourglass(int activation) {
        if (!game.getFlyboard().isReadyToAdventure()) {
            for (String clientNickname : game.getClients().keySet()) {
                try {
                    VirtualClient client = game.getClients().get(clientNickname);
                    if (activation == 3) {
                        client.setState(GameState.FINISH_LAST_HOURGLASS);
                    } else {
                        client.setState(GameState.FINISH_HOURGLASS);
                    }
                } catch (Exception e) {
                    ServerController.getInstance().handleGameCrash(e, clientNickname, game.getIdGame());
                }
            }
        }
    }


    /**
     * Updates client states based on the current state of a played {@link SldAdvCard}.
     * This method is a central dispatcher for guiding clients through the card effects.
     * It sends specific {@link SetCardStateEvent} or {@link SetStateEvent} to relevant players.
     *
     * @param card The {@link SldAdvCard} whose state has changed.
     */
    public void update(SldAdvCard card) {
        Player player = card.getActualPlayer();
        if (player == null) {
            player = game.getFlyboard().getScoreBoard().getFirst();
        }
        String username = player.getNickname();

//        VirtualClient client = game.getClients().get(player.getNickname());
        CardState cardState = card.getState();
        Logger.debug("setto lo stato : " + cardState + " a " + player.getNickname());
        switch (cardState) {
//            case BUILDING_SHIP -> broadcast(new StartGameMessage(game.getIdGame()));
            case ENGINE_CHOICE -> {
                Event event = new SetCardStateEvent(player.getNickname(), CardState.ENGINE_CHOICE);
                game.addEvent(event);
            }

            case ACCEPTATION_CHOICE -> {
                Event event = new SetCardStateEvent(player.getNickname(), CardState.ACCEPTATION_CHOICE);
                game.addEvent(event);
            }

            case CREW_REMOVE_CHOICE -> {
                Event event = new SetCardStateEvent(player.getNickname(), CardState.CREW_REMOVE_CHOICE);
                game.addEvent(event);
            }

            case DRILL_CHOICE -> {
                Event event = new SetCardStateEvent(player.getNickname(), CardState.DRILL_CHOICE);
                game.addEvent(event);
            }

            case PLANET_CHOICE -> {
                Event event = new SetCardStateEvent(player.getNickname(), CardState.PLANET_CHOICE);
                game.addEvent(event);
            }
            case COMPARING -> {
                Event event = new SetCardStateEvent(player.getNickname(), CardState.COMPARING);
                game.addEvent(event);
            }

            case DICE_ROLL -> {
                switch (card) {
                    case SldCombatZone combatZone -> {
                        Map<String, VirtualClient> clients = game.getClients();

                        for (String nick : clients.keySet()) {
                            if (nick.equals(combatZone.getActualPlayer().getNickname())) {
                                Event event = new SetCardStateEvent(nick, CardState.DICE_ROLL);
                                game.addEvent(event);
                            } else {
                                Event event = new SetCardStateEvent(nick, CardState.WAITING_ROLL);
                                game.addEvent(event);
                            }
                        }
                    }

                    case SldMeteorSwarm meteorSwarm -> {

                        String leaderNickname = game.getFlyboard().getScoreBoard().getFirst().getNickname();
                        Map<String, VirtualClient> clients = game.getClients();

                        for (String nick : clients.keySet()) {
                            if (nick.equals(leaderNickname)) {
                                Event event = new SetCardStateEvent(nick, CardState.DICE_ROLL);
                                game.addEvent(event);
                            } else {
                                Event event = new SetCardStateEvent(nick, CardState.WAITING_ROLL);
                                game.addEvent(event);
                            }
                        }
                    }

                    case SldPirates pirates -> {
                        String firstPlayer = pirates.getPenaltyPlayers().getFirst().getNickname();
                        Map<String, VirtualClient> clients = game.getClients();

                        for (String nick : clients.keySet()) {
                            if (nick.equals(firstPlayer)) {
                                Event event = new SetCardStateEvent(nick, CardState.DICE_ROLL);
                                game.addEvent(event);
                            } else {
                                Event event = new SetCardStateEvent(nick, CardState.WAITING_ROLL);
                                game.addEvent(event);
                            }
                        }
                    }

                    default -> Logger.error("Not expected card!");
                }
            }

            case GOODS_PLACEMENT -> {
                Event event = new SetCardStateEvent(player.getNickname(), CardState.GOODS_PLACEMENT);
                game.addEvent(event);
            }

            case FINALIZED -> {

                Map<String, VirtualClient> clients = game.getClients();
                FlyBoard flyBoard = game.getFlyboard();

                for (int i = 0; i < flyBoard.getScoreBoard().size(); i++) {
                    Player p = flyBoard.getScoreBoard().get(i);

                    if (p.getShipBoard().getNumberHumans() == 0) {
                        flyBoard.leavePlayer(p);
//                        Event event = new SetStateEvent(p.getNickname(), GameState.REMOVED_FROM_FLYBOARD);
//                        game.addEvent(event);
                    }
                }

                for(String nick : doubledNicknames){
                    flyBoard.leavePlayer(flyBoard.getPlayerByUsername(nick));
                }

                flyBoard.refreshWaitingPlayers();

                List<Player> score = flyBoard.getScoreBoard();
                for (Player p : score) {
                    String n = p.getNickname();

                    Event eve = new SetCardStateEvent(n, CardState.ASK_LEAVE);
                    game.addEvent(eve);
                }
                if (flyBoard.getScoreBoard().isEmpty()) {
                    ServerController.getInstance().setEndGame(game.getIdGame());
                }
            }

            case STARDUST_END -> {
                for (String n : game.getClients().keySet()) {
                    Event eve = new SetCardStateEvent(n, CardState.STARDUST_END);
                    game.addEvent(eve);
                }
            }

            case EPIDEMIC_END -> {
                for (String n : game.getClients().keySet()) {
                    Event eve = new SetCardStateEvent(n, CardState.EPIDEMIC_END);
                    game.addEvent(eve);
                }
            }

            default -> {
            }
        }
    }
}