package org.codehaus.httpcache4j;

import org.codehaus.httpcache4j.cache.HTTPCache;

/**
 * @author Erlend Hamnaberg<erlend.hamnaberg@arktekk.no>
 */
public class DefaultUserAgent implements UserAgent {
    private HTTPCache cache;

    public DefaultUserAgent(HTTPCache cache) {
        this.cache = cache;
    }

    @Override
    public HTTPResponse execute(final HTTPRequest request) {
        return cache.execute(request);
    }
}
