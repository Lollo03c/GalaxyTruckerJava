package org.mio.progettoingsoft;

import org.mio.progettoingsoft.advCards.CannonPenalty;
import org.mio.progettoingsoft.advCards.Meteor;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.exceptions.BadParameterException;

import java.util.Collections;
import java.util.List;

public abstract class AdventureCard {

    private final int level;
    private final int id;
    protected AdvCardType type;
    protected static AdvCardController controller = new AdvCardController();

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

    public void start(FlyBoard flyBoard){

    }

    //(under-testing method) apply the effect of the card on the passed player
    public void startTest(FlyBoard flyBoard, Player player, int cod){}

    public boolean canBeDefeatedBy(Player player){
        return false;
    }

    public List<Meteor> getMeteors() throws BadParameterException{
        throw new BadParameterException("");
    }

    public int getDaysLost() throws BadParameterException {
        throw new BadParameterException("");

    }

    public int getStrength() throws BadParameterException {
        throw new BadParameterException("");
    }

    public List<GoodType> getGoods() throws BadParameterException {
        throw new BadParameterException("");
    }

    public int getStolenGoods() throws BadParameterException {
        throw new BadParameterException("");
    }

    public int getCrewLost() throws BadParameterException{
        throw new BadParameterException("");
    }

    public int getCrewNeeded() throws BadParameterException{
        throw new BadParameterException("");
    }

    public int getCredits() throws BadParameterException{
        throw new BadParameterException("");
    }

    public List<CannonPenalty> getCannonPenalty() throws BadParameterException{
        throw new BadParameterException("");
    }
}