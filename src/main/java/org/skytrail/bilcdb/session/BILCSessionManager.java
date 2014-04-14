package org.skytrail.bilcdb.session;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import org.skytrail.bilcdb.model.security.DBUser;
import org.skytrail.bilcdb.model.security.Auth;

public class BILCSessionManager implements SessionManager {

    SessionCache cache;
    @Inject
    public BILCSessionManager(SessionCache sessionCache) {
        this.cache = sessionCache;
    }

    @Override
    public Optional<DBUser> get(String sessionId) {
        DBUser user = cache.getBySessionId(sessionId).getUser();
        if (user != null) {
           return Optional.of(user);
        }
        return Optional.absent();
    }

    @Override
    public String create(Auth auth) {
        for(String session : cache.getByAuth(auth)) {
            return session;
        }
        return cache.put(auth);
    }
}
