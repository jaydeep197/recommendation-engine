package com.example.helloworld.resources;

import com.example.helloworld.core.Device;
import com.example.helloworld.core.DeviceType;
import com.example.helloworld.core.SmartApp;
import com.example.helloworld.core.Template;
import com.example.helloworld.db.DeviceDao;
import com.example.helloworld.db.DeviceTypeDao;
import com.example.helloworld.db.PersonDAO;
import com.example.helloworld.db.SmartAppDao;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yammer.dropwizard.hibernate.UnitOfWork;
import com.yammer.dropwizard.jersey.caching.CacheControl;
import com.yammer.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by jaydeep.gc on 5/26/2017.
 */

@Path("/recommend")
@Produces(MediaType.APPLICATION_JSON)
public class RecommendResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldResource.class);

    ObjectMapper mapper = new ObjectMapper();
    JsonNode deviceTypeToCapability;
    JsonNode deviceToDeviceType;
    JsonNode smartAppToCapability;
    Integer deviceCount;

    private final DeviceDao deviceDao;
    private final DeviceTypeDao deviceTypeDao;
    private final SmartAppDao smartAppDao;




    public RecommendResource(DeviceDao deviceDao, DeviceTypeDao deviceTypeDao, SmartAppDao smartAppDao) {
        this.deviceDao = deviceDao;
        this.deviceTypeDao = deviceTypeDao;
        this.smartAppDao = smartAppDao;
    }


    @GET
    @UnitOfWork
    @Timed(name = "get-requests")
    @CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.DAYS)
    public List<String> sayHello() {
        return getSmartApp();
        //return new Saying(counter.incrementAndGet(), template.render(name));
    }


    private List<String> getSmartApp() {

        List<Device> devices = deviceDao.findAll();
        Set<String> deviceTypeCapabilities = new HashSet<>();
        Map<String, Set<String>> smartAppToCapability = new HashMap<>();
        List<String> smartAppIds = new ArrayList<>();


        for(Device device : devices) {
            List<DeviceType> deviceTypes = deviceTypeDao.findById(device.getDeviceTypeId());
            for (DeviceType deviceType : deviceTypes) {
                deviceTypeCapabilities.add(deviceType.getCapabiliy());
            }
        }

        List<SmartApp> smartApps = smartAppDao.findAll();
        for (SmartApp smartApp : smartApps) {
            String smartAppId = smartApp.getSmartAppId();
            if (smartAppToCapability.containsKey(smartAppId)) {
                Set<String> capability = smartAppToCapability.get(smartAppId);
                capability.add(smartApp.getCapability());
                smartAppToCapability.replace(smartAppId, capability);
            } else {
                smartAppToCapability.put(smartAppId, new HashSet<>(Arrays.asList(smartApp.getCapability())));
            }
        }

        for (String smartAppId : smartAppToCapability.keySet()) {
            if (deviceTypeCapabilities.containsAll(smartAppToCapability.get(smartAppId))) {
                smartAppIds.add(smartAppId);
            }
        }
        return smartAppIds;
    }

}
