package org.skytrail.bilcdb;

import com.bazaarvoice.dropwizard.assets.ConfiguredAssetsBundle;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.skytrail.bilcdb.auth.basic.BasicAuthModule;
import org.skytrail.bilcdb.auth.openid.OpenIDAuthenticator;
import org.skytrail.bilcdb.auth.openid.OpenIDRestrictedToProvider;
import org.skytrail.bilcdb.auth.openid.OpenIdCache;
import org.skytrail.bilcdb.auth.openid.OpenIdModule;
import org.skytrail.bilcdb.cli.RenderCommand;
import org.skytrail.bilcdb.db.OpenIdAuthDAO;
import org.skytrail.bilcdb.filters.UIFilter;
import org.skytrail.bilcdb.model.security.DBUser;
import org.skytrail.bilcdb.model.Template;
import org.skytrail.bilcdb.health.TemplateHealthCheck;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.skytrail.bilcdb.resources.*;
import org.skytrail.bilcdb.session.SessionManager;
import org.skytrail.bilcdb.session.SessionModule;

import javax.servlet.DispatcherType;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

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
        return "bilcdb";
    }

    @Override
    public void initialize(Bootstrap<BILCConfiguration> bootstrap) {
        bootstrap.addCommand(new RenderCommand());
        bootstrap.addBundle(new ConfiguredAssetsBundle("/assets/", "/ui"));

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

        List<AbstractModule> modules = Arrays.asList(new SessionModule(), new OpenIdModule());

        if (configuration.getBasicAuthConfiguration() != null) {
            modules.add(new BasicAuthModule());
        }
        Injector injector = Guice.createInjector(modules);

        final Template template = configuration.buildTemplate();

        environment.healthChecks().register("template", new TemplateHealthCheck(template));

        environment.servlets().addFilter("DBFilter",
                new UIFilter(new UIFilter.Redirect("/database", "/ui/database/index.html")))
                    .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        // TODO(jlh): Guice-ify this better
        environment.jersey().register(new OpenIDRestrictedToProvider(
                new OpenIDAuthenticator(injector.getInstance(SessionManager.class)), "OpenID"));
        environment.jersey().register(new HelloWorldResource(template));
        environment.jersey().register(new OpenIdResource(
                injector.getInstance(OpenIdCache.class),
                new OpenIdAuthDAO(hibernateBundle.getSessionFactory()),
                injector.getInstance(SessionManager.class)));
        environment.jersey().register(new ViewResource());
    }
}
