package org.codehaus.httpcache4j;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:hamnis@codehaus.org">Erlend Hamnaberg</a>
 */
public final class Cookies implements Iterable<Cookie> {
    private final Map<String, Cookie> cookies = new LinkedHashMap<String, Cookie>();

    public Cookies(Iterable<Cookie> cookies) {
        for (Cookie cookie : cookies) {
            this.cookies.put(cookie.getName(), cookie);
        }
    }

    private Cookies(Map<String, Cookie> cookieMap) {
        this.cookies.putAll(cookieMap);
    }

    private Cookies copy() {
        return new Cookies(this.cookies);
    }

    public Optional<Cookie> getCookie(String name) {
        return Optional.fromNullable(cookies.get(name));
    }

    public Cookies add(Cookie c) {
        Cookies copy = copy();
        copy.cookies.put(c.getName(), c);
        return copy;
    }
    
    public Cookies remove(Cookie c) {
        Cookies copy = copy();
        copy.cookies.remove(c.getName());
        return copy;
    }

    @Override
    public Iterator<Cookie> iterator() {
        return cookies.values().iterator();
    }

    public HTTPRequest addCookies(final HTTPRequest request) {
        List<Directive> directives = Lists.newArrayList(
                Iterables.transform(
                        Iterables.filter(this, new CookiePredicate(request)),
                        new CookieDirectiveFunction()
                )
        );
        if (!directives.isEmpty()) {
            Header header = new Header("Cookie", new Directives(directives));
            return request.addHeader(header);
        }
        return request;
    }

    private static class CookiePredicate implements Predicate<Cookie> {
        private final HTTPRequest request;

        public CookiePredicate(HTTPRequest request) {
            this.request = request;
        }

        @Override
        public boolean apply(Cookie input) {
            return input.isAllowed(request);
        }
    }

    private static class CookieDirectiveFunction implements Function<Cookie, Directive> {
        @Override
        public Directive apply(Cookie input) {
            return input.toRequestDirective();
        }
    }
}
