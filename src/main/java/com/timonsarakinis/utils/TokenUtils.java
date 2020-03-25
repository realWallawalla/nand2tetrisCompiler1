package com.timonsarakinis.utils;

import com.google.common.base.Splitter;

import java.util.List;

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
}
