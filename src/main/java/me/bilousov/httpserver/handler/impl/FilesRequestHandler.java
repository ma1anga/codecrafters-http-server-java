package me.bilousov.httpserver.handler.impl;

import me.bilousov.httpserver.constant.HttpRequestMethod;
import me.bilousov.httpserver.handler.HttpRequestHandler;
import me.bilousov.httpserver.model.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.regex.Matcher;

import static me.bilousov.httpserver.constant.Common.*;

public class FilesRequestHandler extends HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(FilesRequestHandler.class);


    @Override
    public String handleHttpRequest(Path workingDirPath, Matcher requestPathMatcher, HttpRequest httpRequest) {
        final String fileName = requestPathMatcher.group(1);
        final Path fullFilePath = workingDirPath.resolve(fileName);

        if (httpRequest.getMethod().equals(HttpRequestMethod.POST)) {
            try {
                Files.writeString(fullFilePath, httpRequest.getBody());

                return MessageFormat.format(HTTP_RESPONSE_PATTERN, HTTP_MESSAGE_OK_CREATED) + CRLF + CRLF;
            } catch (IOException exception) {
                log.error("Error during file writing", exception);

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
            } catch (IOException exception) {
                log.error("Error reading file", exception);

                return MessageFormat.format(HTTP_RESPONSE_PATTERN, HTTP_MESSAGE_NOT_FOUND) + CRLF + CRLF;
            }
        }
    }

    private String getFileContent(Path filePath) throws IOException {
        return new String(Files.readAllBytes(filePath));
    }
}
