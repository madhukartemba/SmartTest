package com.madhukartemba.smarttest.util;

import java.awt.Color;

import com.madhukartemba.smarttest.entity.Parameters;

public class Printer {

    public static final String RESET = "\033[0m";
    public static final String BOLD = "\033[1m";
    public static final Color DEFAULT_COLOR_1 = Parameters.DEFAULT_COLOR_1.getValue();
    public static final String DEFAULT_COLOR_1_CODE = getColorCode(DEFAULT_COLOR_1);
    public static final Color DEFAULT_COLOR_2 = Parameters.DEFAULT_COLOR_2.getValue();
    public static final String DEFAULT_COLOR_2_CODE = getColorCode(DEFAULT_COLOR_2);
    public static final Color BUILD_SUCCESSFUL = Color.decode("#23D18B");
    public static final Color BUILD_IS_FAILING = Color.decode("#FFA500");
    public static final String SEPERATOR = ":";
    public static final String CLEAR_LINE = "\033[2K\r";

    public static void print(String str, Color color) {
        String colorCode = getColorCode(color);
        System.out.print(colorCode + str + RESET);
    }

    public static void println(String str, Color color) {
        String colorCode = getColorCode(color);
        System.out.println(colorCode + str + RESET);
    }

    public static void print(String str) {
        System.out.print(DEFAULT_COLOR_1_CODE + str + RESET);
    }

    public static void println(String str) {
        System.out.println(DEFAULT_COLOR_1_CODE + str + RESET);
    }

    public static void formatPrint(String str) {
        formatPrint(str, DEFAULT_COLOR_1, DEFAULT_COLOR_2);
    }

    public static void formatPrint(String str, Color color2) {
        formatPrint(str, DEFAULT_COLOR_1, color2);
    }

    public static void formatPrint(String str, Color color1, Color color2) {
        if (str == null || str.isEmpty() || !str.contains(SEPERATOR)) {
            println(str, color1);
            return;
        }
        String strs[] = str.split(SEPERATOR);
        print(strs[0] + SEPERATOR, color1);
        for (int i = 1; i < strs.length; i++) {
            print(strs[i], color2);
        }
        System.out.println();
    }

    public static void boldPrint(String str, Color color) {
        String colorCode = getColorCode(color);
        System.out.print(BOLD + colorCode + str + RESET);
    }

    public static void boldPrintln(String str, Color color) {
        String colorCode = getColorCode(color);
        System.out.println(BOLD + colorCode + str + RESET);
    }

    public static void boldPrint(String str) {
        System.out.print(BOLD + DEFAULT_COLOR_1_CODE + str + RESET);
    }

    public static void boldPrintln(String str) {
        System.out.println(BOLD + DEFAULT_COLOR_1_CODE + str + RESET);
    }

    public static void boldFormatPrint(String str) {
        boldFormatPrint(str, DEFAULT_COLOR_1, DEFAULT_COLOR_2);
    }

    public static void boldFormatPrint(String str, Color color2) {
        boldFormatPrint(str, DEFAULT_COLOR_1, color2);
    }

    public static void boldFormatPrint(String str, Color color1, Color color2) {
        if (str == null || str.isEmpty() || !str.contains(SEPERATOR)) {
            boldPrintln(str, color1);
            return;
        }
        String strs[] = str.split(SEPERATOR);
        boldPrint(strs[0] + SEPERATOR, color1);
        for (int i = 1; i < strs.length; i++) {
            boldPrint(strs[i], color2);
        }
        System.out.println();
    }

    public static void carriageReturn(int lines) {
        if (lines == 0) {
            return;
        }
        System.out.print("\033[" + lines + "A\r");
    }

    public static void clearLine() {
        System.out.print(CLEAR_LINE); // clear line and move cursor to the start
        System.out.flush(); // flush output buffer to console
    }

    public static String getColorCode(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        return String.format("\033[38;2;%d;%d;%dm", r, g, b);
    }
}
