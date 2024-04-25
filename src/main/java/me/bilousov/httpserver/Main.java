package me.bilousov.httpserver;

import me.bilousov.httpserver.server.HttpServer;
import me.bilousov.httpserver.util.CliUtil;

import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        final HttpServer httpServer = new HttpServer();
        httpServer.start(CliUtil.getWorkingDirectoryFromCliArgs(args));
    }
}