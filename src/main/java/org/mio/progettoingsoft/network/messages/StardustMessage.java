package org.mio.progettoingsoft.network.messages;

import org.mio.progettoingsoft.model.advCards.sealed.SldStardust;

public final class StardustMessage extends Message {
    private final SldStardust card;
    public StardustMessage(int idGame, String nickname, SldStardust card) {
        super(idGame, nickname);
        this.card = card;
    }
    public SldStardust getCard() {
        return card;
    }
}
