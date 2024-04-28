package me.bilousov.httpserver.handler.impl;

import me.bilousov.httpserver.constant.HttpHeader;
import me.bilousov.httpserver.constant.HttpRequestMethod;
import me.bilousov.httpserver.handler.HttpRequestHandler;
import me.bilousov.httpserver.model.HttpHeaders;
import me.bilousov.httpserver.model.HttpRequest;
import me.bilousov.httpserver.model.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;

import static me.bilousov.httpserver.constant.Common.*;

public class FilesRequestHandler extends HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(FilesRequestHandler.class);


    @Override
    public HttpResponse handleHttpRequest(Path workingDirPath, Matcher requestPathMatcher, HttpRequest httpRequest) {
        final String fileName = requestPathMatcher.group(1);
        final Path fullFilePath = workingDirPath.resolve(fileName);

        if (httpRequest.getMethod().equals(HttpRequestMethod.POST)) {
            return handlePostRequest(fullFilePath, httpRequest);
        } else {
            return handleGetRequest(fullFilePath);
        }
    }

    private HttpResponse handlePostRequest(Path fullFilePath, HttpRequest httpRequest) {
        try {
            Files.writeString(fullFilePath, httpRequest.getBody());

            return HttpResponse.builder()
                    .status(HTTP_MESSAGE_OK_CREATED)
                    .build();
        } catch (IOException exception) {
            log.error("Error during file writing", exception);

            return HttpResponse.builder()
                    .status(HTTP_MESSAGE_BAD_REQUEST)
                    .build();
        }
    }

    private HttpResponse handleGetRequest(Path fullFilePath) {
        try {
            final String fileContent = getFileContent(fullFilePath);

            final HttpHeaders headers = new HttpHeaders();
            headers.put(HttpHeader.CONTENT_TYPE, "application/octet-stream");
            headers.put(HttpHeader.CONTENT_LENGTH, String.valueOf(fileContent.length()));

            return HttpResponse.builder()
                    .status(HTTP_MESSAGE_OK)
                    .headers(headers)
                    .body(fileContent)
                    .build();
        } catch (IOException exception) {
            log.error("Error reading file", exception);

            return HttpResponse.builder()
                    .status(HTTP_MESSAGE_NOT_FOUND)
                    .build();
        }
    }

    private String getFileContent(Path filePath) throws IOException {
        return new String(Files.readAllBytes(filePath));
    }
}
