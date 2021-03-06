package org.skytrail.bilcdb.session.memory;

import com.google.common.base.Predicate;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import org.skytrail.bilcdb.model.security.Auth;
import org.skytrail.bilcdb.session.SessionCache;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by herndon on 4/13/14.
 */
public class InMemorySessionCache implements SessionCache {
    private volatile Cache<String, Auth> cache;

    @Inject
    public InMemorySessionCache() {
        // If there is no activity against a key then we want
        // it to be expired from the cache, but each fresh write
        // will reset the expiry timer
        cache = CacheBuilder
                .newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
    }

    @Override
    public String put(Auth auth) {
        String sessionId = UUID.randomUUID().toString();
        cache.put(sessionId, auth);
        return sessionId;
    }

    @Override
    public Auth getBySessionId(final String sessionId) {
        return cache.getIfPresent(sessionId);
    }

    @Override
    public Iterable<String> getByAuth(final Auth auth) {
        return Maps.filterEntries(cache.asMap(), new Predicate<Map.Entry<String, Auth>>() {
            @Override
            public boolean apply(@Nullable Map.Entry<String, Auth> stringAuthEntry) {
                return stringAuthEntry.getValue().equals(auth);
            }
        }).keySet();
    }

    @Override
    public void delete(String sessionId) {
        cache.invalidate(sessionId);
    }
}
