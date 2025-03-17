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
            case EPIDEMIC -> play_epidemic(card);  // WORKS
            case STARDUST -> play_stardust(card);  // WORKS
            case SMUGGLERS -> play_smugglers(card);
            case OPEN_SPACE -> play_open_space(card); // WORKS
            case COMBAT_ZONE -> play_combat_zone(card);
            case METEOR_SWARM -> play_meteor_swarm(card);
            case ABANDONED_SHIP -> play_abandoned_ship(card);
            case ABANDONED_STATION -> play_abandoned_station(card);
            default -> throw new IllegalStateException("Unexpected value: " + card.getType());
        }
    }

    private void play_epidemic(AdventureCard card) {
        for (Player player : flyBoard.getScoreBoard()) {
            List<Component> toDoRemove = new ArrayList<>();
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
            for (Component c : toDoRemove) {
                c.removeGuest();
            }
        }
    }

    private void play_stardust(AdventureCard card) {
        List<Player> playersReverse = new ArrayList<>(flyBoard.getScoreBoard());
        Collections.reverse(playersReverse);
        for (Player player : playersReverse) {
            int daysLost = player.getShipBoard().getExposedConnectors();
            flyBoard.moveDays(player, -daysLost);
        }
    }

    private void play_open_space(AdventureCard card) {
        for (Player player : flyBoard.getScoreBoard()) {
            int toActivate = player.getView().askDoubleEngine();
            player.getShipBoard().removeEnergy(toActivate);
            int base = player.getShipBoard().getBaseEnginePower();
            int power = base + toActivate * 2;
            if (power == 0) {
                throw new NoPowerException(player);
            } else {
                flyBoard.moveDays(player, power);
            }
        }
    }

    public void play_meteor_swarm(AdventureCard card) throws BadParameterException {
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

    public void play_smugglers(AdventureCard card) throws BadParameterException {
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

        for (int i = 0; i < flyBoard.getScoreBoard().size() && !defeated; i++) {
            List<Component> activated = flyBoard.getScoreBoard().get(i).getView().askDoubleDrill();
            int power = 0;
            for (Component c : activated) {
                power += c.getFirePower();
            }
            if (power > strength) {
                boolean wantsToActivate = flyBoard.getScoreBoard().get(i).getView().askForEffect(card.getType());
                if (wantsToActivate) {
                    flyBoard.getScoreBoard().get(i).addCredits(reward);
                    flyBoard.moveDays(flyBoard.getScoreBoard().get(i), -daysLost);
                }
                defeated = true;
            } else {
                int removed = 0;
                while (removed < crewLost) {
                    Component toRemoveFrom = flyBoard.getScoreBoard().get(i).getView().askForHousingToRemoveGuest("Da quale cabina vuoi rimuovere un membro?");
                    toRemoveFrom.removeGuest();
                    removed++;
                }
            }
        }
    }

    public void play_abandoned_ship(AdventureCard card) throws BadParameterException {

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

}