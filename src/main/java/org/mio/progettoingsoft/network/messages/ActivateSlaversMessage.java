package org.mio.progettoingsoft.network.messages;

import org.mio.progettoingsoft.model.Cordinate;

import java.util.List;

public final class ActivateSlaversMessage extends Message{
    List<Cordinate> activatedDrills;
    boolean wantsToActivate;
    public ActivateSlaversMessage (int idGame, String nickname,List<Cordinate> activatedDrills,boolean wantsToActivate){
        super(idGame,nickname);
        this.activatedDrills = activatedDrills;
        this.wantsToActivate = wantsToActivate;
    }

    public List<Cordinate> getActivatedDrills(){
        return activatedDrills;
    }

    public boolean getWantsToActivate(){
        return wantsToActivate;
    }
}
