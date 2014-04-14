package org.skytrail.bilcdb.auth.openid;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.openid4java.consumer.ConsumerManager;

import javax.swing.text.html.Option;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>In-memory cache to provide the following to OpenID authentication:</p>
 * <ul>
 * <li>Short term storage of thread local session data</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
class InMemoryOpenIdCache implements OpenIdCache {

    /**
     * Simple cache for {@link org.openid4java.consumer.ConsumerManager} entries
     */
    private final Cache<String, DiscoveryInformationMemento> mementoCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();

    @Override
    public Optional<DiscoveryInformationMemento> getMemento(String sessionid) {
        return Optional.fromNullable(mementoCache.getIfPresent(sessionid));
    }

    @Override
    public String putMemento(DiscoveryInformationMemento memento) {
        String uuid = UUID.randomUUID().toString();
        mementoCache.put(uuid, memento);
        return uuid;
    }

    @Override
    public void deleteMemento(String sessionId) {
        mementoCache.invalidate(sessionId);
    }
}
