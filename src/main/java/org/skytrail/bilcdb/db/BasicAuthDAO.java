package org.skytrail.bilcdb.db;

import org.skytrail.bilcdb.model.security.BasicAuth;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

public class BasicAuthDAO extends AbstractDAO<BasicAuth> {
    public BasicAuthDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<BasicAuth> findByKey(String key) {
        return Optional.fromNullable(get(key));
    }

    public BasicAuth create(BasicAuth basicAuth) {
        return persist(basicAuth);
    }
}
