package org.mio.progettoingsoft;

import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.network.VirtualView;

import java.rmi.RemoteException;
import java.util.*;

public class Lobby {
    private Map<Game, List<VirtualView>> ongoingGames;
    private Game waitingGame;

    public Lobby(){
        ongoingGames = new HashMap<>();
    }

    //return the first game with free spots, null otherwise
    public Optional<Game> checkAvailableGames(){
        return Optional.empty();
    }

    public Map<Game, List<VirtualView>> getOngoingGames() {
        return ongoingGames;
    }

    public Game getWaitingGame() {
        return waitingGame;
    }

    public void createGame(VirtualView client, String nickname, int numPlayers) throws RemoteException {
        Game newGame = new Game(numPlayers, nickname);

        List<VirtualView> playerViews = new ArrayList<>();
        playerViews.add(client);
        ongoingGames.put(newGame, playerViews);

        waitingGame = newGame;

        newGame.addPlayer(nickname, HousingColor.BLUE);

        int remaining = newGame.leftPlayers();
        client.notify("Waiting for " + remaining + " player" + (remaining != 1 ? "s." : "."));
    }

    public void joinGame(VirtualView client, String nickname) throws RemoteException {
        List<VirtualView> players = ongoingGames.get(waitingGame);

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
            for (VirtualView v : players) {
                v.notify("Waiting for " + remaining + " player" + (remaining != 1 ? "s." : "."));
            }
        } else {
            for (VirtualView v : players) {
                waitingGame = null;
                v.notify("The game is full. The will start shortly.");
            }
        }
    }
}
