package ru.git.ivanv_lab.model.api.settings;

import ru.git.ivanv_lab.model.Transport;

import static ru.git.ivanv_lab.BaseTest.fabric;

public class PartnerTransport {
    private int id;
    private boolean multiSignature;
    private boolean onModeration;
    private boolean templateOnly;

    public PartnerTransport(Builder builder){
        this.id=builder.id;
        this.multiSignature=builder.multiSignature;
        this.onModeration=builder.onModeration;
        this.templateOnly=builder.templateOnly;
    }

    public static class Builder{
        private int id;
        private boolean multiSignature;
        private boolean onModeration;
        private boolean templateOnly;

        public Builder withName(Transport transport){
            id = fabric.getTransportId(transport.getName());
            return this;
        }

        public Builder withMultiSignature(boolean active){
            this.multiSignature=active;
            return this;
        }

        public Builder withModeration(boolean active){
            this.onModeration=active;
            return this;
        }

        public Builder withTemplateOnly(boolean active){
            this.templateOnly=active;
            return this;
        }

        public PartnerTransport build(){
            return new PartnerTransport(this);
        }
    }
}
