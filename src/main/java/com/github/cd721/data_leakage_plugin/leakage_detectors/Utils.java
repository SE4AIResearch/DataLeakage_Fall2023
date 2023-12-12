package com.github.cd721.data_leakage_plugin.leakage_detectors;

import com.github.cd721.data_leakage_plugin.data.Invocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Utils {

    //TODO: write better docs
    public static int getActualLineNumberFromInternalLineNumber(String folderPath, int internalLineNumber) {
        File file = new File(folderPath + "LinenoMapping.facts");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;

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

    public static int getInternalLineNumberFromInvocation(String folderPath, Invocation invocation) {
        File file = new File(folderPath + "InvokeLineno.facts");
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
}
