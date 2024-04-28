package me.bilousov.httpserver.model;

public class HttpRequest {

    private final String method;
    private final String path;
    private final HttpHeaders headers;
    private final String body;


    private HttpRequest(Builder builder) {
        this.method = builder.method;
        this.path = builder.path;
        this.headers = builder.headers;
        this.body = builder.body;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private String method;
        private String path;
        private HttpHeaders headers;
        private String body;

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
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

        public HttpRequest build() {
            return new HttpRequest(this);
        }
    }
}
