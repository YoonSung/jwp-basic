package core.mvc;

import core.annotation.RequestMethod;

import java.util.Objects;

public class HandlerKey {
    private final String url;
    private final RequestMethod method;

    public HandlerKey(String url, RequestMethod method) {
        this.url = url;
        this.method = method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HandlerKey that = (HandlerKey) o;
        return Objects.equals(url, that.url) &&
                Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, method);
    }
}
