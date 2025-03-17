package org.mio.progettoingsoft;

import org.mio.progettoingsoft.advCards.Meteor;

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

    public List<Meteor> getMeteors(){
        return Collections.emptyList();
    }

    public int getDaysLost(){

    }
}
