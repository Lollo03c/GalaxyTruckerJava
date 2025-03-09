package org.mio.progettoingsoft.advCards;

public class Slaver extends AdvancedEnemy{
    private final int crewLost;
    private final int reward;

    public Slaver(int id, int level, int strength, int daysLost, int reward, int crewLost) {
        super(id, level, strength, daysLost);
        this.reward = reward;
        this.crewLost = crewLost;
    }
}
