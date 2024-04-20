package me.bilousov.httpserver.server;

import me.bilousov.httpserver.model.HttpHeader;
import me.bilousov.httpserver.model.HttpHeaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpServer {

    private static final int DEFAULT_PORT = 4221;

    private static final String HTTP_RESPONSE_PATTERN = "HTTP/1.1 {0}";
    private static final String ECHO_REQUEST_PATTERN = "^/echo/(.*)$";
    private static final String USER_AGENT_REQUEST_PATTERN = "^/user-agent$";

    private static final String HTTP_MESSAGE_OK = "200 OK";
    private static final String HTTP_MESSAGE_NOT_FOUND = "404 Not Found";

    private static final String REQUEST_START_LINE_DIVIDER = " ";
    private static final String HTTP_HEADER_DIVIDER = ": ";
    private static final String CRLF = "\r\n";

    public void start() {
        Socket clientSocket = null;

        PrintWriter out;
        BufferedReader in;

        try (ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT)) {
            serverSocket.setReuseAddress(true);
            clientSocket = serverSocket.accept();

            System.out.println("Accepted new connection");

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            String requestStartLine = in.readLine();

            HttpHeaders httpRequestHeaders = parseRequestHttpHeaders(in);

            String response = constructResponse(requestStartLine, httpRequestHeaders);

            out.println(response);

            System.out.println("Closing connection...");
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private HttpHeaders parseRequestHttpHeaders(BufferedReader reader) throws IOException {
        final HttpHeaders httpRequestHeaders = new HttpHeaders();

        while (reader.ready()) {
            String line = reader.readLine();

            if(emptyOrCrlf(line)) {
                break;
            }

            final List<String> headerParts = Arrays.asList(line.split(HTTP_HEADER_DIVIDER));
            httpRequestHeaders.put(headerParts.getFirst(), headerParts.getLast());
        }

        return httpRequestHeaders;
    }

    private boolean emptyOrCrlf(String line) {
        return line == null || Objects.equals(line, "") || Objects.equals(line, CRLF);
    }

    private String constructResponse(String requestStartLine, HttpHeaders requestHeaders) {
        final String requestPath = getRequestPath(requestStartLine);

        final Pattern echoPattern = Pattern.compile(ECHO_REQUEST_PATTERN);
        final Pattern userAgentPattern = Pattern.compile(USER_AGENT_REQUEST_PATTERN);

        final Matcher echoMatcher = echoPattern.matcher(requestPath);
        final Matcher userAgentMatcher = userAgentPattern.matcher(requestPath);

        if (echoMatcher.matches()) {
            final String body = echoMatcher.group(1);

            return MessageFormat.format(HTTP_RESPONSE_PATTERN, HTTP_MESSAGE_OK) + CRLF +
                    "Content-Type: text/plain" + CRLF +
                    "Content-Length: " + body.length() + CRLF +
                    CRLF +
                    body + CRLF;
        } else if(userAgentMatcher.matches()) {
            final String body = requestHeaders.get(HttpHeader.USER_AGENT);

            return MessageFormat.format(HTTP_RESPONSE_PATTERN, HTTP_MESSAGE_OK) + CRLF +
                    "Content-Type: text/plain" + CRLF +
                    "Content-Length: " + body.length() + CRLF +
                    CRLF +
                    body + CRLF;
        } else if (requestPath.equals("/")) {
            return MessageFormat.format(HTTP_RESPONSE_PATTERN, HTTP_MESSAGE_OK) + CRLF + CRLF;
        } else {
            return MessageFormat.format(HTTP_RESPONSE_PATTERN, HTTP_MESSAGE_NOT_FOUND) + CRLF + CRLF;
        }
    }

    private String getRequestPath(String requestStartLine) {
        final List<String> startLineParts = Arrays.asList(requestStartLine.split(REQUEST_START_LINE_DIVIDER));

        // Start line format -> "GET /index.html HTTP/1.1"
        return startLineParts.get(1);
    }
}
