package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.Direction;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.ShipBoard;
import org.mio.progettoingsoft.responses.CannonResponse;

import java.util.Optional;

public class LightCannon extends CannonPenalty {

    public LightCannon(Direction direction) {
        super(direction);
    }

//    @Override
//    public void apply(Player player, int value) {
//        ShipBoard board = player.getShipBoard();
//        Optional<Component> hitComponent = findHit(player, value);
//
//        if (hitComponent.isEmpty())
//            return;
//
//        boolean activedShield = player.getView().askShield(direction);
//
//        if (activedShield){
//            board.removeEnergy();
//        }
//        else{
//            board.removeComponent(hitComponent.get());
//        }
//    }

    @Override
    public void apply(String json, Player player) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        CannonResponse response = objectMapper.readValue(json, CannonResponse.class);

        player.getShipBoard().removeEnergy(response.getEnergyUsed());
        if (response.isDestroyed()){
            player.getShipBoard().removeComponent(response.getComponentPosition());
        }
    }
}
