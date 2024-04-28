package me.bilousov.httpserver.handler.impl;

import me.bilousov.httpserver.constant.HttpHeader;
import me.bilousov.httpserver.handler.HttpRequestHandler;
import me.bilousov.httpserver.model.HttpHeaders;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.regex.Matcher;

import static me.bilousov.httpserver.constant.Common.*;

public class UserAgentRequestHandler extends HttpRequestHandler {


    @Override
    public String handleHttpRequest(Path workingDirPath, String requestMethod, Matcher requestPathMatcher, HttpHeaders requestHeaders, String requestBody) {
        final String body = requestHeaders.get(HttpHeader.USER_AGENT);

        return MessageFormat.format(HTTP_RESPONSE_PATTERN, HTTP_MESSAGE_OK) + CRLF +
                "Content-Type: text/plain" + CRLF +
                "Content-Length: " + body.length() + CRLF +
                CRLF +
                body;
    }
}
