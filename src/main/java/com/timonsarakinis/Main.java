package com.timonsarakinis;

import com.timonsarakinis.tokenizer.JackTokenizer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import static com.timonsarakinis.utils.IOUtils.*;
import static com.timonsarakinis.utils.TokenUtils.*;

public class Main {
    public static void main(String[] args) {
        //List<Path> filePaths = FileReaderWriter.getPaths(args[0]);
        createDirectory();
        String directory = "src/main/resources/";
        List<Path> filePaths = getPaths(directory);
        filePaths.forEach(Main::prepareForTokenization);
    }

    private static void prepareForTokenization(Path path) {
        String fileName = extractFileName(path.getFileName().toString());
        deleteFile(getOutputPath(fileName));
        List<String> lines = readFile(path);
        String tokens = removeNoneTokens(lines);
        tokenize(splitIntoTokens(tokens), fileName);
    }

    private static void tokenize(List<String> tokens, String fileName) {
        JackTokenizer jackTokenizer = new JackTokenizer(tokens);
        String root = "tokens";
        writeRootNode(root, fileName, true);
        while (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
            byte[] tokenInXml = prepareForOutPut(jackTokenizer.getCurrentToken());
            writeToFile(tokenInXml, fileName);
        }
        writeRootNode(root, fileName, false);
        System.out.printf("wrote output successfully to file: %s \n", fileName);
    }

    private static void writeRootNode(String rootNode, String fileName, boolean openTag) {
        byte[] rootXml;
        if (openTag) {
            rootXml = "<".concat(rootNode).concat(">").concat("\n").getBytes(StandardCharsets.UTF_8);
        } else {
            rootXml = "</".concat(rootNode).concat(">").getBytes(StandardCharsets.UTF_8);
        }
        writeToFile(rootXml, fileName);
    }
}
