package me.bilousov.httpserver.parser;

import me.bilousov.httpserver.constant.HttpHeader;
import me.bilousov.httpserver.model.HttpHeaders;
import me.bilousov.httpserver.model.HttpRequest;
import me.bilousov.httpserver.util.ParseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static me.bilousov.httpserver.constant.Common.CRLF;

public class HttpRequestParser {

    private static final String REQUEST_START_LINE_DIVIDER = " ";
    private static final String HTTP_HEADER_DIVIDER = ": ";
    private static final Logger log = LoggerFactory.getLogger(HttpRequestParser.class);

    public HttpRequest parseHttpRequest(BufferedReader inputBufferedReader) throws IOException {
        log.info("Start HTTP request parsing");

        final String requestStartLine = inputBufferedReader.readLine();

        final List<String> requestStartLineParts = getRequestStartLineParts(requestStartLine);
        final String requestMethod = requestStartLineParts.get(0);
        final String requestPath = requestStartLineParts.get(1);

        final HttpHeaders requestHeaders = parseRequestHttpHeaders(inputBufferedReader);
        final String requestBody = parseRequestBody(inputBufferedReader, requestHeaders.get(HttpHeader.CONTENT_LENGTH));

        log.info("Finish HTTP request parsing");

        return HttpRequest.builder()
                .method(requestMethod)
                .path(requestPath)
                .headers(requestHeaders)
                .body(requestBody)
                .build();
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
