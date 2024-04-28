package me.bilousov.httpserver.model;

import java.text.MessageFormat;
import java.util.Objects;

import static me.bilousov.httpserver.constant.Common.CRLF;

public class HttpResponse {

    public static final String HTTP_RESPONSE_PATTERN = "HTTP/1.1 {0}";

    private final String status;
    private final HttpHeaders headers;
    private final String body;


    private HttpResponse(Builder builder) {
        this.status = builder.status;
        this.headers = builder.headers;
        this.body = builder.body;
    }

    public String getStatus() {
        return status;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public String toStringHttpResponse() {
        return MessageFormat.format(HTTP_RESPONSE_PATTERN, getStatus()) + CRLF +
                getHeadersString() + CRLF +
                getBody() +
                CRLF;
    }

    public static HttpResponse.Builder builder() {
        return new HttpResponse.Builder();
    }

    private String getHeadersString() {
        final StringBuilder stringBuilder = new StringBuilder();

        if (Objects.isNull(headers)) {
            return "";
        }

        for (String headerName : headers.keySet()) {
            stringBuilder
                    .append(headerName)
                    .append(": ")
                    .append(headers.get(headerName))
                    .append(CRLF);
        }

        return stringBuilder.toString();
    }

    public static class Builder {
        private String status;
        private HttpHeaders headers;
        private String body;

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder headers(HttpHeaders headers) {
            this.headers = headers;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }
}
