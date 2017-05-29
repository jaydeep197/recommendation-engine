package com.st.analytics.db;

import com.st.analytics.core.Device;
import com.google.common.base.Optional;
import com.yammer.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * Created by jaydeep.gc on 5/26/2017.
 */

public class DeviceDao extends AbstractDAO<Device> {
    public DeviceDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<Device> findById(String deviceId) {
        return Optional.fromNullable(get(deviceId));
    }

    public Device create(Device device) {
        return persist(device);
    }

    public List<Device> findAll() {
        return list(namedQuery("com.example.helloworld.core.Device.findAll"));
    }
}
