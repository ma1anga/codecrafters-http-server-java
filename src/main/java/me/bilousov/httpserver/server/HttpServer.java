package me.bilousov.httpserver.server;

import me.bilousov.httpserver.handler.HttpRequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Objects;

public class HttpServer {

    private static final int DEFAULT_PORT = 4221;

    public void start(String workingDirectory) {
        final Path workingDirPath = getWorkingDirPath(workingDirectory);

        System.out.println("Starting with working directory: " + workingDirPath.toString());

        try (final ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT)) {
            serverSocket.setReuseAddress(true);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new HttpRequestHandler(clientSocket, workingDirPath).start();
            }
        } catch (IOException exception) {
            System.out.println("Error creating server socket: " + exception.getMessage());
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
