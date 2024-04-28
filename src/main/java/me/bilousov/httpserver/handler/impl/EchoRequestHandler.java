package me.bilousov.httpserver.handler.impl;

import me.bilousov.httpserver.constant.HttpHeader;
import me.bilousov.httpserver.handler.HttpRequestHandler;
import me.bilousov.httpserver.model.HttpHeaders;
import me.bilousov.httpserver.model.HttpRequest;
import me.bilousov.httpserver.model.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.regex.Matcher;

import static me.bilousov.httpserver.constant.Common.HTTP_MESSAGE_OK;

public class EchoRequestHandler extends HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(EchoRequestHandler.class);


    @Override
    public HttpResponse handleHttpRequest(Path workingDirPath, Matcher requestPathMatcher, HttpRequest httpRequest) {
        log.info("Start request handling");

        final String body = requestPathMatcher.group(1);

        final HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeader.CONTENT_TYPE, "text/plain");
        headers.put(HttpHeader.CONTENT_LENGTH, String.valueOf(body.length()));

        return HttpResponse.builder()
                .status(HTTP_MESSAGE_OK)
                .headers(headers)
                .body(body)
                .build();
    }
}
