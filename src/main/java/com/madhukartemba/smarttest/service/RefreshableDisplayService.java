package com.madhukartemba.smarttest.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.madhukartemba.smarttest.util.Printer;

public class RefreshableDisplayService {
    protected List<String> printLines;
    protected int lineCount = 0;

    public static void main(String[] args) throws Exception {
        RefreshableDisplayService refreshableDisplayService = new RefreshableDisplayService();
        List<String> content1 = Arrays.asList("Hello", "this is some example text", "a test for the display");
        List<String> content2 = Arrays.asList("Hi", "I have updated the text", "for the display", "does it work?");

        for (int i = 0; i < 10; i++) {
            List<String> content = i % 2 == 0 ? content1 : content2;
            refreshableDisplayService.update(content);
            Thread.sleep(1000);
        }

    }

    public RefreshableDisplayService() {
        this.printLines = new ArrayList<>();
        this.lineCount = 0;
    }

    public synchronized void update(List<String> lines) {
        reset();
        setPrintLines(lines);
        print();
    }

    public void reset() {
        returnToStart();
        for (int i = 0; i < lineCount; i++) {
            Printer.clearLine();
            Printer.println("");
        }
        returnToStart();
        this.printLines.clear();
    }

    protected void setPrintLines(List<String> lines) {
        this.printLines.clear();
        this.printLines.addAll(lines);
        this.lineCount = printLines.size();
    }

    protected void returnToStart() {
        if (lineCount == 0) {
            return;
        }
        Printer.carriageReturn(lineCount);
    }

    protected void print() {
        for (String printLine : printLines) {
            Printer.clearLine();
            Printer.formatPrint(printLine);
        }
    }

}
