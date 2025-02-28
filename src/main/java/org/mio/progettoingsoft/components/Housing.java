package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;

import java.util.HashMap;
import java.util.Map;

public class Housing extends Component {

//    private final Set<AlienType> aliensAllowed;
    private final Boolean isFirst;
    private final Map<AlienType, Integer> guestedAlien;
    private Integer guestedHuman;

    public Housing(Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(ComponentType.HOUSING, topConn, bottomConn, rightConn, leftConn);

        guestedAlien = new HashMap<>();
        guestedHuman = 0;
        this.isFirst = false;
    }

    public Housing(Boolean isFirst, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(ComponentType.HOUSING, topConn, bottomConn, rightConn, leftConn);

        guestedAlien = new HashMap<>();
        guestedHuman = 0;
        this.isFirst = isFirst;
    }

    public void addAlienType(AlienType type){
        guestedAlien.put(type, 0);
    }

    public Boolean canContain(AlienType type){
        return guestedAlien.containsKey(type);
    }

    public Boolean addMember(){
        if (!containsAlien() && guestedHuman < 2){
            guestedHuman++;
            return true;
        }

        return false;
    }

    public Boolean removeMember(){
        if (guestedHuman > 0){
            guestedHuman--;
            return true;
        }
        return false;
    }

    public Boolean addAlien(AlienType type){
        if (isEmpty() && guestedAlien.containsKey(type)){
            guestedAlien.put(type, guestedAlien.get(type) + 1);
            return true;
        }

        return false;
    }

    public Boolean removeAlien(AlienType type){
        return guestedAlien.replace(type, 1, 0);
    }

    private Boolean containsAlien(){
        for (AlienType type : guestedAlien.keySet()){
            if (guestedAlien.get(type) > 0)
                return true;
        }

        return false;
    }

    private Boolean isEmpty(){
        return guestedHuman == 0 && !containsAlien();
    }
}

