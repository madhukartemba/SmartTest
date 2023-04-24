package com.madhukartemba.smarttest.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class FileCleaner {

    private static ConcurrentHashMap<Path, String> fileToCleanOutputCache = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("input.txt");
        System.out.println(clean(path));
    }

    /**
     * This function will accept a file path and will return a string after removing
     * all the comments and strings from the file.
     * 
     * @param path
     * @return Cleaned file as a string.
     * @throws IOException
     */
    public static String clean(Path path) throws IOException {

        if (fileToCleanOutputCache.containsKey(path)) {
            return fileToCleanOutputCache.get(path);
        }

        // Read the input file into a string
        String input = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);

        // Define the regular expression pattern to match comments, string literals and
        // endline characters.
        Pattern pattern = Pattern.compile(
                "\"[^\"\\\\]*(\\\\.[^\"\\\\]*)*\"|'[^'\\\\]*(\\\\.[^'\\\\]*)*'|/\\*.*?\\*/|//.*?$|\n",
                Pattern.DOTALL | Pattern.MULTILINE);

        // Remove all comments, string literals and endlines from the input string
        String output = pattern.matcher(input).replaceAll("");

        fileToCleanOutputCache.put(path, output);

        return output;
    }

    /**
     * This function will accept a file path and will return a string after removing
     * all the comments and strings from the file.
     * 
     * @param input string.
     * @return Cleaned file as a string.
     * @throws IOException
     */
    public static String clean(String input) throws IOException {

        // Define the regular expression pattern to match comments, string literals and
        // endline characters.
        Pattern pattern = Pattern.compile(
                "\"[^\"\\\\]*(\\\\.[^\"\\\\]*)*\"|'[^'\\\\]*(\\\\.[^'\\\\]*)*'|/\\*.*?\\*/|//.*?$|\n",
                Pattern.DOTALL | Pattern.MULTILINE);

        // Remove all comments, string literals and endlines from the input string
        return pattern.matcher(input).replaceAll("");
    }

}
