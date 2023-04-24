package com.madhukartemba.smarttest.util;

public class ProcessMonitor {
    public static void main(String[] args) throws InterruptedException {
        // simulate some processes
        int[] processIds = { 1, 2, 3 };
        String[] processStates = { "IN_QUEUE", "IN_QUEUE", "IN_QUEUE" };

        for (int i = 0; i < 10000; i++) {
            // simulate some processing time
            Thread.sleep(1);

            // update the state of the current process
            processStates[i%processIds.length] =  "RUNNING " + Math.random();

            // print the current states on one line
            for (int j = 0; j < processIds.length; j++) {
                System.out.println("Process " + j + " :" + processStates[j] + "                        ");
            }
            System.out.print("\033[3A\r");

        }

        // print a newline to separate the final output
        System.out.println("\n\n\n");

        // print the final states on one line
        System.out.printf("Final States: %s, %s, %s\n", processStates[0], processStates[1], processStates[2]);
    }
}
