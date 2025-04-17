package org.mio.progettoingsoft;

import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.network.VirtualClient;

import java.rmi.RemoteException;
import java.util.*;

public class Lobby {
    private Map<Game, List<VirtualClient>> ongoingGames;
    private Game waitingGame;

    public Lobby(){
        ongoingGames = new HashMap<>();
    }

    //return the first game with free spots, null otherwise
    public Optional<Game> checkAvailableGames(){
        return Optional.empty();
    }

    public Map<Game, List<VirtualClient>> getOngoingGames() {
        return ongoingGames;
    }

    public Game getWaitingGame() {
        return waitingGame;
    }

    public void createGame(VirtualClient client, String nickname, int numPlayers) throws RemoteException {
        Game newGame = new Game(numPlayers, nickname);

        List<VirtualClient> playerViews = new ArrayList<>();
        playerViews.add(client);
        ongoingGames.put(newGame, playerViews);

        waitingGame = newGame;

        newGame.addPlayer(nickname, HousingColor.BLUE);

        int remaining = newGame.leftPlayers();
        client.notify("Waiting for " + remaining + " player" + (remaining != 1 ? "s." : "."));
    }

    public void joinGame(VirtualClient client, String nickname) throws RemoteException {
        List<VirtualClient> players = ongoingGames.get(waitingGame);

        players.add(client);

        HousingColor assignedColor;
        switch (players.size()) {
            case 1 -> assignedColor = HousingColor.BLUE;
            case 2 -> assignedColor = HousingColor.RED;
            case 3 -> assignedColor = HousingColor.GREEN;
            case 4 -> assignedColor = HousingColor.YELLOW;
            default -> {
                client.notify("Error in color assignment.");
                return;
            }
        }

        waitingGame.addPlayer(nickname, assignedColor);
        client.notify("You joined the game!");

        int remaining;
        if (players.size() < waitingGame.getNumPlayers()) {
            remaining = waitingGame.leftPlayers();
            for (VirtualClient v : players) {
                v.notify("Waiting for " + remaining + " player" + (remaining != 1 ? "s." : "."));
            }
        } else {
            for (VirtualClient v : players) {
                waitingGame = null;
                v.notify("The game is full. The will start shortly.");
            }
        }
    }
}
