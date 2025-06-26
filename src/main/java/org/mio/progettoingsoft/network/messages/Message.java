package org.mio.progettoingsoft.network.messages;

import java.io.Serializable;

/**
 * An abstract sealed class representing a generic message exchanged over the network
 * in the Galaxy Truckers game. This class serves as the base for all specific message types.
 * It includes common fields such as a game ID and a nickname, which are typically
 * associated with a game action or event.
 *
 * <p>This is a sealed class, meaning its direct subclasses are restricted to the
 * ones explicitly listed in the {@code permits} clause.</p>
 */
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
