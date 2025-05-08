package org.mio.progettoingsoft.network.input;

public final class StringInput extends Input {
    private final String string;

    public StringInput(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }
}
