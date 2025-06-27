package org.mio.progettoingsoft.model.advCards;

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

    public String criterionToString() {
        return switch (this) {
            case CREW -> "Crew";
            case FIRE_POWER -> "Fire power";
            case ENGINE_POWER -> "Engine power";
        };
    }
}
