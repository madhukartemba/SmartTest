package com.madhukartemba.smarttest.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeParser {

    // Regular expression pattern to match instances of a given keyword surrounded
    // by non-alphanumeric characters
    Pattern pattern;

    /**
     * Constructs a CodeParser object with a regular expression pattern that matches
     * instances of a given keyword
     * surrounded by non-alphanumeric characters.
     * 
     * @param keyword the keyword to search for
     */
    public CodeParser(String keyword) {
        this.pattern = Pattern.compile("[^a-zA-Z0-9_$]" + keyword + "[^a-zA-Z0-9_$]");
    }

    public CodeParser(Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Checks whether the input contains the keyword that was specified in the
     * constructor.
     * 
     * @param input the input string to search for the keyword in
     * @return true if the input contains the keyword, false otherwise
     */
    public boolean containsKeyword(String input) {
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

}
