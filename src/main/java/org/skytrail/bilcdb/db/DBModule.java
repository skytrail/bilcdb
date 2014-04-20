package org.skytrail.bilcdb.db;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import org.skytrail.bilcdb.BILCConfiguration;
import org.skytrail.bilcdb.model.security.DbUser;

/**
 * Created by herndon on 4/13/14.
 */
public class DBModule extends AbstractModule {

    @Override
    protected void configure() {
        HibernateBundle<BILCConfiguration> hibernateBundle =
                new HibernateBundle<BILCConfiguration>(DbUser.class) {
                    @Override
                    public DataSourceFactory getDataSourceFactory(BILCConfiguration configuration) {
                        return configuration.getDataSourceFactory();
                    }
                };
        bind(new TypeLiteral<HibernateBundle<BILCConfiguration>>() {
        }).toInstance(hibernateBundle);
    }
}
