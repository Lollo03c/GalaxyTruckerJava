package org.mio.progettoingsoft;

public enum AdvCardType {
    ABANDONED_SHIP("Abandoned Ship"),
    ABANDONED_STATION("Abandoned Station"),
    COMBAT_ZONE("Combat Zone"),
    EPIDEMIC("Epidemic"),
    METEOR_SWARM("Meteor Swarm"),
    OPEN_SPACE("Open Space"),
    PIRATE("Pirate"),
    PLANETS("Planets"),
    SLAVER("Slaver"),
    SMUGGLERS("Smagglers"),
    STARDUST("Stardust");

    private String value;

    AdvCardType(String str){
        value = str;
    }
}
