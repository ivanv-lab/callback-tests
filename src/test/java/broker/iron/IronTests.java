package broker.iron;

import base.BaseBrokerTests;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import ru.git.ivanv_lab.api.ApiWorker;
import ru.git.ivanv_lab.model.general.Status;
import ru.git.ivanv_lab.model.general.Transport;
import ru.git.ivanv_lab.processing.CaseBody;
import ru.git.ivanv_lab.processing.CaseCondition;
import ru.git.ivanv_lab.processing.TestCase;
import ru.git.ivanv_lab.processing.TestCaseChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Epic("broker-api")
@Feature("Smoke")
public class IronTests extends BaseBrokerTests {

    private final ApiWorker api = new ApiWorker("broker", "Iron", "IronAcc");
    private final TestCaseChecker checker=new TestCaseChecker();
    private static final List<TestCase> testCaseList=new ArrayList<>();

    @Order(1)
    @Execution(ExecutionMode.SAME_THREAD)
    @Story("Отправка единичного сообщения без шаблона")
    @DisplayName("1.1.1. Push->Viber->SMS. Проверка каскадирования")
    @Test
    @Tag("Iron1")
    @Description("1.1.1. Статус Прочитано(Read) для Push" +
                 "(Проверяем, что по Push получили статус о доставке. " +
                 "Viber и SMS не отправлены)")
    void ironOneOne() {
        TestCase testCase = new TestCase(1, "1.1.1. Push->Viber->SMS. Проверка каскадирования",
                "1.1.1. Статус Прочитано(Read) для Push" +
                "(Проверяем, что по Push получили статус о доставке. " +
                "Viber и SMS не отправлены)",
                new CaseBody(gen.genNumber(), "79100000041", ""),
                List.of(
                        new CaseCondition(200, "", Transport.PUSH, Status.READ)
                ));

        testCaseList.add(testCase);

        String request = """
                {
                    "messages": [
                        {
                            "recipient": "%s",
                            "message-id": "%s",
                            "push": {
                                "applications": "IronApp",
                                "ttl": "120",
                                "content": {
                                    "title": "IronOne",
                                    "text": "This is test message"
                                }
                            },
                            "viber": {
                                "originator": "13000",
                                "ttl": "120",
                                "content": {
                                    "text": "This is test message",
                                    "image-url": "test.domain/picture.jpg",
                                    "button-name": "button",
                                    "button-url": "test.domain"
                                }
                            },
                            "sms": {
                                "originator": "13000",
                                "translit": false,
                                "ttl": "120",
                                "content": {
                                    "text": "This is test message"
                                }
                            }
                        }
                    ]
                }
                """.formatted(testCase.getCaseBody().getRecipient(),
                testCase.getCaseBody().getMessageId());

        api
                .post("/broker-api/send", request)
                .code(200);

        //checker.checkCase(testCase);
    }

    @Order(2)
    @Execution(ExecutionMode.SAME_THREAD)
    @Story("Отправка единичного сообщения без шаблона")
    @DisplayName("1.1.2. Push->Viber->SMS. Проверка каскадирования")
    @Test
    @Tag("Iron1")
    @Description("1.1.2. Статус Ошибка (Failed или Rejected) для Push -> " +
                 "отправляем на Viber со статусом Read -> " +
                 "не отправляем на SMS")
    void ironOneTwo() {
        TestCase testCase=new TestCase(2, "1.1.2. Push->Viber->SMS. Проверка каскадирования",
                "1.1.2. Статус Ошибка (Failed или Rejected) для Push -> " +
                "отправляем на Viber со статусом Read -> " +
                "не отправляем на SMS",
                new CaseBody(gen.genNumber(), "79100000042",""),
                List.of(
                        new CaseCondition(200, "Application is not allowed",
                                Transport.PUSH, Status.REJECTED),
                        new CaseCondition(200, "", Transport.VIBER, Status.READ)
                ));

        testCaseList.add(testCase);

        String request = """
                {
                    "messages": [
                        {
                            "recipient": "%s",
                            "message-id": "%s",
                            "push": {
                                "applications": "SampleApp1",
                                "ttl": "10",
                                "content": {
                                    "title": "IronOne",
                                    "text": "This is test message"
                                }
                            },
                            "viber": {
                                "originator": "13000",
                                "ttl": "20",
                                "content": {
                                    "text": "This is test message",
                                    "image-url": "test.domain/picture.jpg",
                                    "button-name": "button",
                                    "button-url": "test.domain"
                                }
                            },
                            "sms": {
                                "originator": "13000",
                                "translit": false,
                                "ttl": "10",
                                "content": {
                                    "text": "This is test message"
                                }
                            }
                        }
                    ]
                }
                """.formatted(testCase.getCaseBody().getRecipient(),
                testCase.getCaseBody().getMessageId());

        api
                .post("/broker-api/send", request)
                .code(200);
    }

    @Order(3)
    @Test
    synchronized void waiting(){
        try{
            wait(40_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Order(4)
    @TestFactory
    @Story("Проверка статусов")
    Stream<DynamicTest> checkStatuses(){
        return testCaseList.stream()
                .map(testCase -> DynamicTest
                        .dynamicTest(testCase.getCaseId()+" - "+testCase.getName(),
                                ()->executeCheckStatus(testCase)));
    }

    void executeCheckStatus(TestCase testCase){
        checker.checkCase(testCase);
    }
}
