package com.github.cd721.data_leakage_plugin.leakage_detectors;

import com.github.cd721.data_leakage_plugin.data.Invocation;
import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class LeakageDetector {
    public LeakageType leakageType;
    //TODO: remove debug
    private final boolean debug = true;

    /**
     * @return The name of the CSV file that contains relevant information
     * about the leakage of {@link #leakageType} of this detector.
     */
    public abstract String getCsvFileName();

    /**
     * @return The column of the CSV file (specified by {@link #getCsvFileName()}) that
     * contains the {@link Invocation} associated with this detector's {@link #leakageType}.
     */
    public abstract int getCsvInvocationColumn();

    /**
     * Adds a {@code LeakageInstance} to this detector's {@code leakageInstances()}.
     * @param instance The {@link LeakageInstance} to add.
     */
    public abstract void addLeakageInstance(LeakageInstance instance);

    /**
     * A {@link List<LeakageInstance>} representing containing the {@link LeakageInstance}s caught by this detector.
     * @return
     */
    public abstract List<LeakageInstance> leakageInstances();

    public LeakageDetector() {

    }

    /**
     * Looks through the CSV files that provide information about the provided {@code leakageType}
     * @param folderPath The location of the leakage analysis tool output.
     * @param leakageType A {@code LeakageType}; the type of leakage we are looking for.
     * @return A {@code List<LeakageInstance>} that contain any instances of leakage
     * matching the {@code leakageType}.
     */
    public List<LeakageInstance> FindLeakageInstances(String folderPath, LeakageType leakageType) {

        try {

            File file = new File(folderPath + this.getCsvFileName());
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(("\t"));
                Invocation invocation = new Invocation(columns[getCsvInvocationColumn()]);
                int internalLineNumber = getInternalLineNumberFromInvocation(folderPath, invocation);
                int actualLineNumber = getActualLineNumberFromInternalLineNumber(folderPath, internalLineNumber);

                var leakageInstance = new LeakageInstance(actualLineNumber, leakageType);

                var existingInstances = leakageInstances();
                if (!debug || !existingInstances.contains(leakageInstance)) {
                    addLeakageInstance(leakageInstance);
                }


            }

            return leakageInstances();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public abstract boolean isLeakageDetected();

    /**
     * Looks through "LinenoMapping.facts" to find the actual line number that corresponds to an internal line number.
     * The "actual line number" is the line number of the end user's code.
     * The "internal line number" is meaningless to the end user.
     * @param folderPath The location of the leakage analysis tool output that contains "InvokeLineno.facts".
     * @param internalLineNumber A number used within and provided by the leakage analysis tool.
     * @return An {@code int} representing an actual line number in the user's code. 
     */
    private int getActualLineNumberFromInternalLineNumber(String folderPath, int internalLineNumber) {
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
    private int getInternalLineNumberFromInvocation(String folderPath, Invocation invocation) {
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
