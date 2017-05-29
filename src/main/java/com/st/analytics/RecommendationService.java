package com.st.analytics;

import com.st.analytics.auth.ExampleAuthenticator;
import com.st.analytics.cli.RenderCommand;
import com.st.analytics.core.*;
import com.st.analytics.db.DeviceDao;
import com.st.analytics.db.DeviceTypeDao;
import com.st.analytics.db.PersonDAO;
import com.st.analytics.db.SmartAppDao;
import com.st.analytics.health.TemplateHealthCheck;
import com.st.analytics.resources.*;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.hibernate.HibernateBundle;
import com.yammer.dropwizard.migrations.MigrationsBundle;

public class RecommendationService extends Service<RecommendConfiguration> {
    public static void main(String[] args) throws Exception {
        new RecommendationService().run(args);
    }

    private final HibernateBundle<RecommendConfiguration> hibernateBundle =
            new HibernateBundle<RecommendConfiguration>(Person.class, Device.class, DeviceType.class, SmartApp.class) {
                @Override
                public DatabaseConfiguration getDatabaseConfiguration(RecommendConfiguration configuration) {
                    return configuration.getDatabaseConfiguration();
                }
            };

    @Override
    public void initialize(Bootstrap<RecommendConfiguration> bootstrap) {
        bootstrap.setName("hello-world");
        bootstrap.addCommand(new RenderCommand());
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new MigrationsBundle<RecommendConfiguration>() {
            @Override
            public DatabaseConfiguration getDatabaseConfiguration(RecommendConfiguration configuration) {
                return configuration.getDatabaseConfiguration();
            }
        });
        bootstrap.addBundle(hibernateBundle);
    }

    @Override
    public void run(RecommendConfiguration configuration,
                    Environment environment) throws ClassNotFoundException {
        final PersonDAO dao = new PersonDAO(hibernateBundle.getSessionFactory());
        final DeviceDao deviceDao =  new DeviceDao(hibernateBundle.getSessionFactory());
        final DeviceTypeDao deviceTypeDao = new DeviceTypeDao(hibernateBundle.getSessionFactory());
        final SmartAppDao smartAppDao = new SmartAppDao(hibernateBundle.getSessionFactory());
        environment.addProvider(new BasicAuthProvider<User>(new ExampleAuthenticator(),
                                                            "SUPER SECRET STUFF"));

        final Template template = configuration.buildTemplate();

        environment.addHealthCheck(new TemplateHealthCheck(template));
        environment.addResource(new HelloWorldResource(template));
        environment.addResource(new RecommendResource(deviceDao, deviceTypeDao, smartAppDao));
        environment.addResource(new ProtectedResource());

        environment.addResource(new PeopleResource(dao));
        environment.addResource(new PersonResource(dao));
    }
}
