package org.mio.progettoingsoft.network.messages;

import java.io.Serializable;

public sealed abstract class Message  implements Serializable permits ActivateSlaversMessage, AddCreditsMessage, AddPlayerMessage, AdvanceMeteorMessage, AdvancePlayerMessage, ApplyEffectMessage, AvailablePlacesMessage, BatteryMessage, BuildShipMessage, CardStateMessage, ChoiceErrorMessage, ChoosePlacementMessage, ComponentMessage, CrewRemoveMessage, DeckMessage, DoubleDrillMessage, DoubleEngineMessage, DrawCardMessage, EndBuildMessage, FlyBoardMessage, GameIdMessage, GameInfoMessage, GoodMessage, LandOnPlanetMessage, LeaveMessage, MeteorMessage, NicknameMessage, RollDiceMessage, SkipEffectMessage, StardustMessage, StateMessage, WelcomeMessage {

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
