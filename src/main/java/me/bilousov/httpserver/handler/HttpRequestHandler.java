package me.bilousov.httpserver.handler;

import me.bilousov.httpserver.model.HttpHeaders;

import java.nio.file.Path;
import java.util.regex.Matcher;


public abstract class HttpRequestHandler {

    public abstract String handleHttpRequest(Path workingDirPath, String requestMethod, Matcher requestPathMatcher, HttpHeaders requestHeaders, String requestBody);
}
