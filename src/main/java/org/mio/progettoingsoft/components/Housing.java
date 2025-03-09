package org.mio.progettoingsoft.components;

import javafx.scene.control.Alert;
import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;

import java.util.HashMap;
import java.util.Map;

public class Housing extends Component {

//    private final Set<AlienType> aliensAllowed;
    private final Boolean isFirst;
    private final Map<AlienType, Boolean> guestedAlien;

    private Integer guestedHuman;
    private HousingColor color;

    public Housing(int id, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(id, ComponentType.HOUSING, topConn, bottomConn, rightConn, leftConn);

        guestedAlien = new HashMap<>();
        guestedHuman = 0;
        this.isFirst = false;
    }

    public Housing(int id, boolean isFirst, HousingColor color,  Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(id, ComponentType.HOUSING, topConn, bottomConn, rightConn, leftConn);

        guestedAlien = new HashMap<>();
        guestedHuman = 0;
        this.isFirst = isFirst;
        this.color = color;

    }

    @Override
    public void addAlienType(AlienType type){
        if (!type.equals(AlienType.NOALIEAN))
            guestedAlien.put(type, false);
    }

    public Boolean canContain(AlienType type){
        return guestedAlien.containsKey(type);
    }

    @Override
    public Boolean addHumanMember(){
        boolean canGuestHuman = ! guestedAlien.containsValue(true);

        if (canGuestHuman && guestedHuman < 2){
            guestedHuman++;
            return true;
        }

        return false;
    }

    @Override
    public Integer getQuantityMembers(){
        return guestedHuman;
    }

    @Override
    public Boolean removeHumanMember(){
        if (guestedHuman > 0){
            guestedHuman--;
            return true;
        }
        return false;
    }

    @Override
    public Boolean addAlien(AlienType type){
        if (isEmpty() && guestedAlien.containsKey(type)){
            guestedAlien.put(type, true);
            return true;
        }

        return false;
    }

    @Override
    public Boolean removeAlien(AlienType type){
        return guestedAlien.replace(type, true, false);
    }

    @Override
    public Boolean containsAlien(AlienType type){
        return getGuestedAlien().getOrDefault(type, false);
    }

    private Boolean isEmpty(){
        return guestedHuman == 0 && !guestedAlien.containsValue(true);
    }

    public Map<AlienType, Boolean> getGuestedAlien(){
        return guestedAlien;
    }
}

