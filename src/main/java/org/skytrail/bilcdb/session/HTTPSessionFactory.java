package org.skytrail.bilcdb.session;

import org.skytrail.bilcdb.model.security.Auth;

public interface HTTPSessionFactory<T extends Auth> {
    String createSession(T auth);
}
