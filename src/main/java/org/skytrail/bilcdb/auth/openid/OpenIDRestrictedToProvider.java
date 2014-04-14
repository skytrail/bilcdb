package org.skytrail.bilcdb.auth.openid;


import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import io.dropwizard.auth.Authenticator;
import org.skytrail.bilcdb.auth.annotation.RestrictedTo;
import org.skytrail.bilcdb.model.security.DBUser;

import javax.inject.Inject;

/**
 * <p>Authentication provider to provide the following to Jersey:</p>
 * <ul>
 * <li>Bridge between Dropwizard and Jersey for HMAC authentication</li>
 * <li>Provides additional {@link org.skytrail.bilcdb.model.security.Authority} information</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class OpenIDRestrictedToProvider implements InjectableProvider<RestrictedTo, DBUser> {

    private final Authenticator<OpenIDCredentials, DBUser> authenticator;
    private final String realm;

    /**
     * Creates a new {@link OpenIDRestrictedToProvider} with the given {@link io.dropwizard.auth.Authenticator} and realm.
     *
     * @param authenticator the authenticator which will take the {@link OpenIDCredentials} and
     *                      convert them into instances of {@code T}
     * @param realm         the name of the authentication realm
     */
    public OpenIDRestrictedToProvider(Authenticator<OpenIDCredentials, DBUser> authenticator, String realm) {
        this.authenticator = authenticator;
        this.realm = realm;
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable getInjectable(ComponentContext componentContext, RestrictedTo restrictedTo, DBUser dbUser) {
        return new OpenIDRestrictedToUser(authenticator, realm, restrictedTo.value());
    }
}

