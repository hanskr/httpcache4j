package org.codehaus.httpcache4j;

import org.codehaus.httpcache4j.cache.HTTPCache;

/**
 * @author Erlend Hamnaberg<erlend.hamnaberg@arktekk.no>
 */
public interface UserAgent {
    HTTPResponse execute(HTTPRequest request);
}
