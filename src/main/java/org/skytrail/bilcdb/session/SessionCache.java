package org.skytrail.bilcdb.session;

import org.skytrail.bilcdb.model.security.Auth;

public interface SessionCache {
    public String put(Auth auth);
    public Auth getBySessionId(String sessionId);
    public Iterable<String> getByAuth(Auth auth);
}
