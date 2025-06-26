package org.mio.progettoingsoft.model.events;

import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.utils.Logger;

import java.util.Map;

/**
 * An abstract sealed class representing a generic game event.
 * Events are used to communicate state changes or actions within the game
 * model and to propagate these changes to connected clients.
 *
 * All events are associated with a {@code nickname}, typically representing
 * the player who initiated or is affected by the event.
 */
public abstract sealed class Event permits AddCreditsEvent, AddCrewEvent, AddGoodEvent, AddPendingGoodEvent, AddPlayerCircuit, AskLeaveEvent, CannonHitEvent, GenericErrorEvent, LandOnPlanetEvent, LeavePlayerEvent, MetoriteEvent, MovePlayerEvent, RemoveComponentEvent, RemoveEnergyEvent, RemoveGoodEvent, RemoveGuestEvent, RemovePendingGoodEvent, SetCardPlayedEvent, SetCardStateEvent, SetPlayedCard, SetStateEvent, CrashEvent {
    protected final String nickname;

    /**
     * Constructs a new Event with the specified player nickname.
     *
     * @param nickname The nickname of the player associated with this event.
     * Can be {@code null} if the event is not player-specific.
     */
    public Event(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Returns the nickname of the player associated with this event.
     *
     * @return The player's nickname, or {@code null} if not applicable.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Default method for sending the event to clients. This method should ideally
     * be overridden by concrete event subclasses to provide specific logic for
     * how that event is handled by {@link VirtualClient}s.
     *
     * @param clients A map of connected clients, where the key is the client's
     * nickname and the value is the {@link VirtualClient} object.
     */
    public void send(Map<String, VirtualClient> clients){
        Logger.error("Event not supported.");
    }
}
