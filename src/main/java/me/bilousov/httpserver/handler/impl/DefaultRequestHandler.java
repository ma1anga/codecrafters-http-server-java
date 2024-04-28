package me.bilousov.httpserver.handler.impl;

import me.bilousov.httpserver.handler.HttpRequestHandler;
import me.bilousov.httpserver.model.HttpRequest;
import me.bilousov.httpserver.model.HttpResponse;

import java.nio.file.Path;
import java.util.regex.Matcher;

import static me.bilousov.httpserver.constant.Common.HTTP_MESSAGE_NOT_FOUND;

public class DefaultRequestHandler extends HttpRequestHandler {

    @Override
    public HttpResponse handleHttpRequest(Path workingDirPath, Matcher requestPathMatcher, HttpRequest httpRequest) {
        return HttpResponse.builder()
                .status(HTTP_MESSAGE_NOT_FOUND)
                .build();
    }
}
