package ru.git.ivanv_lab.callback;

import ru.git.ivanv_lab.model.general.Status;
import ru.git.ivanv_lab.model.general.Transport;

import java.io.Serializable;
import java.util.Objects;

public class CallbackKey implements Serializable {

    private String messageId;
    private Transport transport;
    private Status status;
    private String pushAppName;

    public CallbackKey(String messageId, Transport transport){
        this.messageId = messageId.toLowerCase();
        this.transport = transport;
    }

    public CallbackKey(String messageId, Transport transport, Status status) {
        this.messageId = messageId.toLowerCase();
        this.transport = transport;
        this.status = status;
    }

    public CallbackKey(String messageId, Transport transport, Status status, String pushAppName) {
        this.messageId = messageId.toLowerCase();
        this.transport = transport;
        this.status = status;
        this.pushAppName = pushAppName.toLowerCase();
    }

    public boolean matches(CallbackKey pattern){
        if(pattern.getMessageId()!=null && !pattern.getMessageId().equals(getMessageId()))
            return false;
        if(pattern.getTransport()!=null && !pattern.getTransport().equals(getTransport()))
            return false;
        if(pattern.getStatus()!=null && !pattern.getStatus().equals(getStatus()))
            return false;
        if(pattern.getPushAppName()!=null && pushAppName!=null)
            if(!pattern.getPushAppName().equals(getPushAppName()))
                return false;

        return true;
    }

    public String getMessageId() {
        return messageId;
    }

    public Transport getTransport() {
        return transport;
    }

    public Status getStatus() {
        return status;
    }

    public String getPushAppName() {
        return pushAppName;
    }

    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if(o==null || getClass() != o.getClass()) return false;

        CallbackKey that=(CallbackKey) o;
        return Objects.equals(getMessageId(), that.getMessageId()) &&
               Objects.equals(getTransport(), that.getTransport()) &&
               Objects.equals(getStatus(), that.getStatus()) &&
               Objects.equals(getPushAppName(), that.getPushAppName());
    }

    @Override
    public int hashCode(){
        return Objects.hash(getMessageId(),getTransport(),getStatus(),getPushAppName());
    }

    @Override
    public String toString(){
        String stringKey=messageId;
        if(transport!=null) stringKey+="-"+transport;
        if(status!=null) stringKey+="-"+status;
        if(pushAppName!=null) stringKey+="-"+pushAppName;

        return stringKey;
    }
}
