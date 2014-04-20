package org.skytrail.bilcdb.db;

import com.google.common.base.Optional;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.skytrail.bilcdb.model.security.BasicAuth;
import org.skytrail.bilcdb.model.security.DbUser;

public class DbUserDao extends AbstractDAO<DbUser>{

    public DbUserDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<DbUser> findById(int key) {
        return Optional.fromNullable(get(key));
    }

    public DbUser create(DbUser user) {
        return persist(user);
    }

}
