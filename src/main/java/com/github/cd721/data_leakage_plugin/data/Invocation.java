package com.github.cd721.data_leakage_plugin.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Invocation {

    private final int number;
    private final Pattern invocationPattern = Pattern.compile("(\\$)(invo)([0-9]+)");
    //TODO: test
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
