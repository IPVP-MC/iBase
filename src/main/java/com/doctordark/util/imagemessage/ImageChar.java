package com.doctordark.util.imagemessage;

/**
 * @author bobacadodl
 */
public enum ImageChar {

    BLOCK('\u2588'),
    DARK_SHADE('\u2593'),
    MEDIUM_SHADE('\u2592'),
    LIGHT_SHADE('\u2591');

    private final char character;

    ImageChar(char character) {
        this.character = character;
    }

    public char getChar() {
        return character;
    }
}