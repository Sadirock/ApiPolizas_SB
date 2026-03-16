package com.segurosbolivar.polizasapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String apiKey;
    private double ipc;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public double getIpc() {
        return ipc;
    }

    public void setIpc(double ipc) {
        this.ipc = ipc;
    }
}
