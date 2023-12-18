package com.github.cd721.data_leakage_plugin.data;

public class Invocation {

    private final int number;

    public Invocation(String invocationString) {
        number = Integer.parseInt(String.valueOf(invocationString.charAt(invocationString.length() - 1)));

    }


    public int getNumber() {
        return number;
    }
}
