package com.example.helloworld;

import com.example.helloworld.auth.ExampleAuthenticator;
import com.example.helloworld.cli.RenderCommand;
import com.example.helloworld.core.*;
import com.example.helloworld.db.DeviceDao;
import com.example.helloworld.db.DeviceTypeDao;
import com.example.helloworld.db.PersonDAO;
import com.example.helloworld.db.SmartAppDao;
import com.example.helloworld.health.TemplateHealthCheck;
import com.example.helloworld.resources.*;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.hibernate.HibernateBundle;
import com.yammer.dropwizard.migrations.MigrationsBundle;

public class HelloWorldService extends Service<HelloWorldConfiguration> {
    public static void main(String[] args) throws Exception {
        new HelloWorldService().run(args);
    }

    private final HibernateBundle<HelloWorldConfiguration> hibernateBundle =
            new HibernateBundle<HelloWorldConfiguration>(Person.class, Device.class, DeviceType.class, SmartApp.class) {
                @Override
                public DatabaseConfiguration getDatabaseConfiguration(HelloWorldConfiguration configuration) {
                    return configuration.getDatabaseConfiguration();
                }
            };

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
        bootstrap.setName("hello-world");
        bootstrap.addCommand(new RenderCommand());
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new MigrationsBundle<HelloWorldConfiguration>() {
            @Override
            public DatabaseConfiguration getDatabaseConfiguration(HelloWorldConfiguration configuration) {
                return configuration.getDatabaseConfiguration();
            }
        });
        bootstrap.addBundle(hibernateBundle);
    }

    @Override
    public void run(HelloWorldConfiguration configuration,
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
