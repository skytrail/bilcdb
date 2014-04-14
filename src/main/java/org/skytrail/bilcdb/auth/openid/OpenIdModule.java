package org.skytrail.bilcdb.auth.openid;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import io.dropwizard.auth.Authenticator;
import org.skytrail.bilcdb.model.security.DBUser;

public class OpenIdModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(OpenIdCache.class).to(InMemoryOpenIdCache.class);
        bind(new TypeLiteral<Authenticator<OpenIDCredentials, DBUser>>(){}).to(OpenIDAuthenticator.class);
    }
}
