package me.bilousov.httpserver.util;

import java.util.Arrays;
import java.util.List;

public final class ParseUtil {

    private static final String DIRECTORY_CLI_ARG_NAME = "--directory";


    private ParseUtil() {}

    public static String getWorkingDirectoryFromCliArgs(String[] args) {
        final List<String> argsList = Arrays.asList(args);

        if (argsList.size() > 1) {
            if (argsList.getFirst().equals(DIRECTORY_CLI_ARG_NAME)) {
                return argsList.get(1);
            }
        }

        return null;
    }

    public static int safeParseInt(String integer) {
        int parsedInteger = 0;

        try {
           parsedInteger = Integer.parseInt(integer);
        } catch (NumberFormatException e) {
            System.out.println("Failed to parse integer: " + integer + ". Value will be defaulted to 0. Error: " + e.getMessage());
        }

        return parsedInteger;
    }
}
