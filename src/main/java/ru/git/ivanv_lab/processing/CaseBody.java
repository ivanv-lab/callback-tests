package ru.git.ivanv_lab.processing;

public class CaseBody {
    private String messageId;
    private String recipient;
    private String value;

    public CaseBody(String messageId, String recipient, String value) {
        this.messageId = messageId;
        this.recipient = recipient;
        this.value = value;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getValue() {
        return value;
    }
}
