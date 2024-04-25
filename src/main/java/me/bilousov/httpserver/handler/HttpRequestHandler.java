package me.bilousov.httpserver.handler;

import me.bilousov.httpserver.constant.HttpHeader;
import me.bilousov.httpserver.model.HttpHeaders;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequestHandler extends Thread {

    private static final String HTTP_RESPONSE_PATTERN = "HTTP/1.1 {0}";
    private static final String ECHO_REQUEST_PATTERN = "^/echo/(.*)$";
    private static final String USER_AGENT_REQUEST_PATTERN = "^/user-agent$";
    private static final String FILES_AGENT_REQUEST_PATTERN = "^/files/(.*)$";

    private static final String HTTP_MESSAGE_OK = "200 OK";
    private static final String HTTP_MESSAGE_NOT_FOUND = "404 Not Found";

    private static final String REQUEST_START_LINE_DIVIDER = " ";
    private static final String HTTP_HEADER_DIVIDER = ": ";
    private static final String CRLF = "\r\n";

    private final Socket clientSocket;
    private final Path workingDirPath;


    public HttpRequestHandler(Socket clientSocket, Path workingDirPath) {
        this.clientSocket = clientSocket;
        this.workingDirPath = workingDirPath;
    }

    @Override
    public void run() {
        System.out.println("Accepted new connection. Thread: " + Thread.currentThread().getName());

        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true))
        {
            String requestStartLine = in.readLine();
            HttpHeaders httpRequestHeaders = parseRequestHttpHeaders(in);
            String response = constructResponse(requestStartLine, httpRequestHeaders);
            out.println(response);

            clientSocket.close();
            System.out.println("Closing connection. Thread: " + Thread.currentThread().getName());
        } catch (IOException exception) {
            System.out.println("IO error: " + exception.getMessage());
            exception.printStackTrace();
        } catch (Exception exception) {
            System.out.println("Error during request processing: " + exception.getMessage());
            exception.printStackTrace();
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
        final Pattern filesPattern = Pattern.compile(FILES_AGENT_REQUEST_PATTERN);

        final Matcher echoMatcher = echoPattern.matcher(requestPath);
        final Matcher userAgentMatcher = userAgentPattern.matcher(requestPath);
        final Matcher filesMatcher = filesPattern.matcher(requestPath);

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
        } else if (filesMatcher.matches()) {
            final String fileName = filesMatcher.group(1);
            final Path fullFilePath = workingDirPath.resolve(fileName);

            try {
                final String fileContent = getFileContent(fullFilePath);

                return MessageFormat.format(HTTP_RESPONSE_PATTERN, HTTP_MESSAGE_OK) + CRLF +
                        "Content-Type: application/octet-stream" + CRLF +
                        "Content-Length: " + fileContent.length() + CRLF +
                        CRLF +
                        fileContent + CRLF;
            } catch (IOException e) {
                System.out.println("Error reading file: " + fullFilePath);
                e.printStackTrace();

                return MessageFormat.format(HTTP_RESPONSE_PATTERN, HTTP_MESSAGE_NOT_FOUND) + CRLF + CRLF;
            }
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

    private String getFileContent(Path filePath) throws IOException {
        return new String(Files.readAllBytes(filePath));
    }
}
