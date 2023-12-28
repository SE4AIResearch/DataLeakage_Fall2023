package com.github.cd721.data_leakage_plugin.leakage_detectors;

import com.github.cd721.data_leakage_plugin.data.Invocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Utils {

    //TODO: write better docs
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

    /**
     * Looks through "InvokeLineno.facts" to find a number that corresponds to a particular invocation.
     * The "internal line number" is provided by the leakage analysis tool and shall not be presented to the end user.
     * @param folderPath The location of the leakage analysis tool output that contains "InvokeLineno.facts".
     * @param invocation An {@code Invocation}. This would appear in the leakage analysis tool output as "$invo2", for example.
     * @return An {@code int} representing a number provided by the leakage analysis tool. The number is meaningless to the end user, but may be used by other functions in the plugin code.
     */
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
