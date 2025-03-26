package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.AdvCardType;
import org.mio.progettoingsoft.AdventureCard;
import org.mio.progettoingsoft.ShipBoard;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.BadParameterException;
import org.mio.progettoingsoft.responses.CombatZoneResponse;
import org.mio.progettoingsoft.responses.Response;

import java.util.ArrayList;
import java.util.List;

public class CombatZone extends AdventureCard {
    public List<CombatLine> getLines() {
        return lines;
    }

    private final List<CombatLine> lines;

    public CombatZone(int id, int level, List<CombatLine> lines) {
        super(id, level, AdvCardType.COMBAT_ZONE);
        this.lines = lines;
    }

    public static CombatZone loadCombatZone(JsonNode node) {
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        List<CombatLine> combatLines = new ArrayList<>();
        List<Penalty> cannonPenalties = new ArrayList<>();
        JsonNode criterionsNode = node.path("criterion");
        JsonNode penaltyNode = node.path("penalty");
        for (int j = 0; j < criterionsNode.size(); j++) {
            if (penaltyNode.get(j).get(0).asText().equals("cannonsPenalty")) {
                for (JsonNode cannonsPenalty : penaltyNode.get(j).get(1)) {
                    cannonPenalties.add(CannonPenalty.stringToCannonPenalty(cannonsPenalty.get(1).asText(), cannonsPenalty.get(0).asText()));
                }
                combatLines.add(new CombatLine(Criterion.stringToCriterion(criterionsNode.get(j).asText()), cannonPenalties));
            } else {
                List<Penalty> penaltyList = new ArrayList<>();
                penaltyList.add(LoseSomethingPenalty.stringToPenalty(penaltyNode.get(j).get(0).asText(), penaltyNode.get(j).get(1).asInt()));
                combatLines.add(new CombatLine(Criterion.stringToCriterion(criterionsNode.get(j).asText()), penaltyList));
            }
        }

        return new CombatZone(id, level, combatLines);
    }

    @Override
    public void applyEffect(Response r) {
        CombatZoneResponse res = (CombatZoneResponse) r;

        ShipBoard shipBoard = flyBoard.getPlayerByColor(r.getColorPlayer()).get().getShipBoard();

        if (res.getCriterion().equals(Criterion.ENGINE_POWER)){
            for (int pos : res.getPositions()){
                int[] cord = shipBoard.getCordinate(pos);
                shipBoard.getComponent(cord[0], cord[1]).removeGuest();
            }
        }
        else if (res.getCriterion().equals(Criterion.FIRE_POWER)){
            for (int pos : res.getPositions()){
                int[] cord = shipBoard.getCordinate(pos);
                shipBoard.removeComponent(cord[0], cord[1]);
            }
        }
    }
}
