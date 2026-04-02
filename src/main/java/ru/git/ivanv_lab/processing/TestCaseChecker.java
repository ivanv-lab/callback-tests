package ru.git.ivanv_lab.processing;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.Test;
import ru.git.ivanv_lab.callback.CallbackGetter;
import ru.git.ivanv_lab.callback.CallbackKey;
import ru.git.ivanv_lab.callback.CallbackServer;
import ru.git.ivanv_lab.exception.CallbackNotExistsException;
import ru.git.ivanv_lab.model.general.Status;
import ru.git.ivanv_lab.model.general.Transport;
import tools.jackson.databind.JsonNode;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestCaseChecker {

    public void checkCase(TestCase testCase) {
        Allure.addAttachment("Test case",testCase.logTestCase());

        String messageId = testCase.getCaseBody().getMessageId();
        String recipient = testCase.getCaseBody().getRecipient();
        Set<Transport> expectedTransports = new HashSet<>();

        for (CaseCondition condition : testCase.getCaseConditions()) {

            int httpStatusCode = condition.getExpectedHttpStatusCode();
            String errorDescription = condition.getConditionErrorDescription();
            Transport transport = condition.getConditionTransport();
            Status status = condition.getConditionStatus();
            expectedTransports.add(transport);

            JsonNode callbackNode = null;
            CallbackKey callbackKey = null;

            //Если сообщение не было отправлено - не проверяем
            if (httpStatusCode != 200) return;

            //Проверка ошибки при удачной отправке: Recipient is in blacklist
            if (!errorDescription.isEmpty()) {
                callbackKey = new CallbackKey(messageId, transport);
                callbackNode = CallbackServer.getCallBack(callbackKey);
                if (callbackNode == null) throw new CallbackNotExistsException(callbackKey);

                String actualErrorDescription = callbackNode.get("description").asString();

                assertEquals(actualErrorDescription, errorDescription,
                        String.format("""
                                Ожидаемое и фактическое описание ошибки не совпадает:
                                Ожидаемое описание: %s
                                фактическое описание: %s
                                """, actualErrorDescription, errorDescription));
            }

            //Проверка отправки по Транспорту(Если статус = null)
            if (transport != null && status == null) {
                callbackKey = new CallbackKey(messageId, transport);
                callbackNode = CallbackServer.getCallBack(callbackKey);
                if (callbackNode == null) throw new CallbackNotExistsException(callbackKey);
            }

            //Проверка отправки по Транспорту со Статусом
            if (transport != null && status != null) {
                callbackKey = new CallbackKey(messageId, transport, status);
                callbackNode = CallbackServer.getCallBack(callbackKey);
                if (callbackNode == null) throw new CallbackNotExistsException(callbackKey);
            }
        }

        //Проверка отсутствия других лишних отправок
        Set<Transport> unexpectedTransports = getOtherTransports(expectedTransports);
        for (Transport unexpectedTransport : unexpectedTransports) {
            CallbackKey key = new CallbackKey(messageId, unexpectedTransport);
            assertNull(CallbackServer.getCallBack(key), String.format("""
                    Сообщение с ключом '%s' не должно существовать
                    """, key));
        }
    }

    private Set<Transport> getOtherTransports(Set<Transport> expectedTransports) {
        Transport[] transports = Transport.values();
        Set<Transport> unExp=new HashSet<>();

        for(Transport transport:transports){
            if(!expectedTransports.contains(transport))
                unExp.add(transport);
        }

        return unExp;
    }
}
