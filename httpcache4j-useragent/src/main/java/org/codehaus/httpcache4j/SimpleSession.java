package org.codehaus.httpcache4j;

import java.util.UUID;

/**
 * @author Erlend Hamnaberg<erlend.hamnaberg@arktekk.no>
 */
public final class SimpleSession implements Session {

    private final String uuid = UUID.randomUUID().toString();

    @Override
    public String getId() {
        return uuid;
    }
}
