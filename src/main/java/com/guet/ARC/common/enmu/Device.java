package com.guet.ARC.common.enmu;

/**
 * @author Yulf
 * Date 2024/2/23
 */
public enum Device {

    PC("pc"),

    WECHAT("wechat");

    private String device;

    Device(String device) {
        this.device = device;
    }

    public String getDevice() {
        return device;
    }
}
