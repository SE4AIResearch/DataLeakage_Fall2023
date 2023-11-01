package com.github.cd721.data_leakage_plugin;

import com.github.cd721.data_leakage_plugin.listeners.FileChangeDetector;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;

import java.io.*;
import java.util.Scanner;

public class OverlapLeakageDetector {


    public boolean OverlapLeakageDetected() {
        int count = 0;
        try {

            File file = new File("C:/dev/paper-sample-2-fact/FinalOverlapLeak.csv");
            BufferedReader reader =  new BufferedReader(new FileReader(file));
            while(reader.readLine()!=null) {
                count++;
            }

            return count > 0;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
