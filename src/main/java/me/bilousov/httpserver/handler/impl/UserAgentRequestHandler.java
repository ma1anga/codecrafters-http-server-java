package me.bilousov.httpserver.handler.impl;

import me.bilousov.httpserver.constant.HttpHeader;
import me.bilousov.httpserver.handler.HttpRequestHandler;
import me.bilousov.httpserver.model.HttpHeaders;
import me.bilousov.httpserver.model.HttpRequest;
import me.bilousov.httpserver.model.HttpResponse;

import java.nio.file.Path;
import java.util.regex.Matcher;

import static me.bilousov.httpserver.constant.Common.HTTP_MESSAGE_OK;

public class UserAgentRequestHandler extends HttpRequestHandler {


    @Override
    public HttpResponse handleHttpRequest(Path workingDirPath, Matcher requestPathMatcher, HttpRequest httpRequest) {
        final String body = httpRequest.getHeaders().get(HttpHeader.USER_AGENT);

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
