package org.mio.progettoingsoft.advcards;

public class Slaver extends AdvancedEnemy{
    private int crewLost;

    protected Slaver(int level, int strength, int daysLost, int reward, int crewLost) {
        super(level, strength, daysLost, reward);
        this.crewLost = crewLost;
    }
}
