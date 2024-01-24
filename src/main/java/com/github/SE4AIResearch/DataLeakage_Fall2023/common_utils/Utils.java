package com.github.SE4AIResearch.DataLeakage_Fall2023.common_utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Utils {

    //TODO: write better docs, remove circular dependency
    /**
     * Looks through "LinenoMapping.facts" to find the actual line number that corresponds to an internal line number.
     * The "actual line number" is the line number of the end user's code.
     * The "internal line number" is meaningless to the end user.
     * @param folderPath The location of the leakage analysis tool output that contains "InvokeLineno.facts".
     * @param internalLineNumber A number used within and provided by the leakage analysis tool.
     * @return An {@code int} representing an actual line number in the user's code.
     */
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

  }
