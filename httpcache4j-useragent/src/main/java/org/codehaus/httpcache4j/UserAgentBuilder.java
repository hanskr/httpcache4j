package org.codehaus.httpcache4j;

import com.google.common.base.Strings;
import org.codehaus.httpcache4j.mutable.MutablePreferences;
import org.codehaus.httpcache4j.preference.Preferences;
import org.codehaus.httpcache4j.resolver.ResolverConfiguration;

import java.nio.charset.Charset;
import java.util.Locale;

/**
 * Note: This class is mutable, and is not thread safe.
 *
 * @author Erlend Hamnaberg<erlend.hamnaberg@arktekk.no>
 */
public class UserAgentBuilder {
    private String userAgent = ResolverConfiguration.DEFAULT_USER_AGENT;
    private MutablePreferences preferences = new MutablePreferences();
    private boolean rewriteHEADtoGET;

    public UserAgentBuilder() {
        preferences.addMIMEType(MIMEType.ALL);
        rewriteHEADtoGET = true;
    }


    public UserAgentBuilder userAgent(String userAgent) {
        this.userAgent = Strings.isNullOrEmpty(userAgent) ? ResolverConfiguration.DEFAULT_USER_AGENT : userAgent;
        return this;
    }

    public UserAgentBuilder accept(MIMEType type, double quality) {
        preferences.addMIMEType(type, quality);
        return this;
    }

    public UserAgentBuilder accept(MIMEType type) {
        preferences.addMIMEType(type);
        return this;
    }

    public UserAgentBuilder acceptLanguage(Locale locale) {
        preferences.addLocale(locale);
        return this;
    }

    public UserAgentBuilder acceptLanguage(Locale locale, double quality) {
        preferences.addLocale(locale, quality);
        return this;
    }

    public UserAgentBuilder acceptCharset(Charset charset) {
        preferences.addCharset(charset.name());
        return this;
    }

    public UserAgentBuilder acceptCharset(Charset charset, double quality) {
        preferences.addCharset(charset.name(), quality);
        return this;
    }

    public UserAgentBuilder rewriteHeadToGET(boolean doit) {
        rewriteHEADtoGET = doit;
        return this;
    }


}
