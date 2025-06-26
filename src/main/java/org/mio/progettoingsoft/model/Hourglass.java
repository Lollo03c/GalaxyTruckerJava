package org.mio.progettoingsoft.model;

import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.utils.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Represents an hourglass mechanism within the game,
 * controlling a timed event that can be activated a limited number of times.
 * Each activation triggers a countdown, and upon completion,
 * notifies the game controller.
 */
public class Hourglass {
    private static final int durataSecondi = 5;
    private final int maxAttivazioni = 3;
    private int attivazioni = 0;
    private GameServer game;

    /**
     * Constructs a new Hourglass instance associated with a specific game server.
     * @param game The {@link GameServer} instance that this hourglass will interact with.
     */
    public Hourglass(GameServer game) {
        this.game = game;
    }

    /**
     * Returns the total duration of the hourglass in seconds for a single activation.
     * @return The duration in seconds.
     */
    public static int getTotal(){
        return durataSecondi;
    }

    /**
     * Starts the hourglass countdown.
     * If the maximum number of activations has been reached,
     * the hourglass will not start and a message will be printed to the console.
     * Upon successful activation, it logs the start, increments the activation count,
     * and schedules a {@link TimerTask} to notify the game controller
     * via {@code game.getController().finishHourglass()} when the time expires.
     */
    public void avvia() {
        if (attivazioni >= maxAttivazioni) {
            System.out.println("La clessidra ha già raggiunto il numero massimo di attivazioni.");
            return;
        }
        Logger.debug("La clessidra è stata avviata : " + attivazioni);
        attivazioni++;
        int numeroAttivazione = attivazioni;

        System.out.println("Clessidra attivata. Timer " + numeroAttivazione + " in corso...");

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Timer " + numeroAttivazione + " scaduto!");
                game.getController().finishHourglass(numeroAttivazione);
            }
        }, durataSecondi * 1000L);
    }
}


