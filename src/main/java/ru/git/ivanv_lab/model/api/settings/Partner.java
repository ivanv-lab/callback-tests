package ru.git.ivanv_lab.model.api.settings;

public class Partner {
    private String name;
    private PartnerTransport[] partnerTransports;
    private boolean prepaid;
    private int status;

    public Partner(Builder builder){
        this.name=builder.name;
        this.partnerTransports= builder.partnerTransports;
        this.prepaid=builder.prepaid;
        this.status=builder.status;
    }

    public static class Builder{
        private String name;
        private PartnerTransport[] partnerTransports;
        private boolean prepaid;
        private int status;

        public Builder withName(String name){
            this.name=name;
            return this;
        }

        public Builder withTransports(PartnerTransport[] partnerTransports){
            this.partnerTransports=partnerTransports;
            return this;
        }

        public Builder withPrepaid(boolean active){
            this.prepaid=active;
            return this;
        }

        public Builder withStatus(boolean active){
            this.status=active?1:0;
            return this;
        }

        public Partner build(){
            return new Partner(this);
        }
    }
}
