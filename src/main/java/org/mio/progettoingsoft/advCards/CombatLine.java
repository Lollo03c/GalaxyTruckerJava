package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Game;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.advCards.sealed.CardState;
import org.mio.progettoingsoft.advCards.sealed.SldCombatZone;
import org.mio.progettoingsoft.components.Drill;
import org.mio.progettoingsoft.model.events.CannonHitEvent;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.model.events.SetCardStateEvent;
import org.mio.progettoingsoft.model.interfaces.GameServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CombatLine {
    private final Criterion criterion;
    private final List<Penalty> penalties;

    private Map<Player, Double> toSelectDouble;
    private List<Player> toAsk;
    private Iterator<Player> playerIterator;
    private Player actualPlayer;

    private Iterator<Penalty> penaltyIterator;
    private Penalty actualPenalty;

    public CombatLine(Criterion criterion, List<Penalty> penalties) {
        this.criterion = criterion;
        this.penalties = penalties;
    }

    public Criterion getCriterion() {
        return criterion;
    }

    public List<Penalty> getPenalties() {
        return penalties;
    }

    public void applyEffect(GameServer game, SldCombatZone card){
        FlyBoard flyBoard = game.getFlyboard();

        if (criterion.equals(Criterion.CREW)){
            applyCrew(game, card);
        }
        else if (criterion.equals(Criterion.FIRE_POWER)){
            toAsk = new ArrayList<>(flyBoard.getScoreBoard());
            playerIterator = toAsk.iterator();

            applyFire(game, card);
        }

    }

    private void applyFire(GameServer game, SldCombatZone card){
        if (playerIterator.hasNext()){
            actualPlayer = playerIterator.next();
            Event event = new SetCardStateEvent(actualPlayer.getNickname(), CardState.DRILL_CHOICE);
            game.addEvent(event);
        }
        else{
            Player player = null;
            for (Player pl : toSelectDouble.keySet()){
                if (player == null || toSelectDouble.get(pl) < toSelectDouble.get(player))
                    player = pl;
            }

            if (penalties.getFirst().getType().equals(PenaltyType.DAYS))
                game.getFlyboard().moveDays(player, -penalties.getFirst().getAmount());
            else {
                penaltyIterator = penalties.iterator();
                actualPlayer = player;
                setNextPenality(card);
            }
        }
    }

    public void setNextPenality(SldCombatZone card){
        if (penaltyIterator.hasNext()){
            actualPenalty = penaltyIterator.next();


//            Event event = new CannonHitEvent(actualPlayer.getNickname(), actualPenalty.getCannonType());
        }
        else{
            card.setNextLine();
        }
    }

    private void applyCrew(GameServer gameServer, SldCombatZone card){
        FlyBoard flyBoard = gameServer.getFlyboard();

        Player player = null;
        for (Player play : flyBoard.getScoreBoard()){
            if (player == null || play.getShipBoard().getQuantityGuests() < player.getShipBoard().getQuantityGuests())
                player = play;
        }

        if (penalties.getFirst().getType().equals(PenaltyType.DAYS)){
            flyBoard.moveDays(player, -penalties.getFirst().getAmount());
            card.setNextLine();
        }
        else if (penalties.getFirst().getType().equals(PenaltyType.LIGHT_CANNON) || penalties.getFirst().getType().equals(PenaltyType.HEAVY_CANNON)){
            Event event = new SetCardStateEvent(player.getNickname(), CardState.CREW_REMOVE_CHOICE);
            gameServer.addEvent(event);
        }
    }

    public void setValue(GameServer game, Player player, double value){
        toSelectDouble.put(player, value);

        if (criterion.equals(Criterion.FIRE_POWER))
            applyFire(game, (SldCombatZone) game.getFlyboard().getPlayedCard());
    }
}
