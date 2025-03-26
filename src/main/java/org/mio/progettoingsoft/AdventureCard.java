package org.mio.progettoingsoft;

import org.mio.progettoingsoft.advCards.CannonPenalty;
import org.mio.progettoingsoft.advCards.CombatLine;
import org.mio.progettoingsoft.advCards.Meteor;
import org.mio.progettoingsoft.advCards.Planet;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.exceptions.BadParameterException;
import org.mio.progettoingsoft.responses.Response;

import java.util.Collections;
import java.util.List;

public abstract class AdventureCard {

    private final int level;
    private final int id;
    protected AdvCardType type;

    protected FlyBoard flyBoard;
    protected List<Player> playersToPlay;
    protected int iter;

    public AdventureCard(int id, int level, AdvCardType type) {
        this.id = id;
        this.level = level;
        this.type = type;
    }

    public int getLevel(){
        return level;
    }
    public int getId(){ return id; }
    public AdvCardType getType(){return type;}

    public List<Meteor> getMeteors() throws BadParameterException{
        throw new BadParameterException("La carta selezionata non contiene meteoriti: " + this.getType());
    }

    public int getDaysLost() throws BadParameterException {
        throw new BadParameterException("La carta selezionata non implica perdita di giorni: " + this.getType());
    }

    public int getStrength() throws BadParameterException {
        throw new BadParameterException("La carta selezionata non ha una propria potenza: " + this.getType());
    }

    public List<GoodType> getGoods() throws BadParameterException {
        throw new BadParameterException("La carta selezionata non contiene merci: " + this.getType());
    }

    public int getStolenGoods() throws BadParameterException {
        throw new BadParameterException("La carta selezionata non implica perdita di merci: " + this.getType());
    }

    public int getCrewLost() throws BadParameterException{
        throw new BadParameterException("La carta selezionata non inmplica perdita di equipaggio: " + this.getType());
    }

    public int getCrewNeeded() throws BadParameterException{
        throw new BadParameterException("La carta selezionata non necessita di equipaggio" + this.getType());
    }

    public int getCredits() throws BadParameterException{
        throw new BadParameterException("La carta selezionata non fornisce crediti: " + this.getType());
    }

    public List<CannonPenalty> getCannonPenalty() throws BadParameterException{
        throw new BadParameterException("La carta selezionata non contiene cannonate: " + this.getType());
    }

    public List<Planet> getPlanets() throws BadParameterException {
        throw new BadParameterException("La carta selezionata non contiene pianeti: " + this.getType());
    }

    public List<CombatLine> getLines() throws BadParameterException {
        throw new BadParameterException("La carta selezionata non contiene linee di combattimento: " + this.getType());
    }
    public void start(){}

    public /*abstract*/ void applyEffect(Response res){

    }
}