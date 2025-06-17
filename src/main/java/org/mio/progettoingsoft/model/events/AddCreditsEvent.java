package org.mio.progettoingsoft.model.events;

public final class AddCreditsEvent extends Event{
    private final int addedCredits;
    public AddCreditsEvent(String nickname , int newCreditsValue) {
        super(nickname);
        this.addedCredits = newCreditsValue;
    }
    public int getAddedCredits() {
        return addedCredits;
    }
}
