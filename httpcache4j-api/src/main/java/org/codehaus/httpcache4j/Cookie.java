package org.codehaus.httpcache4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.httpcache4j.util.NumberUtils;
import org.joda.time.DateTime;

/**
 * @author <a href="mailto:hamnis@codehaus.org">Erlend Hamnaberg</a>
 */
public final class Cookie {
    private final String name;
    private final String path;
    private final String domain;
    private final String comment;
    private final int maxAge;
    private final String value;
    private final boolean httpOnly;
    private final boolean secure;
    private final DateTime expires;
    private final DateTime created;

    public Cookie(String name, String value, DateTime created, String path, String domain, String comment, int maxAge, DateTime expires, boolean httpOnly, boolean secure) {
        this.name = name;
        this.path = path;
        this.created = created == null ? new DateTime() : created;
        this.domain = domain;
        this.comment = comment;
        this.maxAge = maxAge < 0 ? -1 : maxAge;
        this.value = value;
        this.httpOnly = httpOnly;
        this.secure = secure;
        this.expires = expires;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getDomain() {
        return domain;
    }

    public String getComment() {
        return comment;
    }

    public DateTime getExpires() {
        return expires;
    }

    public boolean isExpired() {
        if (expires != null) {
            return isNowOrAfter(expires);
        }
        if (maxAge > 0) {
            DateTime dt = created.plusSeconds(maxAge);
            return isNowOrAfter(dt);
        }
        return true;
    }

    private boolean isNowOrAfter(DateTime dt) {
        return dt.isEqualNow() || dt.isAfterNow();
    }

    public int getMaxAge() {
        return maxAge;
    }

    public String getValue() {
        return value;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public boolean isSecure() {
        return secure;
    }

    public boolean isAllowed(HTTPRequest request) {
        String rawpath = request.getRequestURI().getRawPath();
        boolean pathMatches = path != null && rawpath.startsWith(path);
        String host = request.getRequestURI().getHost();
        boolean hostMatches = domain.equalsIgnoreCase(host);

        boolean secureMatches = isSecure() && request.isSecure();
        boolean notExpired = expires == null || expires.isAfterNow();
        boolean notExpiredMaxAge = maxAge == -1 || created.plusSeconds(maxAge).isAfterNow();
        return pathMatches && hostMatches && secureMatches && notExpired && notExpiredMaxAge;
    }

    public Directive toResponseDirective() {
        List<Parameter> parameters = new ArrayList<Parameter>();

        if (path != null) {
            parameters.add(new Parameter("Path", path));
        }
        if (domain != null) {
            parameters.add(new Parameter("Domain", domain));
        }
        if (expires != null) {
            parameters.add(new Parameter("Expires", HeaderUtils.toGMTString(expires)));
        }
        if (maxAge > -1) {
            parameters.add(new Parameter("Max-Age", String.valueOf(maxAge)));
        }
        if (secure) {
            parameters.add(new Parameter("secure", null));
        }
        if (httpOnly) {
            parameters.add(new Parameter("HttpOnly", null));
        }
        if (comment != null) {
            parameters.add(new Parameter("comment", comment));
        }

        return new Directive(name, value, parameters);
    }

    public Directive toRequestDirective() {
        return new Directive(name, value, new ArrayList<Parameter>());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cookie cookie = (Cookie) o;

        if (httpOnly != cookie.httpOnly) return false;
        if (maxAge != cookie.maxAge) return false;
        if (secure != cookie.secure) return false;
        if (comment != null ? !comment.equals(cookie.comment) : cookie.comment != null) return false;
        if (created != null ? !created.equals(cookie.created) : cookie.created != null) return false;
        if (domain != null ? !domain.equals(cookie.domain) : cookie.domain != null) return false;
        if (expires != null ? !expires.equals(cookie.expires) : cookie.expires != null) return false;
        if (name != null ? !name.equals(cookie.name) : cookie.name != null) return false;
        if (path != null ? !path.equals(cookie.path) : cookie.path != null) return false;
        if (value != null ? !value.equals(cookie.value) : cookie.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + maxAge;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (httpOnly ? 1 : 0);
        result = 31 * result + (secure ? 1 : 0);
        result = 31 * result + (expires != null ? expires.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        return result;
    }

    public static Cookie fromDirective(Directive directive) {
        /*Map<String,Parameter> map = directive.getParametersAsMap();
        String domain = get(map, "domain");
        DateTime expires = HeaderUtils.parseGMTString(get(map, "expires"));
        int maxAge = NumberUtils.toInt(get(map, "max-age"), -1);
        boolean secure = map.containsKey("secure");
        boolean httponly = map.containsKey("httponly");
        String comment = get(map, "comment");
        String path = get(map, "path");

        return new Cookie(directive.getName(), directive.getValue(), new DateTime(), path, domain, comment, maxAge, expires, httponly, secure);*/
        return null;
    }

    private static String get(Map<String, Parameter> map, String key) {
        key = key.toLowerCase();
        if (map.containsKey(key)) {
            return map.get(key).getValue();
        }
        return null;
    }
}
