package org.mio.progettoingsoft;

import org.mio.progettoingsoft.advCards.sealed.CardState;
import org.mio.progettoingsoft.advCards.sealed.SldAdvCard;
import org.mio.progettoingsoft.model.events.*;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.utils.Logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class GameController {
    private final GameServer game;
    private final BlockingQueue<Event> eventsQueue;

    public GameController(GameServer game, BlockingQueue<Event> eventsQueue) {
        this.game = game;
        this.eventsQueue = eventsQueue;

        Thread thread = new Thread(() -> clientComunication());
        thread.setDaemon(true);
        thread.start();

    }

    public GameController(GameServer game, BlockingQueue<Event> eventsQueue, boolean testing) {
        this.game = game;
        this.eventsQueue = eventsQueue;

        if (!testing) {
            Thread thread = new Thread(() -> clientComunication());
            thread.setDaemon(true);
            thread.start();
        }
        else{
            Thread thread = new Thread(() -> {
                while (true){
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

    private void clientComunication(){
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

    public void registerListener(){
        Logger.debug("Chiamato registerListener");

        game.getFlyboard().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("movePlayer".equals(evt.getPropertyName())){
                    Logger.debug("event movePlayer gestito");
                    MovePlayerEvent event = (MovePlayerEvent) evt.getNewValue();

                    game.addEvent(event);
                }
                else if ("removeBattery".equals(evt.getPropertyName())){
                    Event event = (RemoveEnergyEvent) evt.getNewValue();
                    game.addEvent(event);
                }
                else if ("addCredits".equals(evt.getPropertyName())){
                    Event event = (AddCreditsEvent) evt.getNewValue();
                    game.addEvent(event);
                }
                else if ("removeGood".equals(evt.getPropertyName())){
                    Event event = (RemoveGoodEvent) evt.getNewValue();
                    game.addEvent(event);
                }
            }
        });

        // listener per ogni Player : gestiscono modifiche ai credits

    }

    public void finishHourglass(int numeroAttivazione) {
        List<VirtualClient> clients =game.getClients().values().stream().toList();
        if(!game.getFlyboard().isReadyToAdventure()) {
            for (VirtualClient client : clients) {
                try {
                    if (numeroAttivazione == 3) {
                        client.setState(GameState.FINISH_LAST_HOURGLASS);
                    } else {
                        client.setState(GameState.FINISH_HOURGLASS);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    public void update(SldAdvCard card){
        Player player = card.getActualPlayer();
        String username = player.getNickname();

//        VirtualClient client = game.getClients().get(player.getNickname());
        CardState cardState = card.getState();
        Logger.debug("setto lo stato : " + cardState + " a " + player.getNickname());
        switch (cardState){
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
                String leaderNickname = game.getFlyboard().getScoreBoard().getFirst().getNickname();
                Map<String, VirtualClient> clients = game.getClients();

                for (String nick : clients.keySet() ){
                    if (nick.equals(leaderNickname)){
                        Event event = new SetCardStateEvent(nick, CardState.DICE_ROLL);
                        game.addEvent(event);
                    }
                    else{
                        Event event = new SetCardStateEvent(nick, CardState.WAITING_ROLL);
                        game.addEvent(event);
                    }
                }
            }

            case GOODS_PLACEMENT -> {
                Event event = new SetCardStateEvent(player.getNickname(), CardState.GOODS_PLACEMENT);
                game.addEvent(event);
            }

            case FINALIZED -> {
                Map<String,VirtualClient> clients = game.getClients();

                Set<String> nicknames = clients.keySet();
                String leader = game.getFlyboard().getScoreBoard().getFirst().getNickname();
                for (String n : nicknames) {
                    if (n.equals(leader)) {
                        Event event = new SetStateEvent(n, GameState.YOU_CAN_DRAW_CARD);
                        game.addEvent(event);
                    }
                    else{
                        Event event = new SetStateEvent(n, GameState.DRAW_CARD);
                        game.addEvent(event);
                    }

                }
            }

            case STARDUST_END -> {
                for(VirtualClient c : game.getClients().values()){
                    try {
                        c.setCardState(CardState.STARDUST_END);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            case EPIDEMIC_END -> {
                for(VirtualClient c : game.getClients().values()){
                    try {
                        c.setCardState(CardState.EPIDEMIC_END);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            default -> {
            }
        }
    }
}