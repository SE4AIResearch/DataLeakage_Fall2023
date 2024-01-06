package com.github.cd721.data_leakage_plugin.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is meant to represent Strings of the form "$invo1" that are present in the CSV files of the leakage anaylsis tool.
 */
public class Invocation {

    private final int number;
    private final Pattern invocationPattern = Pattern.compile("(\\$)(invo)([0-9]+)");
    //TODO: test

    /**
     * Checks whether a String is of the form "$invo{number}". If the String is of this form, the {@link  #number} field is
     * set accordingly. If the String is not of the form "$invo{number}", the {@link #number} field is set to 0.
     * @param invocationString A string believed to be of the form "$invo{number}".
     */
    public Invocation(String invocationString) {
        Matcher matcher = invocationPattern.matcher(invocationString);
        boolean matchFound = matcher.find();

        if(matchFound) {
            String numbersPortion = matcher.group(3);
            number = Integer.parseInt(numbersPortion);

        } else {
            number = 0;
        }

    }


    public int getNumber() {
        return number;
    }
}
