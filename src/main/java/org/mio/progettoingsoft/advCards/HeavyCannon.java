package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.Direction;
import org.mio.progettoingsoft.Player;

import java.util.Optional;

public class HeavyCannon extends CannonPenalty {

    public HeavyCannon(Direction direction) {
        super(direction);
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
