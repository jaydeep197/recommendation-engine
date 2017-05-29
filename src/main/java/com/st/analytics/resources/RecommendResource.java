package com.st.analytics.resources;

import com.st.analytics.core.Device;
import com.st.analytics.core.DeviceType;
import com.st.analytics.core.SmartApp;
import com.st.analytics.db.DeviceDao;
import com.st.analytics.db.DeviceTypeDao;
import com.st.analytics.db.SmartAppDao;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yammer.dropwizard.hibernate.UnitOfWork;
import com.yammer.dropwizard.jersey.caching.CacheControl;
import com.yammer.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by jaydeep.gc on 5/26/2017.
 */

@Path("/recommend")
@Produces(MediaType.APPLICATION_JSON)
public class RecommendResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecommendResource.class);

    private final DeviceDao deviceDao;
    private final DeviceTypeDao deviceTypeDao;
    private final SmartAppDao smartAppDao;

    private Set<String> deviceTypeCapabilities = new HashSet<>();
    private Map<String, Set<String>> smartAppToCapability = new HashMap<>();
    private Boolean loaded = false;



    public RecommendResource(DeviceDao deviceDao, DeviceTypeDao deviceTypeDao, SmartAppDao smartAppDao) {
        this.deviceDao = deviceDao;
        this.deviceTypeDao = deviceTypeDao;
        this.smartAppDao = smartAppDao;
    }

    private void loadCapabilities() {
        if (!loaded) {
            List<Device> devices = deviceDao.findAll();
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
            loaded = true;
        }
    }


    @GET
    @UnitOfWork
    @Path("/load")
    public void load() {
        loaded = false;
        loadCapabilities();
    }

    @GET
    @UnitOfWork
    @Path("/{deviceId}")
    public List<String> getSmartApp(@PathParam("deviceId") String deviceId) {
        loadCapabilities();
        Device device = deviceDao.findById(deviceId).get();
        List<DeviceType> deviceTypes = deviceTypeDao.findById(device.getDeviceTypeId());
        for (DeviceType deviceType : deviceTypes) {
            deviceTypeCapabilities.add(deviceType.getCapabiliy());
        }
        List<String> smartAppIds = new ArrayList<>();
        for (String smartAppId : smartAppToCapability.keySet()) {
            if (deviceTypeCapabilities.containsAll(smartAppToCapability.get(smartAppId))) {
                smartAppIds.add(smartAppId);
            }
        }
        return smartAppIds;
    }









    /*
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

*/
}
