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
    private static final int durationSecs = 10;
    private final int maxActivations = 3;
    private int activations = 0;
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
        return durationSecs;
    }

    /**
     * Starts the hourglass countdown.
     * If the maximum number of activations has been reached,
     * the hourglass will not start and a message will be printed to the console.
     * Upon successful activation, it logs the start, increments the activation count,
     * and schedules a {@link TimerTask} to notify the game controller
     * via {@code game.getController().finishHourglass()} when the time expires.
     */
    public void start() {
        if (activations >= maxActivations) {
            Logger.debug("Hourglass already did " + activations + " cycles");
            return;
        }
        Logger.debug("Hourglass started : " + activations);
        activations++;
        int nActivations = activations;

        Logger.debug("Hourglass started. Timer " + nActivations + " is running");

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Logger.debug("Timer " + nActivations + " ended");
                game.getController().finishHourglass(nActivations);
            }
        }, durationSecs * 1000L);
    }
}


