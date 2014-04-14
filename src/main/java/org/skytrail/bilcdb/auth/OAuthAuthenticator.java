package org.skytrail.bilcdb.auth;

import io.dropwizard.auth.Authenticator;
import org.skytrail.bilcdb.model.security.DBUser;

public interface OAuthAuthenticator extends Authenticator<String, DBUser> {
}