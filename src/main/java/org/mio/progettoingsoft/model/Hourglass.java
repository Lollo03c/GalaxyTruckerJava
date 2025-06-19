package org.mio.progettoingsoft.model;

import org.mio.progettoingsoft.Game;
import org.mio.progettoingsoft.model.interfaces.GameServer;

import java.util.Timer;
import java.util.TimerTask;

public class Hourglass {
    private final int durataSecondi = 120;
    private final int maxAttivazioni = 3;
    private int attivazioni = 0;
    private GameServer game;

    public Hourglass(GameServer game) {
        this.game = game;
    }

    public void avvia() {
        if (attivazioni >= maxAttivazioni) {
            System.out.println("La clessidra ha già raggiunto il numero massimo di attivazioni.");
            return;
        }

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


