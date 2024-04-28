package me.bilousov.httpserver.handler.impl;

import me.bilousov.httpserver.handler.HttpRequestHandler;
import me.bilousov.httpserver.model.HttpRequest;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.regex.Matcher;

import static me.bilousov.httpserver.constant.Common.*;

public class EmptyRequestHandler extends HttpRequestHandler {


    @Override
    public String handleHttpRequest(Path workingDirPath, Matcher requestPathMatcher, HttpRequest httpRequest) {
        return MessageFormat.format(HTTP_RESPONSE_PATTERN, HTTP_MESSAGE_OK) + CRLF;
    }
}
