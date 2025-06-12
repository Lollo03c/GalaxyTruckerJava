package org.mio.progettoingsoft.model.events;

public final class MovePlayerEvent extends Event {
    private final int steps;

    public MovePlayerEvent(String nickname, int steps) {
        super(nickname);
        this.steps = steps;
    }

    public int getSteps() {
        return steps;
    }
}
