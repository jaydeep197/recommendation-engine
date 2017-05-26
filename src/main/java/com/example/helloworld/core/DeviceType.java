package com.example.helloworld.core;

import com.sun.xml.internal.ws.developer.Serialization;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by jaydeep.gc on 5/26/2017.
 */

@Entity
@Table(name = "deviceType")
@NamedQueries({
        @NamedQuery(
                name = "com.example.helloworld.core.DeviceType.findAll",
                query = "SELECT dp FROM DeviceType dp"
        ),
        @NamedQuery(
                name = "com.example.helloworld.core.DeviceType.findById",
                query = "SELECT dp FROM DeviceType dp WHERE dp.deviceTypeId = :deviceTypeId"
        )
})
public class DeviceType implements Serializable {


    @Id
    @Column(name = "deviceTypeId", nullable = false)
    private String deviceTypeId;


    @Id
    @Column(name = "capability", nullable = false)
    private String capabiliy;

    public String getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(String deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    public String getCapabiliy() {
        return capabiliy;
    }

    public void setCapabiliy(String capabiliy) {
        this.capabiliy = capabiliy;
    }
}
