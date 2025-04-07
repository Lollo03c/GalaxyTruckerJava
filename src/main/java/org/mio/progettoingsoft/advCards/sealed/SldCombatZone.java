package org.mio.progettoingsoft.advCards.sealed;

import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.StateEnum;
import org.mio.progettoingsoft.advCards.CombatLine;
import org.mio.progettoingsoft.advCards.Criterion;
import org.mio.progettoingsoft.advCards.Penalty;
import org.mio.progettoingsoft.advCards.PenaltyType;
import org.mio.progettoingsoft.exceptions.BadParameterException;
import org.mio.progettoingsoft.exceptions.BadPlayerException;
import org.mio.progettoingsoft.exceptions.NotEnoughBatteriesException;

import java.util.ArrayList;
import java.util.List;

public final class SldCombatZone extends SldAdvCard{
    private final List<CombatLine> lines;
    private int actualLineIndex;
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
        if(board.getState() != StateEnum.DRAW_CARD){
            throw new IllegalStateException("Illegal state: " + board.getState());
        }
        board.setState(StateEnum.CARD_EFFECT);
        this.allowedPlayers = board.getScoreBoard();
        this.playerIterator = allowedPlayers.iterator();
        switch(lines.getFirst().getCriterion()){
            case CREW -> this.state = CardState.APPLYING;
            case ENGINE_POWER -> this.state = CardState.ENGINE_CHOICE;
            case FIRE_POWER -> this.state = CardState.DRILL_CHOICE;
        }
        this.actualLineIndex = 0;
    }

    public void prepareEngines(FlyBoard board, Player player, int numDoubleEngines) {
        if(this.state != CardState.ENGINE_CHOICE){
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        if(playerIterator.hasNext()){
            actualPlayer = playerIterator.next();
            if(actualPlayer.equals(player)){
                if(numDoubleEngines < 0){
                    throw new BadParameterException("Number of double-engines must be greater than zero");
                }
                if(numDoubleEngines > actualPlayer.getShipBoard().getDoubleEngine().size()){
                    throw new BadParameterException("Number of double-engines must be smaller than the number of double engines");
                }
                if(numDoubleEngines > actualPlayer.getShipBoard().getQuantBatteries()){
                    throw new NotEnoughBatteriesException();
                }
                int power = actualPlayer.getShipBoard().getBaseEnginePower() + 2*numDoubleEngines;
                actualPlayer.getShipBoard().setActivatedEnginePower(power);
                actualPlayer.getShipBoard().removeEnergy(numDoubleEngines);
            }else{
                throw new BadPlayerException("The player " + player.getUsername() + " can't play " + this.getCardName() + " at the moment");
            }
        }else{
            this.state = CardState.APPLYING;
        }


    }

    public void prepareDrills(FlyBoard board, Player player, List<Integer[]> drillsToActivate) {
        if(this.state != CardState.DRILL_CHOICE){
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        if(playerIterator.hasNext()){
            actualPlayer = playerIterator.next();
            if(actualPlayer.equals(player)){
                if(drillsToActivate == null){
                    throw new BadParameterException("List is null");
                }
                if(drillsToActivate.size() > actualPlayer.getShipBoard().getQuantBatteries()){
                    throw new NotEnoughBatteriesException();
                }
                float power = actualPlayer.getShipBoard().getBaseFirePower();
                for(Integer[] coordinate : drillsToActivate){
                    int row = coordinate[0];
                    int col = coordinate[1];
                    power += actualPlayer.getShipBoard().getComponent(row, col).getFirePower();
                }
                actualPlayer.getShipBoard().setActivatedFirePower(power);
                actualPlayer.getShipBoard().removeEnergy(drillsToActivate.size());
            }else{
                throw new BadPlayerException("The player " + player.getUsername() + " can't play " + this.getCardName() + " at the moment");
            }
        }else{
            this.state = CardState.APPLYING;
        }
    }

    public void applyEffect(FlyBoard board) {
        if(this.state != CardState.APPLYING){
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        Criterion criterion = lines.get(actualLineIndex).getCriterion();
        Player toApplyPenalty;
        switch(criterion){
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
        if(lines.get(actualLineIndex).getPenalties().size() == 1){
            Penalty penalty = lines.get(actualLineIndex).getPenalties().getFirst();
            if(penalty.getType() == PenaltyType.CREW){
                this.state = CardState.CREW_REMOVE_CHOICE;
            }else{
                penalty.apply(board, toApplyPenalty);
            }
        }else{
            throw new RuntimeException("Still to be implemented");
        }
    }

    @Override
    public void finish(FlyBoard board) {

    }
}
