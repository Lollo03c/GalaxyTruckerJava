package org.mio.progettoingsoft.model.advCards;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mio.progettoingsoft.model.FlyBoard;
import org.mio.progettoingsoft.model.Player;
import org.mio.progettoingsoft.model.components.GoodType;
import org.mio.progettoingsoft.exceptions.BadParameterException;

import java.util.Iterator;
import java.util.List;

public abstract class AdventureCard {

    private final int level;
    private final int id;
    protected AdvCardType type;

    protected final ObjectMapper objectMapper;

    protected FlyBoard flyBoard;
    protected List<Player> playersToPlay;
    protected Iterator<Player> iterator;

    public AdventureCard(int id, int level, AdvCardType type) {
        objectMapper = new ObjectMapper();

        this.id = id;
        this.level = level;
        this.type = type;
    }

    public int getLevel(){
        return level;
    }

    public int getId(){ return id; }
    public AdvCardType getType(){return type;}

    public List<Meteor> getMeteors() {
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

    public void applyEffect(String json) throws Exception{}
}