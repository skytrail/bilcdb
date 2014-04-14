package org.skytrail.bilcdb.auth;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import org.skytrail.bilcdb.model.security.DBUser;

public class DBOAuthAuthenticator implements OAuthAuthenticator {
    @Override
    public Optional<DBUser> authenticate(String accessToken) throws AuthenticationException {
        return Optional.absent();
    }
}
