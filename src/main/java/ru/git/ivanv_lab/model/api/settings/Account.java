package ru.git.ivanv_lab.model.api.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.git.ivanv_lab.model.general.Transport;

import static ru.git.ivanv_lab.BaseTest.sqlFabric;

public class Account {
    @JsonProperty("name")
    private final String name;
    @JsonProperty("login")
    private final String login;
    @JsonProperty("password")
    private final String password;
    @JsonProperty("protocol_id")
    private final int protocolId;
    @JsonProperty("partner_id")
    private final long partnerId;
    @JsonProperty("transport_id")
    private final int transportId;
    @JsonProperty("status")
    private final int status;
    @JsonProperty("push_statuses")
    private final int pushStatuses;
    @JsonProperty("url")
    private final String url;
    @JsonProperty("status_report")
    private final int statusReport;
    @JsonProperty("event_report")
    private final int eventReport;
    @JsonProperty("max_throughput")
    private final String maxThroughput;
    @JsonProperty("immediate_response_needed")
    private final int immediateResponseNeeded;
    @JsonProperty("additional_processing_rule")
    private final String additionalProcessingRule;

    public Account(Builder builder) {
        this.name = builder.name;
        this.login = builder.login;
        this.password = builder.password;
        this.protocolId = builder.protocolId;
        this.partnerId = builder.partnerId;
        this.transportId = builder.transportId;
        this.status = builder.status;
        this.pushStatuses = builder.pushStatuses;
        this.url = builder.url;
        this.statusReport = builder.statusReport;
        this.eventReport = builder.eventReport;
        this.maxThroughput = builder.maxThroughput;
        this.immediateResponseNeeded = builder.immediateResponseNeeded;
        this.additionalProcessingRule = builder.additionalProcessingRule;
    }

    public String getName() {
        return name;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public int getProtocolId() {
        return protocolId;
    }

    public long getPartnerId() {
        return partnerId;
    }

    public int getTransportId() {
        return transportId;
    }

    public int getStatus() {
        return status;
    }

    public int getPushStatuses() {
        return pushStatuses;
    }

    public String getUrl() {
        return url;
    }

    public int getStatusReport() {
        return statusReport;
    }

    public int getEventReport() {
        return eventReport;
    }

    public String getMaxThroughput() {
        return maxThroughput;
    }

    public int getImmediateResponseNeeded() {
        return immediateResponseNeeded;
    }

    public String getAdditionalProcessingRule() {
        return additionalProcessingRule;
    }

    public static class Builder {
        private String name;
        private String login;
        private String password;
        private int protocolId;
        private long partnerId;
        private int transportId;
        private int status;
        private int pushStatuses;
        private String url;
        private int statusReport;
        private int eventReport;
        private String maxThroughput;
        private int immediateResponseNeeded;
        private String additionalProcessingRule;

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withLogin(String login) {
            this.login = login;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withPartner(String partnerName){
            this.partnerId= sqlFabric.getPartnerId(partnerName);
            return this;
        }

        public Builder withTransport(Transport transport){
            this.transportId= sqlFabric.getTransportId(transport.getDbName());
            return this;
        }

        public Builder withStatus(boolean active){
            this.status=active?1:0;
            return this;
        }

        public Builder withProtocolHTTP(boolean pushStatusesActive, String url, boolean statusReport, boolean eventReport) {
            this.protocolId = sqlFabric.getProtocolId("HTTP");
            this.pushStatuses = pushStatusesActive ? 1 : 0;
            this.url = url.isEmpty() ? null : url;
            this.statusReport = statusReport ? 1 : 0;
            this.eventReport = eventReport ? 1 : 0;
            return this;
        }

        public Builder withProtocolSMPP(String maxThroughput, boolean immediateResponseNeeded,
                                        String additionalProcessingRule) {
            this.protocolId = sqlFabric.getProtocolId("SMPP");
            this.maxThroughput = maxThroughput;
            this.immediateResponseNeeded = immediateResponseNeeded ? 1 : 0;
            this.additionalProcessingRule = additionalProcessingRule.isEmpty() ? null : additionalProcessingRule;
            return this;
        }

        public Account build(){
            return new Account(this);
        }
    }
}
