package org.codehaus.httpcache4j.cookie;

import org.codehaus.httpcache4j.Cookie;
import org.codehaus.httpcache4j.Cookies;

/**
 * @author <a href="mailto:hamnis@codehaus.org">Erlend Hamnaberg</a>
 */
public interface CookieStore {

    public Cookies getAll();

    public Cookie get(String name);

    public Cookie remove(String name);

    public void clear();
}
