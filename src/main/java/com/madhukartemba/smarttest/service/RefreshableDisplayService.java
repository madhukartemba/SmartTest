package com.madhukartemba.smarttest.service;

import java.util.ArrayList;
import java.util.List;

import com.madhukartemba.smarttest.util.Printer;

public class RefreshableDisplayService {
    protected List<String> printLines;
    protected int lineCount = 0;

    public RefreshableDisplayService() {
        this.printLines = new ArrayList<>();
        this.lineCount = 0;
    }

    public void update(List<String> lines) {
        returnToStart();
        this.printLines.clear();
        this.printLines.addAll(lines);
        print();
    }

    public void returnToStart() {
        Printer.carriageReturn(lineCount);
    }

    public void print() {
        for (String printLine : printLines) {
            Printer.formatPrint(printLine);
        }
    }

}
