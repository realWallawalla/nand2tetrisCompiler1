package com.timonsarakinis.utils;

import com.timonsarakinis.tokens.Token;
import com.timonsarakinis.tokens.types.KeywordType;
import com.timonsarakinis.tokens.types.SymbolType;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.timonsarakinis.tokens.types.SymbolType.*;
import static com.timonsarakinis.tokens.types.TokenType.*;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.EnumUtils.isValidEnumIgnoreCase;
import static org.apache.commons.lang3.StringUtils.*;

public class TokenUtils {
    public static final String PLACE_HOLDER = "^";

    public static String removeNoneTokens(List<String> lines) {
        String unrefinedTokens = lines.stream()
                .filter(line -> isNotBlank(line) || !line.startsWith("/"))
                .map(line -> replaceIgnoreCase(line, "\t", " "))
                .map(line -> substringBefore(line, "//"))
                .map(String::trim)
                .collect(joining(" "));

        String removeApiComments = "/\\*([^*]|[\\r\\n]|(\\*+([^*/]|[\\r\\n])))*\\*+/";
        return unrefinedTokens.replaceAll(removeApiComments, "");
    }

    public static List<String> splitIntoTokens(String lines) {
        lines = prepareStringConstants(lines);
        lines = addSpacesToSymbolsForSplit(lines);

        return Stream.of(lines.split(" "))
                .filter(StringUtils::isNotBlank)
                .map(stringConstants -> stringConstants.replace(PLACE_HOLDER, " "))
                .collect(Collectors.toList());
    }

    private static String prepareStringConstants(String lines) {
        int numberOfStringConstants = countMatches(lines, "\"") / 2;

        for (int i = 0; i < numberOfStringConstants; i++) {
            String stringConstant = substringBetween(lines, "\"", "\"");
            String placeHolder = substringBetween(lines, "\"", "\"")
                    .replace(" ", PLACE_HOLDER);
            lines = lines.replace(stringConstant, placeHolder);
        }
        return lines;
    }

    private static String addSpacesToSymbolsForSplit(String lines) {
        return Stream.of(lines.split(" "))
                .map(s -> s.replace(";", (" " + SEMICOLON.getCharacter()) + " "))
                .map(s -> s.replace("&", (" " + AMPERSAND.getCharacter()) + " "))
                .map(s -> s.replace(",", (" " + COMMA.getCharacter()) + " "))
                .map(s -> s.replace("(", (" " + OPEN_PARENTHESIS.getCharacter()) + " "))
                .map(s -> s.replace(")", (" " + CLOSE_PARENTHESIS.getCharacter()) + " "))
                .map(s -> s.replace(".", (" " + DOT.getCharacter()) + " "))
                .map(s -> s.replace("<", (" " + LESS_THAN.getCharacter()) + " "))
                .map(s -> s.replace(">", (" " + GREATER_THAN.getCharacter()) + " "))
                .map(s -> s.replace("-", (" " + HIPHON.getCharacter()) + " "))
                .map(s -> s.replace("[", (" " + OPEN_BRACKET.getCharacter()) + " "))
                .map(s -> s.replace("]", (" " + CLOSE_BRACKET.getCharacter()) + " "))
                .map(s -> s.replace("~", (" " + TILDE.getCharacter()) + " "))
                .map(String::trim)
                .collect(joining(" "));
    }

    public static String FindTokenType(String token) {
        if (SymbolType.getCharacters().contains(token)) {
            return SYMBOL.toString();
        } else if (isValidEnumIgnoreCase(KeywordType.class, token)) {
            return KEYWORD.toString();
        } else if (StringUtils.isNumeric(token)) {
            return INT_CONST.toString();
        } else if (token.indexOf("\"") == 0) {
            return STRING_CONST.toString();
        } else {
            return IDENTIFIER.toString();
        }
    }

    public static byte[] prepareForOutPut(Token token) {
        String node = token.getTokenType().getNodeName();
        String value = token.getValue();

        return "<".concat(node).concat(">").concat(value).concat("</").concat(node).concat(">").concat("\n").getBytes(StandardCharsets.UTF_8);
    }
}
