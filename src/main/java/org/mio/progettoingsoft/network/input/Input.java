package org.mio.progettoingsoft.network.input;

public abstract sealed class Input permits ComponentInput, EmptyInput, SetupInput, StringInput {
}
