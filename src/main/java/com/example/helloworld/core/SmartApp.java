package com.example.helloworld.core;

import java.util.List;
import java.util.Map;

/**
 * Created by jaydeep.gc on 5/25/2017.
 */
public class SmartApp {


    private Boolean isInstalled;
    private Map<String, Boolean> capabilities;

    public Map<String, Boolean> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Map<String, Boolean> capabilities) {
        this.capabilities = capabilities;
    }

    public void updateAvailability(String capabilityId, Boolean isAvailable) {
        this.capabilities.replace(capabilityId, isAvailable);
    }


    public Boolean getIsInstalled() {
        return isInstalled;
    }

    public void setIsInstalled(Boolean isInstalled) {
        this.isInstalled = isInstalled;
    }

}
