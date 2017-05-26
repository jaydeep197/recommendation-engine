package com.example.helloworld.core;

import com.sun.xml.internal.ws.developer.Serialization;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by jaydeep.gc on 5/25/2017.
 */

@Entity
@Table(name = "smartApp")
@NamedQueries({
        @NamedQuery(
                name = "com.example.helloworld.core.SmartApp.findAll",
                query = "SELECT sa FROM SmartApp sa"
        ),
        @NamedQuery(
                name = "com.example.helloworld.core.SmartApp.findById",
                query = "SELECT sa FROM SmartApp sa WHERE sa.smartAppId = :smartAppId"
        )
})
public class SmartApp implements Serializable {


    @Id
    @Column(name = "smartAppId", nullable = false)
    private String smartAppId;


    @Id
    @Column(name = "capability", nullable = false)
    private String capability;

    public String getSmartAppId() {
        return smartAppId;
    }

    public void setSmartAppId(String smartAppId) {
        this.smartAppId = smartAppId;
    }

    public String getCapability() {
        return capability;
    }

    public void setCapability(String capability) {
        this.capability = capability;
    }
}
