package org.mio.progettoingsoft;

import org.mio.progettoingsoft.advCards.*;
import org.mio.progettoingsoft.components.*;
import org.mio.progettoingsoft.exceptions.BadCardException;
import org.mio.progettoingsoft.exceptions.BadParameterException;
import org.mio.progettoingsoft.exceptions.NoPowerException;

import java.util.*;

public class Controller {
    private final FlyBoard flyBoard;

    public Controller(FlyBoard board) {
        this.flyBoard = board;
    }

    public void drawCardAndApply() throws BadParameterException {
        AdventureCard card = flyBoard.drawAdventureCard();
        switch (card.getType()) {
            case PIRATE -> play_pirate(card);
            case SLAVER -> play_slaver(card);
            case PLANETS -> play_planets(card);
            case EPIDEMIC -> play_epidemic(card);
            case STARDUST -> play_stardust(card);
            case SMUGGLERS -> play_smugglers(card);
            case OPEN_SPACE -> play_open_space(card);
            case COMBAT_ZONE -> play_combat_zone(card);
            case METEOR_SWARM -> play_meteor_swarm(card);
            case ABANDONED_SHIP -> play_abandoned_ship(card);
            case ABANDONED_STATION -> play_abandoned_station(card);
            default -> throw new IllegalStateException("Unexpected value: " + card.getType());
        }
    }

    private void play_epidemic(AdventureCard card) {
        for (Player player : flyBoard.getScoreBoard()) {
            Set<Component> toDoRemove = new HashSet<>();
            // for each housing directly connected to another housing, verifies if they all contain at least a member
            // (human/alien) and adds them to those from which one member will be removed
            player.getShipBoard().getComponentsStream()
                    .filter(c -> c.getType().equals(ComponentType.HOUSING))
                    .forEach(c -> {
                        Map<Direction, Component> adj = player.getShipBoard().getAdjacent(c.getRow(), c.getColumn());
                        adj.forEach((direction, component) -> {
                            if (component.getType().equals(ComponentType.HOUSING) && c.getQuantityGuests() > 0 && component.getQuantityGuests() > 0) {
                                toDoRemove.add(component);
                            }
                        });
                    });
            // removes a crew member from each selected housing
            for (Component c : toDoRemove) {
                c.removeGuest();
            }
        }
    }

    private void play_stardust(AdventureCard card) {
        // in reverse order, it gets the number of exposed connectors of each player's ship, then move
        // back the player's rocket that number
        List<Player> playersReverse = new ArrayList<>(flyBoard.getScoreBoard());
        Collections.reverse(playersReverse);
        for (Player player : playersReverse) {
            int daysLost = player.getShipBoard().getExposedConnectors();
            flyBoard.moveDays(player, -daysLost);
        }
    }

    private void play_open_space(AdventureCard card) {
        for (Player player : flyBoard.getScoreBoard()) {
            // asks the player how many double engine to activate
            int toActivate = player.getView().askDoubleEngine();
            player.getShipBoard().removeEnergy(toActivate);
            // calculates the tmp engine power and moves the player forward that number
            int base = player.getShipBoard().getBaseEnginePower();
            int power = base + toActivate * 2;
            if (power == 0) {
                throw new NoPowerException(player);  //still to be managed: throws an exc to indicate that the player must be removed
            } else {
                flyBoard.moveDays(player, power);
            }
        }
    }

    private void play_meteor_swarm(AdventureCard card) throws BadParameterException {
        if (!card.getType().equals(AdvCardType.METEOR_SWARM))
            throw new BadCardException("");

        List<Meteor> meteors = card.getMeteors();

        List<Player> score = new ArrayList<>(flyBoard.getScoreBoard());
        int offsetRow = score.getFirst().getShipBoard().getOffsetRow();
        int offsetCol = score.getFirst().getShipBoard().getOffsetCol();

        for (Meteor meteor : meteors) {
            int row = score.getFirst().getView().rollDicesAndSum() - offsetRow;
            int col = score.getFirst().getView().rollDicesAndSum() - offsetCol;

            for (Player player : score) {
                if (meteor.getDirection().equals(Direction.BACK) || meteor.getDirection().equals(Direction.FRONT))
                    meteor.hit(player, col);
                else
                    meteor.hit(player, row);
            }
        }
    }

    private void play_smugglers(AdventureCard card) throws BadParameterException {
        if (!card.getType().equals(AdvCardType.SMUGGLERS))
            throw new BadCardException("");

        boolean ended = false;
        int daysLost = card.getDaysLost();
        int strength = card.getStrength();
        int stolenGoods = card.getStolenGoods();
        List<GoodType> goods = card.getGoods();


        List<Player> playerList = new ArrayList<>(flyBoard.getScoreBoard());
        for (Player player : playerList) {
            List<Component> doubleDrills = player.getView().askDoubleDrill();

            float power = player.getShipBoard().getBaseEnginePower();
            for (Component drill : doubleDrills) {
                power += drill.getFirePower();
            }
            player.getShipBoard().removeEnergy(doubleDrills.size());

            if (power > strength) {
                boolean answer = player.getView().askForEffect(card.getType());

                if (answer) {
                    flyBoard.moveDays(player, -daysLost);
                    for (GoodType type : goods) {
                        Component depot = player.getView().askForDepotToAdd(type);
                        depot.addGood(type);
                    }
                }
                break;
            } else if (power < strength) {
                for (int i = 0; i < stolenGoods; i++)
                    player.getShipBoard().stoleGood();
            }
        }
    }

    private void play_slaver(AdventureCard card) throws BadParameterException {
        if (!card.getType().equals(AdvCardType.SLAVER))
            throw new BadCardException("");

        boolean defeated = false;

        int strength = card.getStrength();
        int daysLost = card.getDaysLost();
        int crewLost = card.getCrewLost();
        int reward = card.getCredits();

        // for each player, until it is defeated
        for (int i = 0; i < flyBoard.getScoreBoard().size() && !defeated; i++) {
            // asks for which double drill to activate, then calculates the tmp firepower
            List<Component> activated = flyBoard.getScoreBoard().get(i).getView().askDoubleDrill();
            float power = flyBoard.getScoreBoard().get(i).getShipBoard().getBaseFirePower();
            for (Component c : activated) {
                power += c.getFirePower();
            }
            if (power > strength) {
                //the enemy is defeated, now the player can choose whether to get the reward and lose days or not
                boolean wantsToActivate = flyBoard.getScoreBoard().get(i).getView().askForEffect(card.getType());
                if (wantsToActivate) {
                    flyBoard.getScoreBoard().get(i).addCredits(reward);
                    flyBoard.moveDays(flyBoard.getScoreBoard().get(i), -daysLost);
                }
                defeated = true;
            } else if (power < strength) {
                // the player is defeated, the method asks from which housing he wants to remove members until
                // they finish (still to be implemented, at the moment an exc is thrown)
                int removed = 0;
                while (removed < crewLost) {
                    Component toRemoveFrom = flyBoard.getScoreBoard().get(i).getView().askForHousingToRemoveGuest("Da quale cabina vuoi rimuovere un membro?");
                    toRemoveFrom.removeGuest();
                    removed++;
                }
            }
            // if power == strength, nothing happens to the player but the enemy is not defeated, so he will attack the next player
        }
    }

    private void play_abandoned_ship(AdventureCard card) throws BadParameterException {

        if (!card.getType().equals(AdvCardType.ABANDONED_SHIP))
            throw new BadCardException("");

        List<Player> playerList = new ArrayList<>(flyBoard.getScoreBoard());

        int crewLost = card.getCrewLost();
        int credits = card.getCredits();
        int daysLost = card.getDaysLost();

        for (Player player : playerList) {
            if (player.getShipBoard().getQuantityGuests() >= crewLost) {
                boolean answer = player.getView().askForEffect(card.getType());

                if (answer) {
                    player.addCredits(credits);
                    flyBoard.moveDays(player, -1 * daysLost);

                    for (int i = 0; i < crewLost; i++) {
                        String mess = "\nSelect the housing from which remove a crew member .";
                        Component housing = player.getView().askForHousingToRemoveGuest("");
                        housing.removeGuest();
                    }
                    return;
                }
            }
        }
    }

    private void play_abandoned_station(AdventureCard card) throws BadParameterException{
        List<Player> playerList = new ArrayList<>(flyBoard.getScoreBoard());
        int crewNeeded = card.getCrewNeeded();
        int daysLost = card.getDaysLost();
        List<GoodType> goods = card.getGoods();
        for (Player player : playerList){
            if (player.getShipBoard().getQuantityGuests() >= crewNeeded){
                boolean answer = player.getView().askForEffect(card.getType());

                if (answer){
                    flyBoard.moveDays(player, -daysLost);

                    for (GoodType type : goods){
                        Component depot = player.getView().askForDepotToAdd(type);

                        depot.addGood(type);
                    }

                    return;
                }
            }
        }
    }

    private void play_pirate(AdventureCard card) throws BadParameterException {
        if (!card.getType().equals(AdvCardType.PIRATE))
            throw new BadCardException("");

        int credits = card.getCredits();
        int strength = card.getStrength();
        int daysLost = card.getDaysLost();
        List<CannonPenalty> shots = card.getCannonPenalty();

        List<Player> playerList = new ArrayList<>(flyBoard.getScoreBoard());
        List<Player> defeated = new ArrayList<>();

        for (Player player : playerList) {
            ShipBoard board = player.getShipBoard();

            if (board.getBaseFirePower() < strength) {
                List<Component> doubleDrills = player.getView().askDoubleDrill();

                float power = board.getBaseFirePower();
                for (Component c : doubleDrills) {
                    power += c.getFirePower();
                }

                if (power < strength)
                    defeated.add(player);
            }
        }

        if (!defeated.isEmpty()) {
            int value = defeated.getFirst().getView().rollDicesAndSum();

            for (CannonPenalty shot : shots) {
                for (Player player : defeated) {
                    shot.apply(player, value);
                }
            }
        }
    }

    private void play_planets(AdventureCard card) throws BadParameterException {
        List<Player> score = flyBoard.getScoreBoard();
        List<Planet> planets = card.getPlanets();
        int choice = 0;
        for(Player player : score){
            choice = player.getView().askForPlanet(planets);
        }
    }

    // still to be finished
    private void play_combat_zone(AdventureCard card) throws BadParameterException {
        // a combat zone is made out of many lines, for each line the method selects the player to apply the penalty to, according to the criterion
        List<CombatLine> lines = card.getLines();
        for(CombatLine line : lines){
            Player toApplyPenalty = null;
            switch(line.getCriterion()){
                case CREW -> toApplyPenalty = flyBoard.getScoreBoard().stream()
                        .min((p1,p2) -> p1.getShipBoard().compareCrew(p2.getShipBoard()))
                        .get();
                case FIRE_POWER -> {
                    // for each player it calculates the tmp firepower (it is stored in a property, so that it's possible
                    // to compare a player to another based on it)
                    for(Player player : flyBoard.getScoreBoard()){
                        float activatedPower = player.getShipBoard().getBaseFirePower();
                        List<Component> activated = player.getView().askDoubleDrill();
                        for(Component c : activated){
                            activatedPower += c.getFirePower();
                        }
                        player.getShipBoard().setActivatedFirePower(activatedPower);
                    }
                    toApplyPenalty = flyBoard.getScoreBoard().stream()
                            .min((p1,p2) -> p1.getShipBoard().compareActivatedFirePower(p2.getShipBoard()))
                            .get();
                    for(Player player : flyBoard.getScoreBoard()){
                        player.getShipBoard().setActivatedFirePower(player.getShipBoard().getBaseFirePower());
                    }
                }
                case ENGINE_POWER -> {
                    // for each player it calculates the tmp engine power (it is stored in a property, so that it's possible
                    // to compare a player to another based on it)
                    for(Player player : flyBoard.getScoreBoard()){
                        int activatedPower = player.getShipBoard().getBaseEnginePower();
                        int activated = player.getView().askDoubleEngine();
                        activatedPower += activated*2;
                        player.getShipBoard().setActivatedEnginePower(activatedPower);
                    }
                    toApplyPenalty = flyBoard.getScoreBoard().stream()
                            .min((p1,p2) -> p1.getShipBoard().compareActivatedEnginePower(p2.getShipBoard()))
                            .get();
                    for(Player player : flyBoard.getScoreBoard()){
                        player.getShipBoard().setActivatedEnginePower(player.getShipBoard().getBaseEnginePower());
                    }
                }
            }
            // this section will apply the penalties to the selected player, still to be implemented
            for(Penalty penalty : line.getPenalties()){
                // cos'è il "value" parametro di apply?
                // apply di penalty presenta lo stesso problema di start delle carte avventura: devo chiedere qualcosa
                // all'utente prima di applicare la penalità: o lo faccio nel metodo apply ("rompe" il design pattern MVC)
                // oppure devo trovare un'alternativa, il che vorrebbe dire rendere senza metodi la classe penalty
                penalty.apply(toApplyPenalty, 0);
            }
        }
    }

}