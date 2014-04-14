package org.skytrail.bilcdb.auth.openid;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import org.skytrail.bilcdb.db.OpenIdAuthDAO;
import org.skytrail.bilcdb.model.security.DBUser;
import org.skytrail.bilcdb.session.SessionManager;

/**
 * <p>Authenticator to provide the following to application:</p>
 * <ul>
 * <li>Verifies the provided credentials are valid</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class OpenIDAuthenticator implements Authenticator<OpenIDCredentials, DBUser> {

    private final SessionManager sessionManager;
    private final OpenIdAuthDAO dao;

    @Inject
    public OpenIDAuthenticator(SessionManager sessionManager, OpenIdAuthDAO dao) {
        this.sessionManager = sessionManager;
        this.dao = dao;
    }

    @Override
    public Optional<DBUser> authenticate(OpenIDCredentials credentials) throws AuthenticationException {

        // Make sure the user exists in the cache
        // Get the User referred to by the API key
        Optional<DBUser> user = sessionManager.get(credentials.getSessionToken());
        if (!user.isPresent()) {
            return Optional.absent();
        }

        /*
        TODO (jlh): Role checks
        // Check that their authorities match their credentials
        if (!user.get().hasAllAuthorities(credentials.getRequiredAuthorities())) {
            return Optional.absent();
        }
        */

        return user;

    }

}
