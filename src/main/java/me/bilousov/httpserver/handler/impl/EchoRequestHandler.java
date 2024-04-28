package me.bilousov.httpserver.handler.impl;

import me.bilousov.httpserver.handler.HttpRequestHandler;
import me.bilousov.httpserver.model.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.regex.Matcher;

import static me.bilousov.httpserver.constant.Common.*;

public class EchoRequestHandler extends HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(EchoRequestHandler.class);


    @Override
    public String handleHttpRequest(Path workingDirPath, String requestMethod, Matcher requestPathMatcher, HttpHeaders requestHeaders, String requestBody) {
        log.info("Start request handling");

        final String body = requestPathMatcher.group(1);

        return MessageFormat.format(HTTP_RESPONSE_PATTERN, HTTP_MESSAGE_OK) + CRLF +
                "Content-Type: text/plain" + CRLF +
                "Content-Length: " + (body.length()) + CRLF +
                CRLF +
                body;
    }
}
