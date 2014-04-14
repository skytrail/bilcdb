package org.skytrail.bilcdb.session;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.skytrail.bilcdb.session.memory.InMemorySessionCache;

/**
 * Created by herndon on 4/13/14.
 */
public class SessionModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(SessionCache.class).to(InMemorySessionCache.class).in(Scopes.SINGLETON);
    }
}
