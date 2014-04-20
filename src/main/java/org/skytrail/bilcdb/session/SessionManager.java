package org.skytrail.bilcdb.session;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import org.skytrail.bilcdb.model.security.DbUser;
import org.skytrail.bilcdb.model.security.Auth;

public class SessionManager {

    SessionCache cache;
    @Inject
    public SessionManager(SessionCache sessionCache) {
        this.cache = sessionCache;
    }

    public Optional<DbUser> get(String sessionId) {
        DbUser user = cache.getBySessionId(sessionId).getUser();
        if (user != null) {
           return Optional.of(user);
        }
        return Optional.absent();
    }

    public String create(Auth auth) {
        for(String session : cache.getByAuth(auth)) {
            return session;
        }
        return cache.put(auth);
    }

    public void deleteSession(String session) {
        cache.delete(session);
    }
}
