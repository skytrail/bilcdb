package org.skytrail.bilcdb.db;

import com.google.common.base.Optional;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.skytrail.bilcdb.model.security.OpenIdAuth;

/**
 * Created by herndon on 4/13/14.
 */
public class OpenIdAuthDAO extends AbstractDAO<OpenIdAuth> {

    public OpenIdAuthDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<OpenIdAuth> findByKey(String key) {
        return Optional.fromNullable(get(key));
    }

    public OpenIdAuth create(OpenIdAuth openIDAuth) {
        return persist(openIDAuth);
    }
}
