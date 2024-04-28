package me.bilousov.httpserver.server;

import me.bilousov.httpserver.dispatcher.HttpRequestProcessor;
import me.bilousov.httpserver.handler.HttpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

public class HttpServer {

    private static final Logger log = LoggerFactory.getLogger(HttpServer.class);

    private final Map<String, HttpRequestHandler> requestPathToHandlerMappings;
    private final Path workingDirPath;
    private final int port;


    public HttpServer(Map<String, HttpRequestHandler> requestPathToHandlerMappings, String workingDirectory, int port) {
        this.requestPathToHandlerMappings = requestPathToHandlerMappings;
        this.workingDirPath = getWorkingDirPath(workingDirectory);
        this.port = port;
    }

    public void start() {
        log.info("Starting with working directory: {} on port: {}", workingDirPath, port);

        try (final ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);

            //noinspection InfiniteLoopStatement
            while (true) {
                final Socket clientSocket = serverSocket.accept();

                new HttpRequestProcessor(
                        clientSocket,
                        workingDirPath,
                        requestPathToHandlerMappings
                ).start();
            }
        } catch (IOException exception) {
            log.error("Error creating server socket", exception);
        }
    }

    private Path getWorkingDirPath(String workingDirectory) {
        if (Objects.isNull(workingDirectory)) {
            return Path.of("");
        }

        final Path workingDirPath = Path.of(workingDirectory);

        if (Files.exists(workingDirPath) && Files.isDirectory(workingDirPath)) {
            return workingDirPath;
        } else {
            throw new InvalidPathException(workingDirectory, "Provided working directory does not exists or it is a file");
        }
    }
}
