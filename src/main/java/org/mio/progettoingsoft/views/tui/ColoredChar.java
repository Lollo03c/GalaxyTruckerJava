package org.mio.progettoingsoft.views.tui;

public class ColoredChar {
    public char c;
    public String color;
    public static final String WHITE = "\u001B[37m";

    public ColoredChar(char c) {
        this.c = c;
        color = WHITE;
    }

    public void setColor(String color){
        this.color = color;
    }
    public void setChar(char c){
        this.c = c;
    }
    public ColoredChar(char c, String color) {
        this.c = c;
        this.color = color;
    }

    @Override
    public String toString() {
        final String RESET = "\u001B[0m";
        return color + c + RESET;
    }
}

