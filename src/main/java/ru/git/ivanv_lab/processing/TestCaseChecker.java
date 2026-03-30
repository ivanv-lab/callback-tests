package ru.git.ivanv_lab.processing;

import ru.git.ivanv_lab.callback.CallbackKey;
import ru.git.ivanv_lab.callback.CallbackServer;
import ru.git.ivanv_lab.exception.CallbackNotExistsException;
import ru.git.ivanv_lab.model.Status;
import ru.git.ivanv_lab.model.Transport;
import tools.jackson.databind.JsonNode;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCaseChecker {

    public void checkCase(TestCase testCase) {
        String messageId = testCase.getCaseBody().getMessageId();
        String recipient = testCase.getCaseBody().getRecipient();
        Set<Transport> expectedTransports=new HashSet<>();

        for (CaseCondition condition : testCase.getCaseConditions()) {

            int httpStatusCode = condition.getExpectedHttpStatusCode();
            String errorDescription = condition.getConditionErrorDescription();
            Transport transport = condition.getConditionTransport();
            Status status = condition.getConditionStatus();
            expectedTransports.add(transport);

            JsonNode callbackNode=null;
            CallbackKey callbackKey=null;

            //Если сообщение не было отправлено - не проверяем
            if(httpStatusCode!=200) return;

            //Проверка ошибки при удачной отправке: Recipient is in blacklist
            if(!errorDescription.isEmpty()){
                callbackKey = new CallbackKey(messageId);
                callbackNode = CallbackServer.getCallBack(callbackKey);
                if(callbackNode==null) throw new CallbackNotExistsException(callbackKey);

                String actualErrorDescription=callbackNode.get("Error description").asString();

                assertEquals(actualErrorDescription,errorDescription,
                        String.format("""
                                Ожидаемое и фактическое описание ошибки не совпадает:
                                Ожидаемое описание: %s
                                фактическое описание: %s
                                """, actualErrorDescription, errorDescription));
            }

            //Проверка отправки по Транспорту(Если статус = null)
            if (transport!=null && status==null){
                callbackKey=new CallbackKey(messageId, transport);
                callbackNode = CallbackServer.getCallBack(callbackKey);
                if(callbackNode==null) throw new CallbackNotExistsException(callbackKey);
            }

            //Проверка отправки по Транспорту со Статусом
            if(transport!=null && status!=null){
                callbackKey=new CallbackKey(messageId, transport, status);
                callbackNode = CallbackServer.getCallBack(callbackKey);
                if(callbackNode==null) throw new CallbackNotExistsException(callbackKey);
            }
        }

        //Проверка отсутствия других лишних отправок
        Set<Transport> unexpectedTransports=getOtherTransports(expectedTransports);
        for(Transport unexpectedTransport:unexpectedTransports){

        }
    }

    private Set<Transport> getOtherTransports(Set<Transport> expectedTransports){
        Transport[] allTransports=Transport.values();
        Set<Transport> otherTransports=new HashSet<>();

        for(Transport expectedTransport:expectedTransports){
            for (Transport allTransport : allTransports) {
                if (allTransport.equals(expectedTransport))
                    otherTransports.add(allTransport);
            }
        }

        return otherTransports;
    }
}
