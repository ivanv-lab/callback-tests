package ru.git.ivanv_lab.callback;

import tools.jackson.databind.JsonNode;

public class CallbackGetter {

    public synchronized JsonNode getCallBack(CallbackKey key){
        JsonNode callback=CallbackServer.getCallBack(key);

        for(int i=0;i<5;i++){
            if(callback==null){
                try{
                    wait(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                callback=CallbackServer.getCallBack(key);
            }
        }

        return callback;
    }
}
