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
        askEngine = new ArrayList<>(flyBoard.getScoreBoard());
        askEngineIterator = askEngine.iterator();

        askFire = new ArrayList<>(flyBoard.getScoreBoard());
        askFireIterator = askFire.iterator();

        if (getId() == 16){
            Player minCrew = null;
            for (Player player : flyBoard.getScoreBoard()){
                if (minCrew == null || player.getShipBoard().getQuantityGuests() < minCrew.getShipBoard().getQuantityGuests() ){
                    minCrew = player;
                }
            }
            flyBoard.moveDays(minCrew, -3);

            setNextPlayerEngine();


        }
        else if (getId() == 36){

        }
    }

    public void setNextPlayerEngine(){
        if (askEngineIterator.hasNext()){
            actualPlayer = askEngineIterator.next();
            setState(CardState.ENGINE_CHOICE);
        }
        else{
            if (getId() == 16){
                Player minPlayer = null;
                int minPower = 0;

                for (Player player : enginePower.keySet()){
                    int power = 2 * enginePower.get(player) + player.getShipBoard().getBaseEnginePower();

                    if (minPlayer == null || power < minPower) {
                        minPlayer = player;
                        minPower = power;
                    }
                    Logger.info(player.getNickname() + " " + power);
                }

                actualPlayer = minPlayer;
                setState(CardState.CREW_REMOVE_CHOICE);

            }
        }
    }

    public void setNextPlayerFire(){
        if (askFireIterator.hasNext()){
            actualPlayer = askFireIterator.next();
            setState(CardState.DRILL_CHOICE);
        }
        else{
            if (getId() == 16){
                Player minPlayer = null;

                for (Player player : firePower.keySet()){

                    if (minPlayer == null || firePower.get(player) < firePower.get(minPlayer)) {
                        minPlayer = player;
                    }
                    Logger.info(player.getNickname() + " " + firePower.get(player));
                }

                actualPlayer = minPlayer;
                cannonIterator = getCannonPenalty().iterator();
                setNextCannon();

            }
        }
    }

    public void setNextCannon(){
        if (cannonIterator.hasNext()){
            actualCannon = cannonIterator.next();
            setState(CardState.DICE_ROLL);
        }
        else{
            setState(CardState.FINALIZED);
        }
    }

    public void setNextCannon(String nickname, boolean destroyed, boolean energy){

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

    public void setEnginePower(Player player, int power){
        enginePower.put(player, power);
    }

    public void setNextLine(){
        if (lineIterator.hasNext()){
            actualLine = lineIterator.next();
            actualLine.applyEffect(game, this);
        }
        else{
            setState(CardState.FINALIZED);
        }
    }

    public void setDrills(Player player, List<Cordinate> drillsCord){
        double power = player.getShipBoard().getBaseFirePower();
        for (Cordinate cord : drillsCord){
            power += player.getShipBoard().getOptComponentByCord(cord).get().getFirePower(true);
        }
        firePower.put(player, power);
        setNextPlayerFire();

    }

    public void removeCrew(String nickname, List<Cordinate> cordinates){
        if (! nickname.equals(actualPlayer.getNickname())){
            throw new IncorrectFlyBoardException("");
        }

        for (Cordinate cord : cordinates){
            int idComp = actualPlayer.getShipBoard().getOptComponentByCord(cord).get().getId();
            flyBoard.getComponentById(idComp).removeGuest();

        }

        for (Cordinate cord : cordinates){
            int idComp = actualPlayer.getShipBoard().getOptComponentByCord(cord).get().getId();

            Event event = new RemoveGuestEvent(nickname, idComp);
            game.addEvent(event);
        }

        if (getId() == 16){
            setNextPlayerFire();
        }


    }

    public void prepareEngines(FlyBoard board, Player player, int numDoubleEngines) {
        if (this.state != CardState.ENGINE_CHOICE) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        if (actualPlayer.equals(player)) {
            if (numDoubleEngines < 0) {
                throw new BadParameterException("Number of double-engines must be greater than zero");
            }
            if (numDoubleEngines > actualPlayer.getShipBoard().getDoubleEngine().size()) {
                throw new BadParameterException("Number of double-engines must be smaller than the number of double engines");
            }
            if (numDoubleEngines > actualPlayer.getShipBoard().getQuantBatteries()) {
                throw new NotEnoughBatteriesException();
            }
            int power = actualPlayer.getShipBoard().getBaseEnginePower() + 2 * numDoubleEngines;
            actualPlayer.getShipBoard().setActivatedEnginePower(power);
//            actualPlayer.getShipBoard().removeEnergy(numDoubleEngines);
            if (playerIterator.hasNext()) {
                actualPlayer = playerIterator.next();
            } else {
                this.state = CardState.APPLYING;
            }
        } else {
            throw new BadPlayerException("The player " + player.getNickname() + " can't play " + this.getCardName() + " at the moment");
        }

    }

    public void prepareDrills(FlyBoard board, Player player, List<Integer[]> drillsToActivate) {
        if (this.state != CardState.DRILL_CHOICE) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        if (actualPlayer.equals(player)) {
            if (drillsToActivate == null) {
                throw new BadParameterException("List is null");
            }
            if (drillsToActivate.size() > actualPlayer.getShipBoard().getQuantBatteries()) {
                throw new NotEnoughBatteriesException();
            }
            double power = actualPlayer.getShipBoard().getBaseFirePower();
            for (Integer[] coordinate : drillsToActivate) {
                int row = coordinate[0];
                int col = coordinate[1];
//                power += actualPlayer.getShipBoard().getComponent(row, col).getFirePower();
            }
            actualPlayer.getShipBoard().setActivatedFirePower(power);
//            actualPlayer.getShipBoard().removeEnergy(drillsToActivate.size());

            if (playerIterator.hasNext()) {
                actualPlayer = playerIterator.next();
            } else {
                this.state = CardState.APPLYING;
            }
        } else {
            throw new BadPlayerException("The player " + player.getNickname() + " can't play " + this.getCardName() + " at the moment");
        }
    }

    public void applyEffect(FlyBoard board) {
        if (this.state != CardState.APPLYING) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        Criterion criterion = lines.get(actualLineIndex).getCriterion();
        Player toApplyPenalty;
        switch (criterion) {
            case CREW -> toApplyPenalty = allowedPlayers.stream()
                    .min((p1, p2) -> p1.getShipBoard().compareCrew(p2.getShipBoard()))
                    .get();
            case ENGINE_POWER -> toApplyPenalty = allowedPlayers.stream()
                    .min((p1, p2) -> p1.getShipBoard().compareActivatedEnginePower(p2.getShipBoard()))
                    .get();
            case FIRE_POWER -> toApplyPenalty = allowedPlayers.stream()
                    .min((p1, p2) -> p1.getShipBoard().compareActivatedFirePower(p2.getShipBoard()))
                    .get();
            default -> toApplyPenalty = null;
        }
        if (lines.get(actualLineIndex).getPenalties().size() == 1) {
            tempPenalty = lines.get(actualLineIndex).getPenalties().getFirst();
            if (tempPenalty.getType() == PenaltyType.CREW) {
                actualPlayer = toApplyPenalty;
                this.state = CardState.CREW_REMOVE_CHOICE;
            } else {
                tempPenalty.apply(board, toApplyPenalty);
                nextLine(board);
            }
        } else {
            penaltyIterator = lines.get(actualLineIndex).getPenalties().iterator();
            tempPenalty = penaltyIterator.next();
            actualPlayer = toApplyPenalty;
            if (tempPenalty.getType() == PenaltyType.LIGHT_CANNON) {
                this.state = CardState.APPLY_LIGHT_CANNON;
            } else {
                this.state = CardState.APPLY_HEAVY_CANNON;
            }
        }
    }

    public void applyRemoveCrew(FlyBoard board, Player player, List<Integer[]> housingToRemoveCrew) {
        if (this.state != CardState.CREW_REMOVE_CHOICE) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        if (housingToRemoveCrew == null) {
            throw new BadParameterException("List is null");
        }
        if (housingToRemoveCrew.isEmpty()) {
            throw new BadParameterException("List is empty");
        }
        if (housingToRemoveCrew.size() != tempPenalty.getAmount()) {
            throw new BadParameterException("List has wrong size");
        }
        if (player.equals(actualPlayer)) {
            tempPenalty.apply(board, player, housingToRemoveCrew);
            nextLine(board);
        } else {
            throw new BadPlayerException("The player " + player.getNickname() + " cannot play " + this.getCardName() + " at the moment");
        }
    }

    public void applyCannon(FlyBoard board, Player player, List<Integer[]> shieldsToActivate) {
        if (this.state != CardState.APPLY_LIGHT_CANNON) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        if (shieldsToActivate == null) {
            throw new BadParameterException("List is null");
        }
        if (shieldsToActivate.isEmpty()) {
            throw new BadParameterException("List is empty");
        }


        // still to be implemented, activating shields and applying the cannon


//        if (player.getShipBoard().getMultiplePieces().size() > 1) {
//            this.state = CardState.PART_CHOICE;
//        } else {
//            this.nextCannon(board);
//        }
    }

    public void applyCannon(FlyBoard board, Player player) {
        if (this.state != CardState.APPLY_HEAVY_CANNON) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }


        // still to be implemented, applying the cannon

//
//        if (player.getShipBoard().getMultiplePieces().size() > 1) {
//            this.state = CardState.PART_CHOICE;
//        } else {
//            this.nextCannon(board);
//        }
    }

    private void nextCannon(FlyBoard board) {
        if (penaltyIterator.hasNext()) {
            tempPenalty = penaltyIterator.next();
            if (tempPenalty.getType() == PenaltyType.LIGHT_CANNON) {
                this.state = CardState.APPLY_LIGHT_CANNON;
            } else {
                this.state = CardState.APPLY_HEAVY_CANNON;
            }
        } else {
            nextLine(board);
        }
    }

    private void nextLine(FlyBoard board) {
        List<Player> noPowerPlayers = board.getScoreBoard().stream()
                .filter(player ->
                        player.getShipBoard().getBaseEnginePower() == 0 &&
                                (player.getShipBoard().getDoubleEngine().isEmpty() ||
                                        player.getShipBoard().getQuantBatteries() <= 0)
                ).toList();
        if (!noPowerPlayers.isEmpty()) {
            // here the method should call a procedure or throw an exception to remove the players with no power
            throw new RuntimeException("There's at least a player with no power, not implemented yet");
        }
        List<Player> noCrewPlayers = new ArrayList<Player>();
        for (Player p : board.getScoreBoard()) {
            if (p.getShipBoard().getQuantityGuests() == 0) {
                noCrewPlayers.add(p);
            }
        }
        if (!noCrewPlayers.isEmpty()) {
            // here the method should call a procedure or throw an exception to remove the players with no power
            throw new RuntimeException("There's at least a player with no crew, not implemented yet");
        }
        if (actualLineIndex < lines.size() - 1) {
            actualLineIndex++;
            this.state = CardState.APPLYING;
        } else {
            this.state = CardState.FINALIZED;
        }
    }

    @Override
    public void finish(FlyBoard board) {
        if (this.state != CardState.FINALIZED) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }
//        board.setState(GameState.DRAW_CARD);
    }

    public Map<Player, Double> getFirePower() {
        return firePower;
    }

    public Map<Player, Integer> getEnginePower() {
        return enginePower;
    }

    @Override
    public int getCrewLost(){
        if (getId() == 16){
            return 2;
        }

        return 0;
    }

    @Override
    public List<CannonPenalty> getCannonPenalty(){
        if (getId() == 16){
            return new ArrayList<>(List.of(
                    new CannonPenalty(Direction.BACK, CannonType.LIGHT),
                    new CannonPenalty(Direction.BACK, CannonType.HEAVY)
            ));
        }

        return Collections.emptyList();
    }

    public CannonPenalty getActualCannon() {
        return actualCannon;
    }
}
