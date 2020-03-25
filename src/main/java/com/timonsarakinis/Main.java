package com.timonsarakinis;

import com.timonsarakinis.utils.IOUtils;
import com.timonsarakinis.tokenizer.JackTokenizer;
import com.timonsarakinis.utils.TokenUtils;

import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String filePath = "src/main/resources/";
        //List<Path> filePaths = FileReaderWriter.getPaths(args[0]);
        IOUtils.createDirectory();
        List<Path> filePaths = IOUtils.getPaths(filePath);
        filePaths.forEach(Main::prepareForTokenization);
    }

    private static void prepareForTokenization(Path path) {
        List<String> lines = IOUtils.readFile(path);
        String tokens = TokenUtils.removeNoneTokens(lines);
        tokenize(TokenUtils.splitIntoTokens(tokens));
    }

    private static void tokenize(List<String> tokens) {
        JackTokenizer jackTokenizer = new JackTokenizer(tokens);
        while (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
            String currentToken = jackTokenizer.getCurrentToken();
        }
    }
}
