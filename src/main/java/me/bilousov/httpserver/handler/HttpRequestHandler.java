package me.bilousov.httpserver.handler;

import me.bilousov.httpserver.model.HttpRequest;
import me.bilousov.httpserver.model.HttpResponse;

import java.nio.file.Path;
import java.util.regex.Matcher;


public abstract class HttpRequestHandler {

    public abstract HttpResponse handleHttpRequest(Path workingDirPath, Matcher requestPathMatcher, HttpRequest httpRequest);
}
