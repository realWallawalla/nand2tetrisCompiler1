package com.timonsarakinis.tokens.types;

import java.util.List;

public enum SymbolType {
    OPENING_BRACE("{"),
    CLOSE_BRACE("}"),
    OPEN_PARENTHESIS("("),
    CLOSE_PARENTHESIS(")"),
    OPEN_BRACKET("["),
    CLOSE_BRACKET("]"),
    DOT("."),
    COMMA(","),
    SEMICOLON(";"),
    STAR("*"),
    HIPHON("-"),
    PLUS("+"),
    SLASH("/"),
    AMPERSAND("&"),
    VERTICAL_BAR("|"),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    EQUALS("="),
    TILDE("~");

    private final String character;

    SymbolType(String character) {
        this.character = character;
    }

    public String getCharacter() {
        return character;
    }
}