package org.mio.progettoingsoft.network.server.socket;

import org.mio.progettoingsoft.GameManager;
import org.mio.progettoingsoft.network.messages.*;
import org.mio.progettoingsoft.network.server.ServerController;
import org.mio.progettoingsoft.utils.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handles incoming messages received by the server. This class acts as a consumer
 * of a {@link BlockingQueue} of {@link Message} objects, processing each message
 * by delegating its handling to the {@link ServerController}.
 * It uses an {@link ExecutorService} to process messages asynchronously,
 * allowing the server to handle multiple client requests concurrently.
 */
public class ServerMessageHandler implements Runnable {
    private final BlockingQueue<Message> receivedMessages;
    private final ServerController serverController;
    private ExecutorService executors = Executors.newFixedThreadPool(4);

    /**
     * Constructs a new {@code ServerMessageHandler}.
     * @param serverController The {@link ServerController} instance to which message processing will be delegated.
     * @param receivedMessages The {@link BlockingQueue} from which messages will be consumed.
     */
    public ServerMessageHandler(ServerController serverController, BlockingQueue<Message> receivedMessages){
        this.serverController = serverController;
        this.receivedMessages = receivedMessages;
    }

    /**
     * The main execution loop for the message handler.
     * This method continuously takes messages from the {@code receivedMessages} queue
     * and submits them to the {@code executors} for asynchronous processing by the {@link ServerController}.
     * If the thread is interrupted while waiting for a message, it logs the interruption
     * and attempts to gracefully shut down the executor service.
     */
    @Override
    public void run(){
        while (true){
            try {
                Message message = receivedMessages.take();

                if (message.getGameId() != -1 && !GameManager.getInstance().getOngoingGames().containsKey(message.getGameId())) {
                    Logger.error("Game not found.");
                    executors.submit(() -> {
                        serverController.handleGameCrash(new Exception("Game crashed"), message.getNickname(), message.getGameId());
                    });
                    return;
                }

                switch (message){
                    case NicknameMessage nicknameMessage -> {
                        executors.submit(() -> serverController.handleNickname(nicknameMessage.getIdClient(), nicknameMessage.getNickname()));
                    }

                    case GameInfoMessage gameInfoMessage -> {
                        serverController.handleGameInfo(gameInfoMessage.getGameInfo(), gameInfoMessage.getNickname());
                    }

                    case ComponentMessage componentMessage -> {
                        switch (componentMessage.getAction()) {
                            case ADD -> {
                                serverController.addComponent(componentMessage.getGameId(), componentMessage.getNickname(),
                                        componentMessage.getIdComp(), componentMessage.getCordinate(), componentMessage.getRotations());
                            }

                            case REMOVE -> {
                                if (componentMessage.isToAllClient()) {
                                    serverController.removeComponentToAll(componentMessage.getGameId(), message.getNickname(), componentMessage.getCordinate());
                                } else {
                                    serverController.removeComponent(componentMessage.getGameId(), message.getNickname(), componentMessage.getCordinate());
                                }
                            }

                            case DISCARD -> {
                                serverController.discardComponent(componentMessage.getGameId(), componentMessage.getIdComp());
                            }

                            case COVERED -> {
                                serverController.getCoveredComponent(componentMessage.getGameId(), componentMessage.getNickname());
                            }

                            case DRAW_UNCOVERED -> {
                                serverController.drawUncovered(componentMessage.getGameId(), componentMessage.getNickname(), componentMessage.getIdComp());
                            }
                        }
                    }

                    case DeckMessage deckMessage -> {
                        switch (deckMessage.getAction()){
                            case BOOK -> serverController.bookDeck(deckMessage.getGameId(), deckMessage.getNickname(), deckMessage.getDeckNumber());
                            case UNBOOK -> serverController.freeDeck(deckMessage.getGameId(), deckMessage.getNickname(), deckMessage.getDeckNumber());
                        }
                    }

                    case BuildShipMessage buildShipMessage -> {
                        serverController.takeBuild(buildShipMessage.getGameId(), buildShipMessage.getNickname());
                    }

                    case EndBuildMessage endBuildMessage -> {
                        serverController.endBuild(endBuildMessage.getGameId(), endBuildMessage.getNickname());
                    }
                    case ChoosePlacementMessage choosePlacementMessage -> {
                        if(choosePlacementMessage.getPlace() == -1) {
                            serverController.getStartingPosition(choosePlacementMessage.getGameId(), choosePlacementMessage.getNickname());
                        } else {
                            serverController.choosePlace(choosePlacementMessage.getGameId(), choosePlacementMessage.getNickname(), choosePlacementMessage.getPlace());
                        }
                    }

                    case EndValidationMessage endValidationMessage -> {
                        serverController.endValidation(endValidationMessage.getGameId(), endValidationMessage.getNickname(), endValidationMessage.isUsedBattery());
                    }

                    case DoubleEngineMessage doubleEngineMessage -> {
                        serverController.activateDoubleEngine(doubleEngineMessage.getGameId(), doubleEngineMessage.getNickname(), doubleEngineMessage.getNumber());
                    }

                    case LeaveMessage leaveMessage -> {
                        serverController.leaveFlight(leaveMessage.getGameId(), leaveMessage.getNickname(), leaveMessage.isLeave());
                    }
                    case DrawCardMessage drawCardMessage -> {
                        serverController.drawCard(drawCardMessage.getGameId(), drawCardMessage.getNickname());
                    }

                    case SkipEffectMessage skipEffectMessage ->
                        serverController.skipEffect(skipEffectMessage.getGameId(), skipEffectMessage.getNickname(), skipEffectMessage.getIdCard());

                    case ApplyEffectMessage applyEffectMessage ->
                        serverController.applyEffect(applyEffectMessage.getGameId(), applyEffectMessage.getNickname());

                    case CrewRemoveMessage crewRemoveMessage ->
                        serverController.removeCrew(crewRemoveMessage.getGameId(), crewRemoveMessage.getNickname(), crewRemoveMessage.getCordinates());

                    case GoodMessage goodMessage ->{
                        switch (goodMessage.getTypeMessage()){
                            case ADD_GOOD -> serverController.addGood(goodMessage.getGameId(), goodMessage.getNickname(), goodMessage.getIdComp(), goodMessage.getGoodType());
                            case REMOVE_GOOD -> serverController.removeGood(goodMessage.getGameId(), goodMessage.getNickname(), goodMessage.getIdComp(), goodMessage.getGoodType());
                        }
                    }

                    case DoubleDrillMessage doubleDrillMessage ->{
                        serverController.activateDoubleDrills(doubleDrillMessage.getGameId(), doubleDrillMessage.getNickname(), doubleDrillMessage.getDrillCordinates());
                    }

                    case LandOnPlanetMessage landOnPlanetMessage -> {
                        serverController.landOnPlanet(landOnPlanetMessage.getGameId(), landOnPlanetMessage.getNickname(), landOnPlanetMessage.getChoice());
                    }

                    case RollDiceMessage rollDiceMessage -> {
                        serverController.setRollResult(rollDiceMessage.getGameId(), rollDiceMessage.getNickname(), rollDiceMessage.getFirst(), rollDiceMessage.getSecond());
                    }

                    case AdvanceMeteorMessage advanceMeteorMessage -> {
                        serverController.advanceMeteor(advanceMeteorMessage.getGameId(), advanceMeteorMessage.getNickname(), advanceMeteorMessage.isDestroyed(), advanceMeteorMessage.isEnergy());
                    }

                    case AdvanceCannonMessage advanceCannonMessage -> {
                        serverController.advanceCannon(advanceCannonMessage.getGameId(), advanceCannonMessage.getNickname(), advanceCannonMessage.isDestroyed(), advanceCannonMessage.isEnergy());
                    }
                    case StartHourglassMessage startHourglassMessage -> {
                        serverController.startHourglass(startHourglassMessage.getGameId());
                    }

                    case AddCrewMessage addCrewMessage -> {
                        serverController.addCrew(addCrewMessage.getGameId(), message.getNickname(), addCrewMessage.getCrewAdded());
                    }

                    default -> {
                        Logger.error("Message not recognized on server: " + message);
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
