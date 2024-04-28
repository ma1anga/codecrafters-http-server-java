package me.bilousov.httpserver.dispatcher;

import me.bilousov.httpserver.constant.HttpHeader;
import me.bilousov.httpserver.handler.HttpRequestHandler;
import me.bilousov.httpserver.model.HttpHeaders;
import me.bilousov.httpserver.util.ParseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.bilousov.httpserver.constant.Common.*;

public class HttpRequestProcessor extends Thread {

    private static final String REQUEST_START_LINE_DIVIDER = " ";
    private static final String HTTP_HEADER_DIVIDER = ": ";
    private static final String CRLF = "\r\n";

    private static final Logger log = LoggerFactory.getLogger(HttpRequestProcessor.class);

    private final Map<String, HttpRequestHandler> requestPathToHandlerMappings;
    private final Socket clientSocket;
    private final Path workingDirPath;


    public HttpRequestProcessor(Socket clientSocket, Path workingDirPath, Map<String, HttpRequestHandler> requestPathToHandlerMappings) {
        this.requestPathToHandlerMappings = requestPathToHandlerMappings;
        this.clientSocket = clientSocket;
        this.workingDirPath = workingDirPath;
    }

    @Override
    public void run() {
        log.info("Accepted a new connection. Socket: {}", clientSocket);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             clientSocket
        ) {
            final String response = processHttpRequest(in);
            out.println(response);

            log.info("Closing connection. Socket: {}", clientSocket);
        } catch (IOException exception) {
            log.error("IO error", exception);
        } catch (Exception exception) {
            log.error("Error during request processing", exception);
        }
    }

    public String processHttpRequest(BufferedReader in) throws IOException {
        log.info("Dispatching request to proper handler");

        final String requestStartLine = in.readLine();

        final List<String> requestStartLineParts = getRequestStartLineParts(requestStartLine);
        final String requestMethod = requestStartLineParts.get(0);
        final String requestPath = requestStartLineParts.get(1);

        for (String patternString : requestPathToHandlerMappings.keySet()) {
            final Pattern pattern = Pattern.compile(patternString);
            final Matcher matcher = pattern.matcher(requestPath);

            if (matcher.matches()) {
                log.info("Handler found: {}", requestPathToHandlerMappings.get(patternString).getClass().getSimpleName());

                final HttpHeaders requestHeaders = parseRequestHttpHeaders(in);

                return requestPathToHandlerMappings
                        .get(patternString)
                        .handleHttpRequest(workingDirPath, requestMethod, matcher, requestHeaders, parseRequestBody(in, requestHeaders.get(HttpHeader.CONTENT_LENGTH)));
            }
        }

        log.warn("Handler was not found for request: {}", requestPath);
        return MessageFormat.format(HTTP_RESPONSE_PATTERN, HTTP_MESSAGE_NOT_FOUND) + CRLF;
    }

    private List<String> getRequestStartLineParts(String requestStartLine) {
        return Arrays.asList(requestStartLine.split(REQUEST_START_LINE_DIVIDER));
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
}
