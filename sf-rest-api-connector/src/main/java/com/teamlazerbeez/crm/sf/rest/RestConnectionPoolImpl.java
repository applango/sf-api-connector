/*
 * Copyright © 2011. Team Lazer Beez (http://teamlazerbeez.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.teamlazerbeez.crm.sf.rest;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.ContentEncodingHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.codehaus.jackson.map.ObjectMapper;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ThreadSafe
public class RestConnectionPoolImpl<T> implements RestConnectionPool<T> {

    private static final int DEFAULT_IDLE_CONN_TIMEOUT = 30;

    private final int idleConnTimeout;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient httpClient;

    @GuardedBy("this")
    private final Map<T, ConnectionConfig> configMap = new HashMap<T, ConnectionConfig>();
    private final ThreadSafeClientConnManager clientConnManager;

    /**
     * Create a new pool with the default idle connection timeout.
     */
    public RestConnectionPoolImpl() {
        this(DEFAULT_IDLE_CONN_TIMEOUT);
    }

    /**
     * Create a new pool with a specific idle connection timeout.
     *
     * @param idleConnTimeout how long an unused connection must sit idle before it is eligible for removal from the
     *                        pool, in seconds
     */
    public RestConnectionPoolImpl(int idleConnTimeout) {
        // defaults are too low for these out of the box
        clientConnManager = new ThreadSafeClientConnManager();
        clientConnManager.setDefaultMaxPerRoute(20);
        clientConnManager.setMaxTotal(60);

        this.httpClient = new ContentEncodingHttpClient(clientConnManager, null);
        this.idleConnTimeout = idleConnTimeout;
    }

    @Override
    public synchronized RestConnection getRestConnection(@Nonnull T orgId) {
        return new RestConnectionImpl(objectMapper, new PoolHttpApiClientProvider(orgId));
    }

    @Override
    public synchronized void configureOrg(@Nonnull T orgId, @Nonnull String host, @Nonnull String token) {
        this.configMap.put(orgId, new ConnectionConfig(host, token));
    }

    @Override
    public Runnable getExpiredConnectionTask() {
        return new HttpExpiredConnManager();
    }

    @Nonnull
    private synchronized HttpApiClient getClientForOrg(T orgId) {
        ConnectionConfig connectionConfig = this.configMap.get(orgId);

        if (connectionConfig == null) {
            throw new IllegalStateException("Org <" + orgId + "> has not been configured");
        }

        return new HttpApiClient(connectionConfig.getHost(), connectionConfig.getOauthToken(),
                this.objectMapper, this.httpClient);
    }

    private class PoolHttpApiClientProvider implements HttpApiClientProvider {

        private final T orgId;

        PoolHttpApiClientProvider(T orgId) {
            this.orgId = orgId;
        }

        @Override
        public HttpApiClient getClient() {
            return getClientForOrg(orgId);
        }
    }

    private class HttpExpiredConnManager implements Runnable {

        @Override
        public void run() {
            clientConnManager.closeExpiredConnections();
            clientConnManager.closeIdleConnections(idleConnTimeout, TimeUnit.SECONDS);
        }
    }
}
