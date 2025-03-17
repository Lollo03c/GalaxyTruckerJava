package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.AdvCardType;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;

public class Slaver extends AdvancedEnemy{
    private final int crewLost;

    @Override
    public int getCrewLost() {
        return crewLost;
    }

    public int getCredits() {
        return reward;
    }

    private final int reward;

    public Slaver(int id, int level, int strength, int daysLost, int reward, int crewLost) {
        super(id, level, strength, daysLost, AdvCardType.SLAVER);
        this.crewLost = crewLost;
        this.reward = reward;
    }
    
    public static Slaver loadSlaver(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        int strength = node.path("strength").asInt();
        int daysLost = node.path("daysLost").asInt();
        int reward = node.path("reward").asInt();
        int crewLost = node.path("crewLost").asInt();

        return new Slaver(id, level, strength, daysLost, reward, crewLost);
    }


}
