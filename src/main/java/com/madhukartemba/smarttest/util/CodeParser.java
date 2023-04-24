package com.madhukartemba.smarttest.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeParser {

    Pattern pattern;

    public CodeParser(String keyword) {
        pattern = Pattern.compile("[^a-zA-Z0-9_$]" + keyword + "[^a-zA-Z0-9_$]");
    }

    public boolean containsKeyword(String input) {
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

}