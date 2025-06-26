package org.mio.progettoingsoft.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.advCards.Planet;
import org.mio.progettoingsoft.exceptions.BadParameterException;
import org.mio.progettoingsoft.exceptions.BadPlayerException;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.model.events.LandOnPlanetEvent;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.utils.Logger;

import java.util.*;

public final class SldPlanets extends SldAdvCard {
    private final int daysLost;
    private final List<Planet> planets;
    private Set<Player> finishedGoodsPlacement = new HashSet<>();
    private boolean readyToProceed = false;

    private Iterator<Planet> planetIterator;
    Planet actualPlanet;


    public Map<Planet, Player> getLandedPlayers() {
        return landedPlayers;
    }


    private final Map<Planet, Player> landedPlayers;
    private int passedPlayers;

    public SldPlanets(int id, int level, int daysLost, List<Planet> planets) {
        super(id, level);
        this.daysLost = daysLost;
        this.planets = planets;
        this.landedPlayers = new HashMap<>();
        this.passedPlayers = 0;
    }

    public static SldPlanets loadPlanets(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        int daysLost = node.path("daysLost").asInt();
        List<Planet> planets = new ArrayList<>();
        JsonNode planetsNode = node.path("planets");
        for(JsonNode planet : planetsNode) {
            planets.add(Planet.stringToPlanet(planet));
        }

        return new SldPlanets(id, level, daysLost, planets);
    }

    @Override
    public List<Planet> getPlanets(){
        return planets;
    }

    @Override
    public int getDaysLost(){return daysLost;}

    @Override
    public String getCardName() {
        return "Planets";
    }

    @Override
    public void init(GameServer game) {
        passedPlayers = 0;

        this.game = game;
        this.flyBoard = game.getFlyboard();
        this.allowedPlayers = new ArrayList<>(flyBoard.getScoreBoard());
        this.playerIterator = allowedPlayers.iterator();
        this.planetIterator = planets.iterator();
    }

    @Override
    // if the planetIndex parameter is -1, the player doesn't want to land
    public void land(Player player, int planetIndex) {
        if (this.state != CardState.PLANET_CHOICE) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        if (this.actualPlayer.equals(player)) {
            passedPlayers++;
            Logger.debug("numero giocatori passati "   + passedPlayers);
            if (planetIndex == -1) {
                //setNextPlayer();
            } else {
                if (planetIndex >= this.planets.size() || planetIndex < 0) {
                    throw new BadParameterException("Index out of list bounds");
                }
                if (this.planets.get(planetIndex).getPlayer().isPresent()) {
                    throw new BadParameterException("This planet is already taken");
                }
                this.planets.get(planetIndex).land(actualPlayer);
                landedPlayers.put(planets.get(planetIndex), actualPlayer);
//                boolean allTaken = true;
//                for (Planet planet : this.planets) {
//                    if (planet.getPlayer().isEmpty()) {
//                        allTaken = false;
//                        break;
//                    }
//                }
//                if (allTaken) {
//                    applyEffect(board);
//                } else {
//                    nextPlayer(board);
//                }
            }
//            if( passedPlayers == game.getNumPlayers() || landedPlayers.size() == planets.size() ) {
//                Logger.debug("numero giocatori passati "   + passedPlayers);
//                applyEffect();
//            }else {
//                setNextPlayer();
//            }
        } else {
            throw new BadPlayerException("The player " + actualPlayer.getNickname() + " cannot play " + this.getCardName() + " at the moment");
        }
    }

    @Override
    public int getPassedPlayers(){
        return passedPlayers;
    }

    public void applyEffect() {
        Logger.debug("applyEffect() called with landedPlayers: " + landedPlayers);
        for (Player player : landedPlayers.values()){
            flyBoard.moveDays(player, -daysLost);
        }

        setNextPlanet();
    }

//    private void nextPlayer(FlyBoard board) {
//        if (playerIterator.hasNext()) {
//            actualPlayer = playerIterator.next();
//            this.state = CardState.PLANET_CHOICE;
//        } else {
//            applyEffect(board);
//        }
//    }


    @Override
    public void setNextPlayer() {
        if (playerIterator.hasNext() ) {
            actualPlayer = playerIterator.next();
            setState(CardState.PLANET_CHOICE);
        } else {
//            Logger.debug("entro in FINALIZED : " + playerIterator+ " "+ actualPlayer);
//            setState(CardState.FINALIZED);
        }
    }

    public void setNextPlanet(){
        if (planetIterator.hasNext()){
            actualPlanet = planetIterator.next();
            if (! landedPlayers.containsKey(actualPlanet)){
                setNextPlanet();
                return;
            }

            actualPlayer = landedPlayers.get(actualPlanet);
            Event event = new LandOnPlanetEvent(landedPlayers.get(actualPlanet).getNickname(), actualPlanet);
            game.addEvent(event);
        }
        else{
            setState(CardState.FINALIZED);
        }
    }
}
