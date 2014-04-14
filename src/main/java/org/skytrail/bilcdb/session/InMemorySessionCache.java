package org.skytrail.bilcdb.session;

import com.google.common.base.Predicate;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import org.skytrail.bilcdb.model.security.Auth;

import javax.annotation.Nullable;
import java.util.Map;
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
        cache.put("Hello_World", auth);
        return "Hello_World";
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
}
