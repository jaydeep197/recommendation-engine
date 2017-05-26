package com.example.helloworld.core;


import javax.persistence.*;
/**
 * Created by jaydeep.gc on 5/26/2017.
 */

@Entity
@Table(name = "device")
@NamedQueries({
        @NamedQuery(
                name = "com.example.helloworld.core.Device.findAll",
                query = "SELECT d FROM Device d"
        ),
        @NamedQuery(
                name = "com.example.helloworld.core.Device.findById",
                query = "SELECT d FROM Device d WHERE d.deviceId = :deviceId"
        )
})
public class Device {

    @Id
    @Column(name = "deviceId", nullable = false)
    private String deviceId;

    @Column(name = "deviceTypeId", nullable = false)

    private String deviceTypeId;
    public String getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(String deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }



}
