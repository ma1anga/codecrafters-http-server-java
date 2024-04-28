package me.bilousov.httpserver;

import me.bilousov.httpserver.handler.HttpRequestHandler;
import me.bilousov.httpserver.handler.impl.EchoRequestHandler;
import me.bilousov.httpserver.handler.impl.EmptyRequestHandler;
import me.bilousov.httpserver.handler.impl.FilesRequestHandler;
import me.bilousov.httpserver.handler.impl.UserAgentRequestHandler;
import me.bilousov.httpserver.server.HttpServer;
import me.bilousov.httpserver.util.ParseUtil;

import java.util.HashMap;
import java.util.Map;

public class Main {

    private static final int DEFAULT_PORT = 4221;

    private static final String EMPTY_REQUEST_PATTERN = "^/$";
    private static final String ECHO_REQUEST_PATTERN = "^/echo/(.*)$";
    private static final String USER_AGENT_REQUEST_PATTERN = "^/user-agent$";
    private static final String FILES_AGENT_REQUEST_PATTERN = "^/files/(.*)$";

    public static void main(String[] args) {
        final HttpServer httpServer = new HttpServer(
                getRequestHandlerMappings(),
                ParseUtil.getWorkingDirectoryFromCliArgs(args),
                DEFAULT_PORT
        );

        httpServer.start();
    }

    private static Map<String, HttpRequestHandler> getRequestHandlerMappings() {
        final var mapping = new HashMap<String, HttpRequestHandler>();
        mapping.put(EMPTY_REQUEST_PATTERN, new EmptyRequestHandler());
        mapping.put(ECHO_REQUEST_PATTERN, new EchoRequestHandler());
        mapping.put(USER_AGENT_REQUEST_PATTERN, new UserAgentRequestHandler());
        mapping.put(FILES_AGENT_REQUEST_PATTERN, new FilesRequestHandler());

        return mapping;
    }
}