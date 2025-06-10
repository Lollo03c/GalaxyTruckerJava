package org.mio.progettoingsoft.network.messages;

import java.io.Serializable;

public sealed abstract class Message  implements Serializable permits AddCreditsMessage, AddPlayerMessage, AdvancePlayerMessage, AvailablePlacesMessage, BuildShipMessage, CardStateMessage, ChoosePlacementMessage, ComponentMessage, CrewLostMessage, DeckMessage, DoubleEngineMessage, DrawCardMessage, EndBuildMessage, FlyBoardMessage, GameIdMessage, GameInfoMessage, LeaveMessage, NicknameMessage, StardustMessage, StateMessage, WelcomeMessage {

    private final int gameId;
    private final String nickname;

    public Message(int gameId, String nickname) {
        this.gameId = gameId;
        this.nickname = nickname;
    }

    public int getGameId() {
        return gameId;
    }

    public String getNickname() {
        return nickname;
    }
}
