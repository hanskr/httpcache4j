/*
 * Copyright (c) 2009. The Codehaus. All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.codehaus.httpcache4j.cache;

import org.apache.commons.io.input.NullInputStream;
import org.codehaus.httpcache4j.*;
import org.codehaus.httpcache4j.payload.InputStreamPayload;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author <a href="mailto:erlend@codehaus.org">Erlend Hamnaberg</a>
 * @version $Revision: $
 */
public abstract class ConcurrentCacheStorageAbstractTest {
    private ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    protected CacheStorage cacheStorage;

    @Before
    public void setUp() {
        cacheStorage = createCacheStorage();
    }

    protected abstract CacheStorage createCacheStorage();

    @Test
    public void test100Concurrent() throws InterruptedException {
        testIterations(100, 100);
    }

    protected void testIterations(int numberOfIterations, int expected) throws InterruptedException {
        Vary vary = new Vary();
        List<Callable<HTTPResponse>> calls = new ArrayList<Callable<HTTPResponse>>();
        for (int i = 0; i < numberOfIterations; i++) {
            final HTTPResponse response = createCacheResponse();
            final URI uri = URI.create(String.valueOf(i));
            final Key key = new Key(uri, vary);
            Callable<HTTPResponse> call = new Callable<HTTPResponse>() {
                public HTTPResponse call() throws Exception {
                    HTTPResponse cached = cacheStorage.insert(key, response);
                    CacheItem cacheItem = cacheStorage.get(new HTTPRequest(uri));
                    assertSame(cached, cacheItem.getResponse());
                    cached = cacheStorage.insert(key, createCacheResponse());
                    assertNotSame(cached, cacheItem.getResponse());
                    return cached;
                }
            };
            calls.add(call);
        }
        List<Future<HTTPResponse>> responses = service.invokeAll(calls);
        for (Future<HTTPResponse> response : responses) {
            try {
                HTTPResponse real = response.get();
                assertResponse(real);
            } catch (ExecutionException e) {
                fail(e.getCause().getMessage());
            }
        }

        assertEquals(expected, cacheStorage.size());
        assertEquals(expected, cacheStorage.size());
    }

    private HTTPResponse createCacheResponse() {
        return new HTTPResponse(new InputStreamPayload(new NullInputStream(40), MIMEType.APPLICATION_OCTET_STREAM), Status.OK, new Headers());
    }

    protected void assertResponse(final HTTPResponse response) {
        assertNotNull("Response was null", response);
        assertTrue("Payload was not here", response.hasPayload());
        assertTrue("Payload was not available", response.getPayload().isAvailable());
    }

    @After
    public void tearDown() {
        cacheStorage.clear();
        service.shutdownNow();
    }
}
