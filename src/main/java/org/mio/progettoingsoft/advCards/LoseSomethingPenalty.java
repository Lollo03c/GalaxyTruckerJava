package org.mio.progettoingsoft.advCards;

public abstract class LoseSomethingPenalty extends Penalty{
    protected int amount;

    public LoseSomethingPenalty(int amount) {
        this.amount = amount;
    }

    public static LoseSomethingPenalty stringToPenalty(String p, int amount) {
        return switch (p) {
            case "daysLost" -> new LoseDaysPenalty(amount);
            case "crewLost" -> new LoseCrewPenalty(amount);
            case "goodsLost" -> new LoseGoodsPenalty(amount);
            default -> null;
        };
    }
}
