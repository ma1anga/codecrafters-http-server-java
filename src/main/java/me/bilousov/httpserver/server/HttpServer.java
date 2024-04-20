package me.bilousov.httpserver.server;

import me.bilousov.httpserver.handler.HttpRequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    private static final int DEFAULT_PORT = 4221;

    public void start() {
        try (final ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT)) {
            serverSocket.setReuseAddress(true);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new HttpRequestHandler(clientSocket).start();
            }
        } catch (IOException exception) {
            System.out.println("Error creating server socket: " + exception.getMessage());
        }
    }
}
