package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.components.Depot;
import org.mio.progettoingsoft.components.GoodType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Planets extends AdventureCard {
    private final int daysLost;
    private final List<Planet> planets;
    private Player playerState;
    private List<Player> landedPlayers;


    @Override
    public List<Planet> getPlanets() {
        return planets;
    }

    @Override
    public int getDaysLost() {
        return daysLost;
    }

    public Planets(int id, int level, int daysLost, List<Planet> planets) {
        super(id, level, AdvCardType.PLANETS);
        this.daysLost = daysLost;
        this.planets = planets;
    }

    public static Planets loadPlanets(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        int daysLost = node.path("daysLost").asInt();
        List<Planet> planets = new ArrayList<>();
        JsonNode planetsNode = node.path("planets");
        for(JsonNode planet : planetsNode) {
            planets.add(Planet.stringToPlanet(planet));
        }

        return new Planets(id, level, daysLost, planets);
    }


    public void start(FlyBoard board){
        playerState = board.getScoreBoard().getFirst();
        landedPlayers = new LinkedList<>();
    }

    //takes the player who did the choice and the number of the planet
    //in which he wants to land, 0 otherwise
    public boolean applyEffect(Player player, int choice) {
        if (!playerState.equals(player)) {
            return false;
        }
        if (choice != 0) {
            if (choice < 1 || choice > planets.size() || planets.get(choice - 1).getPlayer().isPresent()) {
                return false;  //scelta non valida
            }
            landedPlayers.addFirst(player);
            planets.get(choice - 1).land(player);
        }
        return true;
    }


    //il giocatore seleziona il deposito dove vuole inserire la sua merce
    //la merce è identificata da un intero che va da 0 a numeroMerciPianeta-1
    public void obtainGood(Player player, Depot depot, int good, FlyBoard board){
        if(playerState.equals(player)) {
            Optional<Planet> currentPlanetOpt = planets.stream()
                    .filter(p -> p.getPlayer().equals(player))
                    .findFirst();
            if (currentPlanetOpt.isEmpty()) {
                return;
            }
             Planet currentPlanet = currentPlanetOpt.get();
             depot.addGood(currentPlanet.getGoods().get(good));
             if(good == currentPlanet.getGoods().size()-1){
                 //in questo caso è finito l'effetto del giocatore, è l'ultima merce che devo posizionare quindi aggiorno lo stato
                 if(board.getScoreBoard().getLast().equals(player) || landedPlayers.size() == planets.size()){
                     //se il giocatore è l'ultimo che poteva giocare, procedo a togliere i giorni di volo
                     for(Player p : landedPlayers){
                         board.moveDays(p, -daysLost);
                     }
                 }
                 else{ //altrimenti passo il turno al prossimo giocatore
                     int i = board.getScoreBoard().indexOf(player);
                     playerState = board.getScoreBoard().get(i+1);
                 }
             }
        }

    }


    //return true if the transition of goods is legit, false otherwise
    public boolean moveGoods(Player player, GoodType good, Component oldDepot, Component newDepot){
         if(playerState.equals(player)){
              return player.getShipBoard().changeDepot(good,(Depot) oldDepot, (Depot) newDepot);
         }
         //o potremmo lanciare un'eccezione se un player a cui non tocca giocare fa una mossa
         return false;
    }

    public boolean discardGoods(Player player, GoodType good, Component depot){
        if(playerState.equals(player)){
            return player.getShipBoard().discardGood(good, (Depot) depot);
        }
        return false;
    }

}
