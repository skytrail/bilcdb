package org.skytrail.bilcdb;

import org.skytrail.bilcdb.auth.DBBasicAuthenticator;
import org.skytrail.bilcdb.auth.openid.OpenIDAuthenticator;
import org.skytrail.bilcdb.auth.openid.OpenIDRestrictedToProvider;
import org.skytrail.bilcdb.cli.RenderCommand;
import org.skytrail.bilcdb.model.security.DBUser;
import org.skytrail.bilcdb.model.Template;
import org.skytrail.bilcdb.db.BasicAuthDAO;
import org.skytrail.bilcdb.health.TemplateHealthCheck;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.skytrail.bilcdb.resources.*;

public class BILCApplication extends Application<BILCConfiguration> {
    public static void main(String[] args) throws Exception {
        new BILCApplication().run(args);
    }

    private final HibernateBundle<BILCConfiguration> hibernateBundle =
            new HibernateBundle<BILCConfiguration>(DBUser.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(BILCConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<BILCConfiguration> bootstrap) {
        bootstrap.addCommand(new RenderCommand());
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new MigrationsBundle<BILCConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(BILCConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new ViewBundle());
    }

    @Override
    public void run(BILCConfiguration configuration,
                    Environment environment) throws ClassNotFoundException {
        final BasicAuthDAO dao = new BasicAuthDAO(hibernateBundle.getSessionFactory());
        final Template template = configuration.buildTemplate();

        environment.healthChecks().register("template", new TemplateHealthCheck(template));

        environment.jersey().register(new BasicAuthProvider<>(new DBBasicAuthenticator(),
                                                              "SUPER SECRET STUFF"));


        environment.jersey().register(new OpenIDRestrictedToProvider<DBUser>(new OpenIDAuthenticator(), "OpenID"));
        environment.jersey().register(new HelloWorldResource(template));
        environment.jersey().register(new ViewResource());
    }
}
