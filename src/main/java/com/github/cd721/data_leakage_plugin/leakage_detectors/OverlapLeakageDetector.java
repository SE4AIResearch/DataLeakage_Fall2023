package com.github.cd721.data_leakage_plugin.leakage_detectors;

import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OverlapLeakageDetector extends LeakageDetector {

    public OverlapLeakageDetector(){
        super();
        this.leakageType= LeakageType.OverlapLeakage;
    }

    @Override
    public List<LeakageInstance> FindLeakageInstances() {
        int count = 0;
        List<LeakageInstance> instances = new ArrayList<>();
        try {

            File file = new File(folderPath + "FinalOverlapLeak.csv");
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line ;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(("\t"));
                Invocation invocation = new Invocation(columns[2]);
                int internalLineNumber = getInternalLineNumberFromInvocation(invocation);
                int actualLineNumber = getActualLineNumberFromInternalLineNumber(internalLineNumber);

                instances.add(new LeakageInstance(actualLineNumber,LeakageType.OverlapLeakage));
                count++;
            }

            return instances;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int CountInstances() {
        int count = 0;
        try {

            File file = new File(folderPath + "FinalOverlapLeak.csv");
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
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

    @Override
    public List<Integer> FindLineNumbers(){
        List<Integer> lineNumbers = new ArrayList<>();
        try {

            File file = new File(folderPath + "FinalOverlapLeak.csv");
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(("\t"));
                Invocation invocation = new Invocation(columns[2]);
                int internalLineNumber = getInternalLineNumberFromInvocation(invocation);
                int actualLineNumber = getActualLineNumberFromInternalLineNumber(internalLineNumber);
                lineNumbers.add(actualLineNumber);
            }

            return lineNumbers;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int getActualLineNumberFromInternalLineNumber(int internalLineNumber) {
        File file = new File(this.folderPath + "LinenoMapping.facts");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line ;

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

    private static class Invocation {
        private final int number;

        public Invocation(String invocationString) {
            number = Integer.parseInt(String.valueOf(invocationString.charAt(invocationString.length() - 1)));
        }

        public int getNumber() {
            return number;
        }
    }



}
