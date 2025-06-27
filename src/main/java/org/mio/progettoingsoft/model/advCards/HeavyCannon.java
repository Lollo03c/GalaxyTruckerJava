package org.mio.progettoingsoft.model.advCards;

import org.mio.progettoingsoft.model.enums.Direction;
import org.mio.progettoingsoft.model.Player;
import org.mio.progettoingsoft.model.enums.CannonType;

public class HeavyCannon extends CannonPenalty {

    public HeavyCannon(Direction direction) {
        super(direction, CannonType.HEAVY);
    }

    public PenaltyType getType(){
        return PenaltyType.HEAVY_CANNON;
    }

    @Override
    public void apply(String json, Player player) throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
//        CannonResponse response = objectMapper.readValue(json, CannonResponse.class);

//        player.getShipBoard().removeComponent(response.getComponentPosition());
    }
}
