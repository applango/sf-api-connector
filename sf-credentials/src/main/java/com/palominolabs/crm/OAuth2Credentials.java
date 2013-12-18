package com.palominolabs.crm;

/**
 * Created with IntelliJ IDEA. User: bjhlista Date: 12/11/13 Time: 10:32 AM
 */
public class OAuth2Credentials implements Credentials {

    String organizationId;
    String hostURL;
    String restURL;
    String partnerServerURL;
    String metadataServerURL;
    String token;

    public OAuth2Credentials() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getPartnerServerURL() {
        return partnerServerURL;
    }

    public void setPartnerServerURL(String partnerServerURL) {
        this.partnerServerURL = partnerServerURL;
    }

    public String getMetadataServerURL() {
        return metadataServerURL;
    }

    public void setMetadataServerURL(String metadataServerURL) {
        this.metadataServerURL = metadataServerURL;
    }

    public String getHostURL() {
        return hostURL;
    }

    public void setHostURL(String hostURL) {
        this.hostURL = hostURL;
    }

    public String getRestURL() {
        return restURL;
    }

    public void setRestURL(String restURL) {
        this.restURL = restURL;
    }
}
