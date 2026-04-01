package ru.git.ivanv_lab.model.api.push;

import static ru.git.ivanv_lab.BaseTest.sqlFabric;

public class MessageTypeRule {
    private int id;
    private long messageTypeId;
    private String price;

    public MessageTypeRule(Builder builder){
        this.id= builder.id;
        this.messageTypeId=builder.messageTypeId;
        this.price= builder.price;
    }

    public static class Builder{
        private int id=0;
        private long messageTypeId;
        private String price;

        public Builder withMessageType(String messageTypeName){
            id++;
            this.messageTypeId = sqlFabric.getMessageTypeId(messageTypeName);
            return this;
        }

        public Builder withPrice(String price){
            this.price=price;
            return this;
        }

        public MessageTypeRule build(){
            return new MessageTypeRule(this);
        }
    }
}
