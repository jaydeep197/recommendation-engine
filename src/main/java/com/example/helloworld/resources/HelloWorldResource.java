package com.example.helloworld.resources;

import com.example.helloworld.core.Saying;
import com.example.helloworld.core.Template;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Optional;
import com.yammer.dropwizard.jersey.caching.CacheControl;
import com.yammer.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Path("/hello-word")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldResource.class);

    private final Template template;
    private final AtomicLong counter;
    ObjectMapper mapper = new ObjectMapper();
    JsonNode deviceTypeToCapability;
    JsonNode deviceToDeviceType;
    JsonNode smartAppToCapability;
    Integer deviceCount;
    public HelloWorldResource(Template template) {
        this.template = template;
        this.counter = new AtomicLong();
        loadData();
    }

    private void loadData() {
        try {
            deviceTypeToCapability = mapper.readTree(new File("C:\\Users\\jaydeep.gc\\Desktop\\ST Analytics\\dropwizard-gradle\\src\\main\\java\\com\\example\\helloworld\\resources\\DeviceTypeToCapability.json"));
            smartAppToCapability = mapper.readTree(new File("C:\\Users\\jaydeep.gc\\Desktop\\ST Analytics\\dropwizard-gradle\\src\\main\\java\\com\\example\\helloworld\\resources\\SmartAppToCapability.json"));
            deviceToDeviceType = mapper.readTree(new File("C:\\Users\\jaydeep.gc\\Desktop\\ST Analytics\\dropwizard-gradle\\src\\main\\java\\com\\example\\helloworld\\resources\\DeviceToDeviceType.json"));
        } catch (Exception e) {

        }
        deviceCount = deviceToDeviceType.size();
    }


    @POST
    @Path("/pair")
    public JsonNode pair(@QueryParam("deviceTypeId") Optional<String> deviceTypeId) throws Exception {
        ((ObjectNode)deviceToDeviceType).put("Device" + String.valueOf(deviceCount++), deviceTypeId.get());
        mapper.writeValue(new File("C:\\Users\\jaydeep.gc\\Desktop\\ST Analytics\\dropwizard-gradle\\src\\main\\java\\com\\example\\helloworld\\resources\\DeviceToDeviceType.json"), deviceToDeviceType);
        return deviceToDeviceType;
    }

    @POST
    @Path("/clear")
    public JsonNode clear(@QueryParam("deviceTypeId") Optional<String> deviceTypeId) throws Exception {
        deviceToDeviceType =  mapper.readTree("{}");
        deviceCount = deviceToDeviceType.size();
        mapper.writeValue(new File("C:\\Users\\jaydeep.gc\\Desktop\\ST Analytics\\dropwizard-gradle\\src\\main\\java\\com\\example\\helloworld\\resources\\DeviceToDeviceType.json"), deviceToDeviceType);
        return deviceToDeviceType;
    }

    @GET
    @Timed(name = "get-requests")
    @CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.DAYS)
    public List<String> sayHello() {
        return getSmartApp();
        //return new Saying(counter.incrementAndGet(), template.render(name));
    }


    @GET
    @Path("/deviceTypeToCapability")
    @Timed(name = "get-requests")
    @CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.DAYS)
    public JsonNode getCapabilityFromDeviceTypeId() {
        return deviceTypeToCapability;
    }



    @GET
    @Path("/smartAppToCapability")
    @Timed(name = "get-requests")
    @CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.DAYS)
    public JsonNode getCapabilityFromSmartAppId() {
        return smartAppToCapability;
    }


    @GET
    @Path("/deviceToDeviceType")
    @Timed(name = "get-requests")
    @CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.DAYS)
    public JsonNode getDeviceFromDeviceTypeId() {
        return deviceToDeviceType;
    }



    private List<String> getSmartApp() {
        JsonNode capabilitiesNode;
        Set<String> capabilities = new HashSet<>();
        Set<String> capabilitiesSmartApp = new HashSet<>();
        List<String> smartApps = new ArrayList<>();
        Iterator<String> fieldNames = deviceToDeviceType.fieldNames();
        while(fieldNames.hasNext()) {
            capabilitiesNode = deviceTypeToCapability.findValue(
                    deviceToDeviceType.findValue(fieldNames.next()).asText());
            for (JsonNode capability : capabilitiesNode) {
                capabilities.add(capability.asText());
            }
        }


        fieldNames = smartAppToCapability.fieldNames();
        while(fieldNames.hasNext()){
            capabilitiesSmartApp.clear();
            String smartAppId = fieldNames.next();
            JsonNode capabilitiesSmartAppNode = smartAppToCapability.findValue(smartAppId);
            for(JsonNode capability : capabilitiesSmartAppNode) {
                capabilitiesSmartApp.add(capability.asText());
            }
            if(capabilities.containsAll(capabilitiesSmartApp)) {
                smartApps.add(smartAppId);
            }
        }

        return smartApps;
    }

    @POST
    public void receiveHello(@Valid Saying saying) {
        LOGGER.info("Received a saying: {}", saying);
    }
}
