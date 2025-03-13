package org.mio.progettoingsoft;

import java.util.List;

public abstract class AdventureCard {

    private final int level;
    private final int id;
    protected static AdvCardController controller = new AdvCardController();

    public AdventureCard(int id, int level) {
        this.id = id;
        this.level = level;

    }

    public int getLevel(){
        return level;
    }
    public int getId(){ return id; }

    public void start(FlyBoard flyBoard){

    }
}
