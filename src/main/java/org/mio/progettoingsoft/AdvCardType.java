package org.mio.progettoingsoft;

/**
 * Enumerates the different types of Adventure Cards that can be encountered in the game.
 * Each type has a string representation that serves as its official name.
 */
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
    SMUGGLERS("Smugglers"),
    STARDUST("Stardust");

    private String value;

    /**
     * Constructs an {@code AdvCardType} enum constant with the specified string value.
     *
     * @param str The string name of the adventure card type.
     */
    AdvCardType(String str){
        value = str;
    }

    /**
     * Returns the string representation (name) of this adventure card type.
     *
     * @return The string value of the adventure card type.
     */
    public String getValue(){
        return value;
    }
}
