package org.mio.progettoingsoft.model.events;

public abstract sealed class Event permits MovePlayerEvent, AddCreditsEvent {
    private final String nickname;

    public Event(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
