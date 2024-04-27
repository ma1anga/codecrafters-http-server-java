package me.bilousov.httpserver.handler;

import me.bilousov.httpserver.constant.HttpHeader;
import me.bilousov.httpserver.constant.HttpRequestMethod;
import me.bilousov.httpserver.model.HttpHeaders;
import me.bilousov.httpserver.util.ParseUtil;

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
    private static final String HTTP_MESSAGE_OK_CREATED = "201 OK";
    private static final String HTTP_MESSAGE_NOT_FOUND = "404 Not Found";
    private static final String HTTP_MESSAGE_BAD_REQUEST = "400 Bad Request";

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
            final String requestStartLine = in.readLine();
            final HttpHeaders httpRequestHeaders = parseRequestHttpHeaders(in);
            final String requestBody = parseRequestBody(in, httpRequestHeaders.get(HttpHeader.CONTENT_LENGTH));

            String response = constructResponse(requestStartLine, httpRequestHeaders, requestBody);
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
            final String line = reader.readLine();

            if(emptyOrCrlf(line)) {
                break;
            }

            final List<String> headerParts = Arrays.asList(line.split(HTTP_HEADER_DIVIDER));
            httpRequestHeaders.put(headerParts.getFirst(), headerParts.getLast());
        }

        return httpRequestHeaders;
    }

    private String parseRequestBody(BufferedReader reader, String contentLengthString) throws IOException {
        int contentLength = ParseUtil.safeParseInt(contentLengthString);

        final char[] buffer = new char[contentLength];
        final int charsRead = reader.read(buffer, 0, buffer.length);

        return new String(buffer, 0, charsRead);
    }

    private boolean emptyOrCrlf(String line) {
        return line == null
                || Objects.equals(line, "")
                || Objects.equals(line, CRLF);
    }

    private String constructResponse(String requestStartLine, HttpHeaders requestHeaders, String requestBody) {
        final List<String> requestStartLineParts = getRequestStartLineParts(requestStartLine);
        final String requestMethod = requestStartLineParts.get(0);
        final String requestPath = requestStartLineParts.get(1);

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

            if (requestMethod.equals(HttpRequestMethod.POST)) {
                try {
                    Files.writeString(fullFilePath, requestBody);

                    return MessageFormat.format(HTTP_RESPONSE_PATTERN, HTTP_MESSAGE_OK_CREATED) + CRLF + CRLF;
                } catch (IOException e) {
                    System.out.println("Error writing file: " + fullFilePath);
                    e.printStackTrace();

                    return MessageFormat.format(HTTP_RESPONSE_PATTERN, HTTP_MESSAGE_BAD_REQUEST) + CRLF + CRLF;
                }
            } else {
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
            }
        } else if (requestPath.equals("/")) {
            return MessageFormat.format(HTTP_RESPONSE_PATTERN, HTTP_MESSAGE_OK) + CRLF + CRLF;
        } else {
            return MessageFormat.format(HTTP_RESPONSE_PATTERN, HTTP_MESSAGE_NOT_FOUND) + CRLF + CRLF;
        }
    }

    private List<String> getRequestStartLineParts(String requestStartLine) {
        return Arrays.asList(requestStartLine.split(REQUEST_START_LINE_DIVIDER));
    }

    private String getFileContent(Path filePath) throws IOException {
        return new String(Files.readAllBytes(filePath));
    }
}
