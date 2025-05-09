package org.mio.progettoingsoft.advCards.sealed;

import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.advCards.CombatLine;
import org.mio.progettoingsoft.advCards.Criterion;
import org.mio.progettoingsoft.advCards.Penalty;
import org.mio.progettoingsoft.advCards.PenaltyType;
import org.mio.progettoingsoft.exceptions.BadParameterException;
import org.mio.progettoingsoft.exceptions.BadPlayerException;
import org.mio.progettoingsoft.exceptions.NotEnoughBatteriesException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class SldCombatZone extends SldAdvCard {
    private final List<CombatLine> lines;
    private int actualLineIndex;
    private Penalty tempPenalty;
    private Iterator<Penalty> penaltyIterator;

    public SldCombatZone(int id, int level, List<CombatLine> lines) {
        super(id, level);
        this.lines = new ArrayList<>(lines);
    }

    @Override
    public String getCardName() {
        return "Combat Zone";
    }

    @Override
    public void init(FlyBoard board) {
        if (board.getState() != GameState.DRAW_CARD) {
            throw new IllegalStateException("Illegal state: " + board.getState());
        }
        board.setState(GameState.CARD_EFFECT);
        this.allowedPlayers = board.getScoreBoard();
        this.playerIterator = allowedPlayers.iterator();
        switch (lines.getFirst().getCriterion()) {
            case CREW -> this.state = CardState.APPLYING;
            case ENGINE_POWER -> this.state = CardState.ENGINE_CHOICE;
            case FIRE_POWER -> this.state = CardState.DRILL_CHOICE;
        }
        this.actualLineIndex = 0;
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
            float power = actualPlayer.getShipBoard().getBaseFirePower();
            for (Integer[] coordinate : drillsToActivate) {
                int row = coordinate[0];
                int col = coordinate[1];
                power += actualPlayer.getShipBoard().getComponent(row, col).getFirePower();
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


        if (player.getShipBoard().getMultiplePieces().size() > 1) {
            this.state = CardState.PART_CHOICE;
        } else {
            this.nextCannon(board);
        }
    }

    public void applyCannon(FlyBoard board, Player player) {
        if (this.state != CardState.APPLY_HEAVY_CANNON) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }


        // still to be implemented, applying the cannon


        if (player.getShipBoard().getMultiplePieces().size() > 1) {
            this.state = CardState.PART_CHOICE;
        } else {
            this.nextCannon(board);
        }
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
        board.setState(GameState.DRAW_CARD);
    }
}
