package com.madhukartemba.smarttest.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class FileCleaner {
    /**
     * This function will accept a file path and will return a string after removing
     * all the comments and strings from the file.
     * 
     * @param path
     * @return Cleaned file as a string.
     * @throws IOException
     */
    public static String clean(Path path) throws IOException {
        // Read the input file into a string
        String input = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);

        // Define the regular expression pattern to match comments and string literals
        Pattern pattern = Pattern.compile(
                "\"[^\"\\\\]*(\\\\.[^\"\\\\]*)*\"|'[^'\\\\]*(\\\\.[^'\\\\]*)*'|/\\*.*?\\*/|//.*?$",
                Pattern.DOTALL | Pattern.MULTILINE);

        // Remove all comments and string literals from the input string
        return pattern.matcher(input).replaceAll("");
    }

}
