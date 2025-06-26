package org.mio.progettoingsoft.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.advCards.*;
import org.mio.progettoingsoft.exceptions.BadParameterException;
import org.mio.progettoingsoft.exceptions.BadPlayerException;
import org.mio.progettoingsoft.exceptions.IncorrectFlyBoardException;
import org.mio.progettoingsoft.exceptions.NotEnoughBatteriesException;
import org.mio.progettoingsoft.model.enums.CannonType;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.model.events.RemoveComponentEvent;
import org.mio.progettoingsoft.model.events.RemoveGuestEvent;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.utils.Logger;

import java.util.*;

public final class SldCombatZone extends SldAdvCard {
    private final List<CombatLine> lines;
    private int actualLineIndex;
    private Penalty tempPenalty;

    private Iterator<Penalty> penaltyIterator;
    private Iterator<CombatLine> lineIterator;
    private CombatLine actualLine;

    private List<Player> askEngine = new ArrayList<>();
    private Iterator<Player> askEngineIterator;
    private Map<Player, Integer> enginePower = new HashMap<>();

    private List<Player> askFire = new ArrayList<>();
    private Iterator<Player> askFireIterator;
    private Map<Player, Double> firePower = new HashMap<>();

    private Iterator<CannonPenalty> cannonIterator = getCannonPenalty().iterator();
    private CannonPenalty actualCannon;


    public SldCombatZone(int id, int level, List<CombatLine> lines) {
        super(id, level);
        this.lines = new ArrayList<>(lines);
    }

    public static SldCombatZone loadCombatZone(JsonNode node) {
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        List<CombatLine> combatLines = new ArrayList<>();
        List<Penalty> cannonPenalties = new ArrayList<>();
        JsonNode criterionsNode = node.path("criterion");
        JsonNode penaltyNode = node.path("penalty");
        for (int j = 0; j < criterionsNode.size(); j++) {
            if (penaltyNode.get(j).get(0).asText().equals("cannonsPenalty")) {
                for (JsonNode cannonsPenalty : penaltyNode.get(j).get(1)) {
                    cannonPenalties.add(CannonPenalty.stringToCannonPenalty(cannonsPenalty.get(1).asText(), cannonsPenalty.get(0).asText()));
                }
                combatLines.add(new CombatLine(Criterion.stringToCriterion(criterionsNode.get(j).asText()), cannonPenalties));
            } else {
                List<Penalty> penaltyList = new ArrayList<>();
                penaltyList.add(LoseSomethingPenalty.stringToPenalty(penaltyNode.get(j).get(0).asText(), penaltyNode.get(j).get(1).asInt()));
                combatLines.add(new CombatLine(Criterion.stringToCriterion(criterionsNode.get(j).asText()), penaltyList));
            }
        }

        return new SldCombatZone(id, level, combatLines);
    }

    @Override
    public List<CombatLine> getLines() {
        return lines;
    }

    @Override
    public String getCardName() {
        return "Combat Zone";
    }

    @Override
    public void init(GameServer game) {

        this.game = game;
        flyBoard = game.getFlyboard();
//
        this.allowedPlayers = flyBoard.getScoreBoard();


        askFire = new ArrayList<>(flyBoard.getScoreBoard());
        askFireIterator = askFire.iterator();

        if (getId() == 16) {
            Player minCrew = null;
            for (Player player : flyBoard.getScoreBoard()) {
                if (minCrew == null || player.getShipBoard().getQuantityGuests() < minCrew.getShipBoard().getQuantityGuests()) {
                    minCrew = player;
                }
            }
            flyBoard.moveDays(minCrew, -3);

            askEngine = new ArrayList<>(flyBoard.getScoreBoard());
            askEngineIterator = askEngine.iterator();
            setNextPlayerEngine();


        }
        else if (getId() == 36){
            askFire = new ArrayList<>(flyBoard.getScoreBoard());
            askFireIterator = askFire.iterator();

            setNextPlayerFire();
        }
    }

    public void setNextPlayerEngine() {
        if (askEngineIterator.hasNext()) {
            actualPlayer = askEngineIterator.next();
            setState(CardState.ENGINE_CHOICE);
        } else {

            Player minPlayer = null;
            int minPower = 0;

            for (Player player : enginePower.keySet()) {
                int power = 2 * enginePower.get(player) + player.getShipBoard().getBaseEnginePower();

                if (minPlayer == null || power < minPower) {
                    minPlayer = player;
                    minPower = power;
                }
                Logger.info(player.getNickname() + " " + power);
            }

            if (getId() == 16) {
                actualPlayer = minPlayer;

                setState(CardState.CREW_REMOVE_CHOICE);
            } else if (getId() == 36) {
                minPlayer.getShipBoard().stoleGood(3);

                Player minCrew = null;
                for (Player player : flyBoard.getScoreBoard()) {
                    if (minCrew == null || player.getShipBoard().getQuantityGuests() < minCrew.getShipBoard().getQuantityGuests()) {
                        minCrew = player;
                    }
                }
                actualPlayer = minPlayer;
                setNextCannon();
            }
        }
    }

    public void setNextPlayerFire() {
        if (askFireIterator.hasNext()) {
            actualPlayer = askFireIterator.next();
            setState(CardState.DRILL_CHOICE);
        } else {
            Player minPlayer = null;

            for (Player player : firePower.keySet()) {

                if (minPlayer == null || firePower.get(player) < firePower.get(minPlayer)) {
                    minPlayer = player;
                }
                Logger.info(player.getNickname() + " " + firePower.get(player));
            }

            if (getId() == 16) {
                actualPlayer = minPlayer;
                cannonIterator = getCannonPenalty().iterator();
                setNextCannon();
            } else if (getId() == 36) {
                flyBoard.moveDays(minPlayer, -4);

                askEngine = new ArrayList<>(flyBoard.getScoreBoard());
                askEngineIterator = askEngine.iterator();

                setNextPlayerEngine();
            }
        }
    }

    public void setNextCannon() {
        if (cannonIterator.hasNext()) {
            actualCannon = cannonIterator.next();
            setState(CardState.DICE_ROLL);
        } else {
            setState(CardState.FINALIZED);
        }
    }

    public void setNextCannon(String nickname, boolean destroyed, boolean energy) {

        Player player = flyBoard.getPlayerByUsername(nickname);

        if (energy)
            player.getShipBoard().removeEnergy(1);

        if (destroyed) {
            Optional<Cordinate> optCord = actualCannon.findHit(player.getShipBoard(), actualCannon.getNumber());
            player.getShipBoard().removeComponent(optCord.get());

            Event event = new RemoveComponentEvent(nickname, optCord.get());
            game.addEvent(event);
        }


        setNextCannon();
    }

    public void setEnginePower(Player player, int power) {
        enginePower.put(player, power);
    }

    public void setDrills(Player player, List<Cordinate> drillsCord){
        double power = player.getShipBoard().getBaseFirePower();
        for (Cordinate cord : drillsCord) {
            power += player.getShipBoard().getOptComponentByCord(cord).get().getFirePower(true);
        }
        firePower.put(player, power);
        setNextPlayerFire();

    }

    public void removeCrew(String nickname, List<Cordinate> cordinates) {
        if (!nickname.equals(actualPlayer.getNickname())) {
            throw new IncorrectFlyBoardException("");
        }

        for (Cordinate cord : cordinates) {
            int idComp = actualPlayer.getShipBoard().getOptComponentByCord(cord).get().getId();
            flyBoard.getComponentById(idComp).removeGuest();

        }

        for (Cordinate cord : cordinates) {
            int idComp = actualPlayer.getShipBoard().getOptComponentByCord(cord).get().getId();

            Event event = new RemoveGuestEvent(nickname, idComp);
            game.addEvent(event);
        }

        if (getId() == 16){
            askFire = new ArrayList<>(flyBoard.getScoreBoard());
            askFireIterator = askFire.iterator();

            setNextPlayerFire();
        }


    }







    public Map<Player, Double> getFirePower() {
        return firePower;
    }

    public Map<Player, Integer> getEnginePower() {
        return enginePower;
    }

    @Override
    public int getCrewLost() {
        if (getId() == 16) {
            return 2;
        }

        return 0;
    }

    @Override
    public List<CannonPenalty> getCannonPenalty() {
        if (getId() == 16) {
            return new ArrayList<>(List.of(
                    new CannonPenalty(Direction.BACK, CannonType.LIGHT),
                    new CannonPenalty(Direction.BACK, CannonType.HEAVY)
            ));
        }
        else if (getId() == 35){
            return new ArrayList<>(List.of(
                    new CannonPenalty(Direction.FRONT, CannonType.LIGHT),
                    new CannonPenalty(Direction.RIGHT, CannonType.LIGHT),
                    new CannonPenalty(Direction.LEFT, CannonType.LIGHT),
                    new CannonPenalty(Direction.BACK, CannonType.HEAVY)
            ));
        }

        return Collections.emptyList();
    }

    public CannonPenalty getActualCannon() {
        return actualCannon;
    }
}
