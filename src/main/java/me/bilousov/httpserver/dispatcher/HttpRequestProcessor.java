package me.bilousov.httpserver.dispatcher;

import me.bilousov.httpserver.handler.HttpRequestHandler;
import me.bilousov.httpserver.handler.impl.DefaultRequestHandler;
import me.bilousov.httpserver.model.HttpRequest;
import me.bilousov.httpserver.model.HttpResponse;
import me.bilousov.httpserver.parser.HttpRequestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequestProcessor extends Thread {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestProcessor.class);

    private final Map<String, HttpRequestHandler> requestPathToHandlerMappings;
    private final Socket clientSocket;
    private final Path workingDirPath;

    private final HttpRequestParser httpRequestParser;


    public HttpRequestProcessor(Socket clientSocket, Path workingDirPath, Map<String, HttpRequestHandler> requestPathToHandlerMappings) {
        this.requestPathToHandlerMappings = requestPathToHandlerMappings;
        this.clientSocket = clientSocket;
        this.workingDirPath = workingDirPath;
        this.httpRequestParser = new HttpRequestParser();
    }

    @Override
    public void run() {
        log.info("Accepted a new connection. Socket: {}", clientSocket);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             clientSocket
        ) {
            final HttpRequest httpRequest = httpRequestParser.parseHttpRequest(in);
            final HttpResponse response = processHttpRequest(httpRequest);
            out.println(response.toStringHttpResponse());

            log.info("Closing connection. Socket: {}", clientSocket);
        } catch (IOException exception) {
            log.error("IO error", exception);
        } catch (Exception exception) {
            log.error("Error during request processing", exception);
        }
    }

    public HttpResponse processHttpRequest(HttpRequest httpRequest) throws IOException {
        log.info("Dispatching request to proper handler");

        final String requestPath = httpRequest.getPath();

        for (String patternString : requestPathToHandlerMappings.keySet()) {
            final Pattern pattern = Pattern.compile(patternString);
            final Matcher matcher = pattern.matcher(requestPath);

            if (matcher.matches()) {
                log.info("Handler found: {}", requestPathToHandlerMappings.get(patternString).getClass().getSimpleName());

                return requestPathToHandlerMappings
                        .get(patternString)
                        .handleHttpRequest(workingDirPath, matcher, httpRequest);
            }
        }

        log.warn("Handler was not found for request: {}. Using default one", requestPath);
        return new DefaultRequestHandler()
                .handleHttpRequest(workingDirPath, null, httpRequest);
    }
}
