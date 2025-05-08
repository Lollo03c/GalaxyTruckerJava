package org.mio.progettoingsoft;

import org.mio.progettoingsoft.network.message.Message;

public class GameController {

    public GameController() {

    }

    /*
    private final FlyBoard flyBoard;

    public static void main(String[] args) {
        FlyBoard fly = new FlyBoard();

        Player playerStefano = new Player("Stefano", HousingColor.BLUE);
        Player playerAntonio = new Player("Antonio", HousingColor.YELLOW);
        Player playerLorenzo = new Player("Lorenzo", HousingColor.GREEN);
        Player playerAndrea = new Player("Andrea", HousingColor.RED);
        ArrayList<Player> players = new ArrayList<>();
        players.add(playerStefano);
        players.add(playerAntonio);
        players.add(playerLorenzo);
        players.add(playerAndrea);

        for (int i = 0; i < 4; i++) {
            fly.addPlayer(players.get(i).getUsername(), players.get(i).getColor());
        }

        fly.getCircuit().set(6, Optional.of(fly.getScoreBoard().get(0)));
        fly.getCircuit().set(3, Optional.of(fly.getScoreBoard().get(1)));
        fly.getCircuit().set(1, Optional.of(fly.getScoreBoard().get(2)));
        fly.getCircuit().set(0, Optional.of(fly.getScoreBoard().get(3)));

        for (Player p : fly.getScoreBoard()) {
            p.getShipBoard().addComponentToPosition(new Depot(2, false, false, Connector.TRIPLE, Connector.DOUBLE, Connector.FLAT, Connector.SINGLE), 2, 4);
            p.getShipBoard().addComponentToPosition(new Pipe(3, Connector.TRIPLE, Connector.FLAT, Connector.DOUBLE, Connector.SINGLE), 3, 3);
            p.getShipBoard().addComponentToPosition(new EnergyDepot(4, false, Connector.FLAT, Connector.TRIPLE, Connector.SINGLE, Connector.DOUBLE), 3, 2);
        }

        fly.getScoreBoard().stream()
                .filter(p -> p.getUsername().equals("Stefano"))
                .findFirst().ifPresent(p -> {
                            p.getShipBoard().addComponentToPosition(new Pipe(5, Connector.DOUBLE, Connector.SINGLE, Connector.DOUBLE, Connector.DOUBLE), 1, 4);
                            p.getShipBoard().addHumanGuest();
                            p.getShipBoard().addHumanGuest();
                        }
                );
        // Stefano has 9 exposed connectors
        fly.getScoreBoard().stream()
                .filter(p -> p.getUsername().equals("Lorenzo"))
                .findFirst().ifPresent(p -> {
                            p.getShipBoard().addComponentToPosition(new Pipe(5, Connector.TRIPLE, Connector.FLAT, Connector.SINGLE, Connector.DOUBLE), 3, 4);
                            p.getShipBoard().addHumanGuest();
                            p.getShipBoard().addHumanGuest();
                        }
                );
        // Lorenzo has 6 exposed connectors
        fly.getScoreBoard().stream()
                .filter(p -> p.getUsername().equals("Andrea"))
                .findFirst().ifPresent(p -> {
                            p.getShipBoard().addComponentToPosition(new Pipe(5, Connector.TRIPLE, Connector.SINGLE, Connector.FLAT, Connector.SINGLE), 4, 2);
                            p.getShipBoard().addHumanGuest();
                            p.getShipBoard().addHumanGuest();
                        }
                );
        // Andrea has 8 exposed connectors
        // Antonio has 7 exposed connectors
        fly.loadAdventureCards();
        fly.shuffleDeck();

        Controller c = new Controller(fly);
        try {
             c.play_abandoned_ship(new AbandonedShip(1, 2, 3, 4, 1));

        } catch (BadParameterException e) {
            e.printStackTrace();
        }
    }

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

    // app logic testing:
    // - epidemic: DONE
    // - stardust: DONE
    // - open space: DONE
    // - abandoned ship: DONE
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
        for (int i = 0; i < flyBoard.getScoreBoard().size(); i++) {
            Player player = flyBoard.getScoreBoard().get(i);
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
                        Optional<Component> depot = player.getView().askForDepotToAdd(type);
                        if (depot.isPresent()) {
                            depot.get().addGood(type);
                        }
                        //altrimenti devo dare la possibilità di scartare le merci e posizionare quelle nuove
                    }
                }
                break;
            } else if (power < strength) {
                player.getShipBoard().stoleGood(stolenGoods);
//                for (int i = 0; i < stolenGoods; i++)
//                    player.getShipBoard().stoleGood(1);
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

    private void play_abandoned_station(AdventureCard card) throws BadParameterException {
        List<Player> playerList = new ArrayList<>(flyBoard.getScoreBoard());
        int crewNeeded = card.getCrewNeeded();
        int daysLost = card.getDaysLost();
        List<GoodType> goods = card.getGoods();
        for (Player player : playerList) {
            if (player.getShipBoard().getQuantityGuests() >= crewNeeded) {
                boolean answer = player.getView().askForEffect(card.getType());

                if (answer) {
                    flyBoard.moveDays(player, -daysLost);

                    for (GoodType type : goods) {
                        Optional<Component> depot = player.getView().askForDepotToAdd(type);
                        if (depot.isPresent()) {
                            depot.get().addGood(type);
                        }
                        //altrimenti devo dare la possibilità di cambiare le merci già caricate
                        //con quelle che potrei caricare in seguito alla ricezione del bottino
                        //aggiungere anche riposizionamento generale delle merci
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
//                    shot.apply(player, value);
                }
            }
        }
    }

    public void play_planets(AdventureCard card) throws BadParameterException {
        List<Player> landedPlayers = new LinkedList<>();
        List<Player> score = flyBoard.getScoreBoard();
        int choice = 0;
        for (Player player : score) {
            choice = player.getView().askForPlanet(card.getPlanets());
            if (choice != 0) {
                landedPlayers.addFirst(player);
                card.getPlanets().get(choice - 1).land(player);
                for (GoodType type : card.getPlanets().get(choice - 1).getGoods()) {
                    Optional<Component> depot = player.getView().askForDepotToAdd(type);
                    depot.ifPresent(component -> component.addGood(type));
                    //da implementare possibilità di riposizionamento merci
                    //da implementare possibilità di scartare merci
                }
            }
            if (landedPlayers.size() == card.getPlanets().size()) {
                break;
            }
        }
        for (Player p : landedPlayers) {
            flyBoard.moveDays(p, -card.getDaysLost());

        }
    }

    // still to be finished
    private void play_combat_zone(AdventureCard card) throws BadParameterException {
        // a combat zone is made out of many lines, for each line the method selects the player to apply the penalty to, according to the criterion
        List<CombatLine> lines = card.getLines();
        for (CombatLine line : lines) {
            Player toApplyPenalty = null;
            switch (line.getCriterion()) {
                case CREW -> toApplyPenalty = flyBoard.getScoreBoard().stream()
                        .min((p1, p2) -> p1.getShipBoard().compareCrew(p2.getShipBoard()))
                        .get();
                case FIRE_POWER -> {
                    // for each player it calculates the tmp firepower (it is stored in a property, so that it's possible
                    // to compare a player to another based on it)
                    for (Player player : flyBoard.getScoreBoard()) {
                        float activatedPower = player.getShipBoard().getBaseFirePower();
                        List<Component> activated = player.getView().askDoubleDrill();
                        for (Component c : activated) {
                            activatedPower += c.getFirePower();
                        }
                        player.getShipBoard().setActivatedFirePower(activatedPower);
                    }
                    toApplyPenalty = flyBoard.getScoreBoard().stream()
                            .min((p1, p2) -> p1.getShipBoard().compareActivatedFirePower(p2.getShipBoard()))
                            .get();
                    for (Player player : flyBoard.getScoreBoard()) {
                        player.getShipBoard().setActivatedFirePower(player.getShipBoard().getBaseFirePower());
                    }
                }
                case ENGINE_POWER -> {
                    // for each player it calculates the tmp engine power (it is stored in a property, so that it's possible
                    // to compare a player to another based on it)
                    for (Player player : flyBoard.getScoreBoard()) {
                        int activatedPower = player.getShipBoard().getBaseEnginePower();
                        int activated = player.getView().askDoubleEngine();
                        activatedPower += activated * 2;
                        player.getShipBoard().setActivatedEnginePower(activatedPower);
                    }
                    toApplyPenalty = flyBoard.getScoreBoard().stream()
                            .min((p1, p2) -> p1.getShipBoard().compareActivatedEnginePower(p2.getShipBoard()))
                            .get();
                    for (Player player : flyBoard.getScoreBoard()) {
                        player.getShipBoard().setActivatedEnginePower(player.getShipBoard().getBaseEnginePower());
                    }
                }
            }
            // this section will apply the penalties to the selected player, still to be implemented
            for (Penalty penalty : line.getPenalties()) {
                // cos'è il "value" parametro di apply?
                // apply di penalty presenta lo stesso problema di start delle carte avventura: devo chiedere qualcosa
                // all'utente prima di applicare la penalità: o lo faccio nel metodo apply ("rompe" il design pattern MVC)
                // oppure devo trovare un'alternativa, il che vorrebbe dire rendere senza metodi la classe penalty
//                penalty.apply(toApplyPenalty, 0);
            }
        }
    }
*/
}