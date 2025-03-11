package org.mio.progettoingsoft;

import java.util.List;

public abstract class AdventureCard {

    private final int level;
    private final int id;

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
