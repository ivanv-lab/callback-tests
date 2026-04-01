package ru.git.ivanv_lab.model.api.push;

import static ru.git.ivanv_lab.BaseTest.sqlFabric;

public class PushTariff {
    private long partnerId;
    private String price;
    private int isMessageTypeTariff;
    private MessageTypeRule[] messageTypeRules;

    public PushTariff(Builder builder) {
        this.partnerId = builder.partnerId;
        this.price = builder.price;
        this.isMessageTypeTariff = builder.isMessageTypeTariff;
        this.messageTypeRules = builder.messageTypeRules;
    }

    public long getPartnerId() {
        return partnerId;
    }

    public String getPrice() {
        return price;
    }

    public int getIsMessageTypeTariff() {
        return isMessageTypeTariff;
    }

    public MessageTypeRule[] getMessageTypeRules() {
        return messageTypeRules;
    }

    public static class Builder {
        private long partnerId;
        private String price;
        private int isMessageTypeTariff;
        private MessageTypeRule[] messageTypeRules;

        public Builder withPartner(String partnerName) {
            this.partnerId = sqlFabric.getPartnerId(partnerName);
            return this;
        }

        public Builder withPrice(String price) {
            this.price = price;
            return this;
        }

        public Builder withIsMessageTypeTariff(boolean active) {
            this.isMessageTypeTariff = active ? 1 : 0;
            return this;
        }

        public Builder withMessageTypeRules(MessageTypeRule[] rules) {
            this.messageTypeRules = rules;
            return this;
        }

        public PushTariff build() {
            return new PushTariff(this);
        }
    }
}
