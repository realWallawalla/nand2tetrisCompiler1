package com.timonsarakinis.tokens.types;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum SymbolType {
    OPEN_BRACE("{"),
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
    AMPERSAND("&amp;"),
    VERTICAL_BAR("|"),
    LESS_THAN("&lt;"),
    GREATER_THAN("&gt;"),
    EQUALS("="),
    TILDE("~");

    private final String character;

    SymbolType(String character) {
        this.character = character;
    }

    public String getCharacter() {
        return character;
    }

    public static List<String> getCharacters() {
        return Arrays.stream(SymbolType.values()).map(SymbolType::getCharacter).collect(Collectors.toList());
    }
}