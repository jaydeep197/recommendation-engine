package com.example.helloworld.db;

import com.example.helloworld.core.DeviceType;
import com.google.common.base.Optional;
import com.yammer.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * Created by jaydeep.gc on 5/26/2017.
 */

public class DeviceTypeDao extends AbstractDAO<DeviceType> {
    public DeviceTypeDao(SessionFactory factory) {
        super(factory);
    }

    public List<DeviceType> findById(String deviceTypeId) {
        return list(namedQuery("com.example.helloworld.core.DeviceType.findById").setParameter("deviceTypeId", deviceTypeId));
    }

    public DeviceType create(DeviceType deviceType) {
        return persist(deviceType);
    }

    public List<DeviceType> findAll() {
        return list(namedQuery("com.example.helloworld.core.DeviceType.findAll"));
    }
}
