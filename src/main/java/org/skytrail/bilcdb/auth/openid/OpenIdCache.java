package org.skytrail.bilcdb.auth.openid;

import com.google.common.base.Optional;
import org.openid4java.consumer.ConsumerManager;

/**
 * Created by herndon on 4/13/14.
 */
public interface OpenIdCache {

    public Optional<DiscoveryInformationMemento> getMemento(String sessionid);
    public String putMemento(DiscoveryInformationMemento memento);
    public void deleteMemento(String sessionId);
}

