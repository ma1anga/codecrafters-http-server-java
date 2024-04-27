package me.bilousov.httpserver;

import me.bilousov.httpserver.server.HttpServer;
import me.bilousov.httpserver.util.ParseUtil;

public class Main {

    public static void main(String[] args) {
        final HttpServer httpServer = new HttpServer();
        httpServer.start(ParseUtil.getWorkingDirectoryFromCliArgs(args));
    }
}