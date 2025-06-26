package org.mio.progettoingsoft.network.messages;

import java.io.Serializable;

public sealed abstract class Message  implements Serializable permits ActivateSlaversMessage, AddCreditsMessage, AddCrewMessage, AddPlayerMessage, AdvanceCannonMessage, AdvanceMeteorMessage, AdvancePlayerMessage, ApplyEffectMessage, AvailablePlacesMessage, BatteryMessage, BuildShipMessage, CannonMessage, CardStateMessage, ChoiceErrorMessage, ChoosePlacementMessage, ComponentMessage, CrewRemoveMessage, DeckMessage, DoubleDrillMessage, DoubleEngineMessage, DrawCardMessage, EndBuildMessage, EndValidationMessage, FlyBoardMessage, GameIdMessage, GameInfoMessage, GoodMessage, LandOnPlanetMessage, LeaveMessage, MeteorMessage, NicknameMessage, RollDiceMessage, SkipEffectMessage, StardustMessage, StartHourglassMessage, StateMessage, WelcomeMessage, CrashMessage {

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
