package org.skytrail.bilcdb.db;

import com.google.common.base.Optional;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.skytrail.bilcdb.model.security.OpenIdAuth;

public class OpenIdAuthDAO extends AbstractDAO<OpenIdAuth> {

    public OpenIdAuthDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<OpenIdAuth> findById(String id) {
        return Optional.fromNullable(get("1234"));
    }

    public OpenIdAuth create(OpenIdAuth openIDAuth) {
        return persist(openIDAuth);
    }
}
