package me.bilousov.httpserver.handler;

import me.bilousov.httpserver.model.HttpRequest;

import java.nio.file.Path;
import java.util.regex.Matcher;


public abstract class HttpRequestHandler {

    public abstract String handleHttpRequest(Path workingDirPath, Matcher requestPathMatcher, HttpRequest httpRequest);
}
