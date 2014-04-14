package org.skytrail.bilcdb.auth.basic;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import io.dropwizard.auth.basic.BasicCredentials;
import org.skytrail.bilcdb.auth.BILCAuthenticator;

/**
 * Created by herndon on 4/13/14.
 */
public class BasicAuthModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<BILCAuthenticator<BasicCredentials>>(){}).to(DBBasicAuthenticator.class);
    }
}
