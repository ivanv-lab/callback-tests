package ru.git.ivanv_lab.model.api.settings;

import ru.git.ivanv_lab.model.Transport;

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

        protected Builder withName(Transport transport){

        }

        protected Builder withMultiSignature(boolean active){
            this.multiSignature=active;
            return this;
        }

        protected Builder withModeration(boolean active){
            this.onModeration=active;
            return this;
        }

        protected Builder withTemplateOnly(boolean active){
            this.templateOnly=active;
            return this;
        }

        protected PartnerTransport build(){
            return new PartnerTransport(this);
        }
    }
}
