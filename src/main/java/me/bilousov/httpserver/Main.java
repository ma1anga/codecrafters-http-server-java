package me.bilousov.httpserver;

import me.bilousov.httpserver.server.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        final HttpServer httpServer = new HttpServer();
        httpServer.start();
    }
}