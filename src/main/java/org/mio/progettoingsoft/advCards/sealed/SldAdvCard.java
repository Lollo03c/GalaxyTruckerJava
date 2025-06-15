package org.mio.progettoingsoft.advCards.sealed;

import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.advCards.CannonPenalty;
import org.mio.progettoingsoft.advCards.CombatLine;
import org.mio.progettoingsoft.advCards.Meteor;
import org.mio.progettoingsoft.advCards.Planet;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.exceptions.BadPlayerException;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.utils.Logger;
import org.mio.progettoingsoft.views.tui.VisualCard;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract sealed class SldAdvCard permits SldAbandonedShip, SldEpidemic, SldOpenSpace, SldSlavers, SldAbandonedStation, SldCombatZone, SldStardust, SldSmugglers, SldPlanets, SldPirates, SldMeteorSwarm{
    private final int level;
    private final int id;
    protected List<Player> allowedPlayers;
    protected Player actualPlayer;
    protected Iterator<Player> playerIterator;
    protected CardState state;

    protected GameServer game;
    protected FlyBoard flyBoard;


    public abstract String getCardName();

    // this method must be called right after drawing the card, it sets the game state CARD_EFFECT
    public abstract void init(GameServer game);

    // this method must be called right after applying the effect of the card on ALL the available players, it sets the game state DRAW_CARD
    public void finish(FlyBoard board){

    }

    public CardState getState() {return state;}

    public Player getActualPlayer() {return actualPlayer;}

    public void keepShipPart(FlyBoard board, Player player, int row, int col) {
        if(this.state != CardState.PART_CHOICE){
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        if(actualPlayer.equals(player)){
            actualPlayer.getShipBoard().keepPart(row, col);
            this.state = CardState.PART_REMOVING_DONE;
        }else{
            throw new BadPlayerException("Illegal player: " + player);
        }
    }

    public SldAdvCard(int id, int level){
        this.id = id;
        this.level = level;
        this.state = CardState.IDLE;
    }

    public void land(Player player, int planetIndex){
        throw new RuntimeException("this card doesn't have method land");
    }

    public int getId() {return id;}

    public int getLevel() {return level;}

    public List<CombatLine> getLines() {
        throw new RuntimeException("this card doesn't have combat lines");
    }

    public int getCrewNeeded()throws Exception{
        throw new Exception("this card doesn't need crew");
    }

    public List<Meteor> getMeteors() {
        throw new RuntimeException("this card doesn't have meteors");
    }

    public List<CannonPenalty> getCannonPenalty(){
        throw new RuntimeException("this card doesn't have cannon penalties");
    }

    public int getStolenGoods(){
        throw new RuntimeException("this card doesn't have stolen goods");
    }

    public List<GoodType> getGoods(){
        return Collections.emptyList();
    }

    public int getCrewLost(){
        throw new RuntimeException("this card doesn't have crew lost");
    }
    public int getCredits(){
        throw new RuntimeException("this card doesn't have credits");
    }

    public int getDaysLost(){
        throw new RuntimeException("this card doesn't have days lost");
    }

    public int getStrength(){
        throw new RuntimeException("this card doesn't have strength");
    }
    public List<Planet> getPlanets(){
        throw new RuntimeException("this card doesn't have planets");
    }

    public void disegnaCard() {
        VisualCard visual = new VisualCard(this);
        visual.drawCard();
    }

    protected void setState(CardState state){
        this.state = state;

        game.getController().update(this);
    }


    public void setNextPlayer(){
        Logger.debug("set next player - carta non implementata");
    }
}

