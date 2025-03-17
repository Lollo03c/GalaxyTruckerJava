package org.mio.progettoingsoft.exceptions;

import org.mio.progettoingsoft.Player;

public class NoPowerException extends RuntimeException {
    private String username;
    public NoPowerException(Player player) {
        super("No power for player " + player.getUsername());
        this.username = player.getUsername();
    }

    public String getPlayerUsername() {
        return username;
    }
}
