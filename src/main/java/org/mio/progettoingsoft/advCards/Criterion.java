package org.mio.progettoingsoft.advCards;

public enum Criterion {
    CREW, FIRE_POWER, ENGINE_POWER;

    public static Criterion stringToCriterion(String c) {
        return switch(c) {
            case "CREW" -> Criterion.CREW;
            case "FIRE_POWER" -> Criterion.FIRE_POWER;
            case "ENGINE_POWER" -> Criterion.ENGINE_POWER;
            default -> Criterion.CREW;
        };
    }
}
