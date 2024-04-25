package me.bilousov.httpserver.util;

import java.util.Arrays;
import java.util.List;

public final class CliUtil {

    private static final String DIRECTORY_CLI_ARG_NAME = "--directory";


    private CliUtil() {}

    public static String getWorkingDirectoryFromCliArgs(String[] args) {
        final List<String> argsList = Arrays.asList(args);

        if (argsList.size() > 1) {
            if (argsList.getFirst().equals(DIRECTORY_CLI_ARG_NAME)) {
                return argsList.get(1);
            }
        }

        return null;
    }
}
