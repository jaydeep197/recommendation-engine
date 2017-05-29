package com.st.analytics.db;

import com.st.analytics.core.SmartApp;
import com.yammer.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * Created by jaydeep.gc on 5/26/2017.
 */

public class SmartAppDao extends AbstractDAO<SmartApp> {
    public SmartAppDao(SessionFactory factory) {
        super(factory);
    }

    public List<SmartApp> findById(String smartAppId) {
        return list(namedQuery("com.example.helloworld.core.SmartApp.findById").setParameter("smartAppId", smartAppId));
    }

    public SmartApp create(SmartApp smartApp) {
        return persist(smartApp);
    }

    public List<SmartApp> findAll() {
        return list(namedQuery("com.example.helloworld.core.SmartApp.findAll"));
    }
}
