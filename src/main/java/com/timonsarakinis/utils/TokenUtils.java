package com.timonsarakinis.utils;

import com.google.common.base.Splitter;
import com.timonsarakinis.tokens.Token;
import com.timonsarakinis.tokens.types.KeywordType;
import com.timonsarakinis.tokens.types.SymbolType;
import com.timonsarakinis.tokens.types.TokenType;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.timonsarakinis.tokens.types.TokenType.*;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.*;

public class TokenUtils {

    public static final int FALSE = -1;

    public static String removeNoneTokens(List<String> lines) {
        return lines.stream()
                .filter(line -> isNotBlank(line)
                || !line.startsWith("/")
                || !line.endsWith("*/"))
                .map(line -> replaceIgnoreCase(line, "\t", " "))
                .map(line -> substringBefore(line, "//"))
                .map(String::trim)
                .collect(joining(" "));
    }

    public static List<String> splitIntoTokens(String lines) {
        int startIndex = lines.indexOf("/**");
        int stopIndex = lines.indexOf("*/");

        if (startIndex != FALSE) {
            StringBuilder builder = new StringBuilder(lines);
            lines = builder.delete(startIndex, stopIndex+2).toString();
        }

        return Splitter.on(" ")
                .trimResults()
                .omitEmptyStrings()
                .splitToList(lines);
    }

    public static String FindTokenType(String token) {
        if (SymbolType.getCharacters().contains(token)) {
            return SYMBOL.toString();
        } else if (EnumUtils.isValidEnumIgnoreCase(KeywordType.class, token)) {
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
        String node = token.getTokenType().toString().toLowerCase();
        String value = token.getValue();

        return "<".concat(node).concat(">").concat(value).concat("</").concat(node).concat(">").concat("\n").getBytes(StandardCharsets.UTF_8);
    }
}
