package com.hickory.services;

import org.flywaydb.core.Flyway;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by Evgeny Frolov on 26/11/14.
 */
@WebListener
public class Initializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        Flyway flyway = new Flyway();

        flyway.setDataSource("jdbc:postgresql://localhost:5432/komruscrm", "komruscrm", "10b123bd");
        flyway.setOutOfOrder(true);

        flyway.migrate();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {

    }
}
