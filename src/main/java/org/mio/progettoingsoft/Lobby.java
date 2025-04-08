package org.mio.progettoingsoft;

import org.mio.progettoingsoft.network.VirtualView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public void createGame(String nickname, int numPlayers){
        Game g = new Game(numPlayers, nickname);
        //ongoingGames.put(g,)
    }
}
