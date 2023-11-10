package com.github.cd721.data_leakage_plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class OverlapLeakageDetector extends LeakageDetector{

    public OverlapLeakageDetector(){
        super();
        this.leakageType= LeakageType.OverlapLeakage;
    }


    @Override
    public int CountInstances() {
        int count = 0;
        try {

            File file = new File(folderPath + "FinalOverlapLeak.csv");
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line = "";
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(("\t"));
                Invocation invocation = new Invocation(columns[2]);
                int internalLineNumber = getInternalLineNumberFromInvocation(invocation);
                int actualLineNumber = getActualLineNumberFromInternalLineNumber(internalLineNumber);
                count++;
            }

            return count;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int getActualLineNumberFromInternalLineNumber(int internalLineNumber) {
        File file = new File(this.folderPath + "LinenoMapping.facts");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line = "";

            while (((line = reader.readLine()) != null)) {

                String[] columns = line.split(("\t"));

                if (Integer.parseInt(columns[0]) == internalLineNumber) {

                    return Integer.parseInt(columns[1]);
                }


            }

            return 0;


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int getInternalLineNumberFromInvocation(Invocation invocation) {
        File file = new File(this.folderPath + "InvokeLineno.facts");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            int lineNumber = invocation.getNumber() + 1;
            String line = "";

            for (int i = 1; i <= lineNumber; i++) {
                line = reader.readLine();

            }
            String[] columns = line.split(("\t"));
            return Integer.parseInt(columns[1]);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private class Invocation {
        private int number;

        public Invocation(String invocationString) {
            number = Integer.parseInt(String.valueOf(invocationString.charAt(invocationString.length() - 1)));
        }

        public int getNumber() {
            return number;
        }
    }



}
