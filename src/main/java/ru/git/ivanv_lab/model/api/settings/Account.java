package ru.git.ivanv_lab.model.api.settings;

public class Account {
    private final String name;
    private final String login;
    private final String password;
    private final int protocolId;
    private final long partnerId;
    private final int transportId;
    private final int status;
    private final int pushStatuses;
    private final String url;
    private final int statusReport;
    private final int eventReport;
    private final String maxThroughput;
    private final int immediateResponseNeeded;
    private final String additionalProcessingRule;

    public static class Builder{
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

        public Builder withName(String name){
            this.name=name;
            return this;
        }

        public Builder withLogin(String login){
            this.login=login;
            return this;
        }

        public Builder withPassword(String password){
            this.password=password;
            return this;
        }

        public enum Protocol{
            HTTP, SMPP
        }

        public Builder withProtocol(Protocol protocol){

        }
    }
}
