package org.skytrail.bilcdb.auth;

import io.dropwizard.auth.Authenticator;
import org.skytrail.bilcdb.model.security.DbUser;

public interface BILCAuthenticator<T> extends Authenticator<T, DbUser> {
}
