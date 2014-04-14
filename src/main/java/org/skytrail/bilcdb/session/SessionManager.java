package org.skytrail.bilcdb.session;

import com.google.common.base.Optional;
import org.skytrail.bilcdb.model.security.DBUser;
import org.skytrail.bilcdb.model.security.Auth;

public interface SessionManager {
    Optional<DBUser> get(String sessionId);
    String create(Auth auth);
}
