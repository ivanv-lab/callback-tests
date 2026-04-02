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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.git.ivanv_lab.BaseTest.sqlFabric;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Epic("broker-api")
@Feature("Smoke")
public class IronTests extends BaseBrokerTests {

    private final ApiWorker api = new ApiWorker("broker", "Iron", "IronAcc");
    private final TestCaseChecker checker = new TestCaseChecker();
    private static final List<TestCase> testCaseList = new ArrayList<>();

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

    @Order(1)
    @Execution(ExecutionMode.SAME_THREAD)
    @Story("Отправка единичного сообщения без шаблона")
    @DisplayName("1.1.2. Push->Viber->SMS. Проверка каскадирования")
    @Test
    @Tag("Iron1")
    @Description("1.1.2. Статус Ошибка (Failed или Rejected) для Push -> " +
                 "отправляем на Viber со статусом Read -> " +
                 "не отправляем на SMS")
    void ironOneTwo() {
        TestCase testCase = new TestCase(2, "1.1.2. Push->Viber->SMS. Проверка каскадирования",
                "1.1.2. Статус Ошибка (Failed или Rejected) для Push -> " +
                "отправляем на Viber со статусом Read -> " +
                "не отправляем на SMS",
                new CaseBody(gen.genNumber(), "79100000042", ""),
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

    @Order(1)
    @Execution(ExecutionMode.SAME_THREAD)
    @Story("Отправка единичного сообщения без шаблона")
    @DisplayName("1.1.3. Push->Viber->SMS. Проверка каскадирования")
    @Test
    @Tag("Iron1")
    @Description("1.1.3. Статус Ошибка (Failed или Rejected) для Push -> " +
                 "статус Ошибка (Failed или Rejected) для Viber -> " +
                 "отправляем на SMS со статусом Delivered")
    void ironOneThree() {
        TestCase testCase = new TestCase(3, "1.1.3. Push->Viber->SMS. Проверка каскадирования",
                "1.1.3. Статус Ошибка (Failed или Rejected) для Push -> " +
                "статус Ошибка (Failed или Rejected) для Viber -> " +
                "отправляем на SMS со статусом Delivered",
                new CaseBody(gen.genNumber(), "79100000021", ""),
                List.of(
                        new CaseCondition(200, "Application is not allowed",
                                Transport.PUSH, Status.REJECTED),
                        new CaseCondition(200, "Message is too long",
                                Transport.VIBER, Status.REJECTED),
                        new CaseCondition(200, "",
                                Transport.SMS, Status.DELIVERED)
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
                                "ttl": "10",
                                "content": {
                                    "text":"qweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqeqweqweqweqweqweqweqweqweqweqweqweqe"
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

    @Order(1)
    @Execution(ExecutionMode.SAME_THREAD)
    @Story("Отправка единичного сообщения без шаблона")
    @DisplayName("1.2.1. Viber->SMS. Проверка каскадирования")
    @Test
    @Tag("Iron1")
    @Description("1.2.1. Viber Доставлено(Delivered). " +
                 "Проверяем нахождение сообщения Viber в статистике. " +
                 "SMS в статистике быть не должно")
    void ironTwoOne() {
        TestCase testCase = new TestCase(4, "1.2.1. Viber->SMS. Проверка каскадирования",
                "1.2.1. Viber Доставлено(Delivered). " +
                "Проверяем нахождение сообщения Viber в статистике. " +
                "SMS в статистике быть не должно",
                new CaseBody(gen.genNumber(), "79100000022", ""),
                List.of(
                        new CaseCondition(200, "", Transport.VIBER, Status.DELIVERED)
                ));

        testCaseList.add(testCase);

        String request = """
                {
                    "messages": [
                        {
                            "recipient": "%s",
                            "message-id": "%s",
                            "viber": {
                                "originator": "13000",
                                "ttl": "20",
                                "content": {
                                    "text": "Test text",
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
                                    "text": "Test text"
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

    @Order(1)
    @Execution(ExecutionMode.SAME_THREAD)
    @Story("Отправка единичного сообщения без шаблона")
    @DisplayName("1.2.2. Viber->SMS. Проверка каскадирования")
    @Test
    @Tag("Iron1")
    @Description("1.2.2. Viber-Отправлено(Transmitted), " +
                 "SMS-Отправлено(Transmitted). " +
                 "В статистике должны быть оба сообщения. " +
                 "В данном сценарии надо проверить ttl " +
                 "(время через которое нужно переотправить сообщение по " +
                 "альтернативному каналу)")
    void ironTwoTwo() {
        TestCase testCase = new TestCase(5, "1.2.2. Viber->SMS. Проверка каскадирования",
                "1.2.2. Viber-Отправлено(Transmitted), " +
                "SMS-Отправлено(Transmitted). " +
                "В статистике должны быть оба сообщения. " +
                "В данном сценарии надо проверить ttl " +
                "(время через которое нужно переотправить сообщение по " +
                "альтернативному каналу)",
                new CaseBody(gen.genNumber(), "79100000001", ""),
                List.of(
                        new CaseCondition(200, "", Transport.VIBER, Status.TRANSMITTED),
                        new CaseCondition(200, "", Transport.SMS, Status.TRANSMITTED)
                ));

        testCaseList.add(testCase);

        String request = """
                {
                    "messages": [
                        {
                            "recipient": "%s",
                            "message-id": "%s",
                            "viber": {
                                "originator": "13000",
                                "ttl": "5",
                                "content": {
                                    "text": "Test text",
                                    "image-url": "test.domain/picture.jpg",
                                    "button-name": "button",
                                    "button-url": "test.domain"
                                }
                            },
                            "sms": {
                                "originator": "13000",
                                "translit": false,
                                "ttl": "5",
                                "content": {
                                    "text": "Test text"
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

    @Order(1)
    @Execution(ExecutionMode.SAME_THREAD)
    @Story("Отправка одиночного сообщения по каждому каналу")
    @DisplayName("2.1. SMS")
    @Test
    @Tag("Iron1")
    @Description("2.1. SMS. " +
                 "В этом сценарии обязательно дополнительно проверить " +
                 "попадание в статистику полей " +
                 "external-id1/external-id2/external-id3/external-id4/external-id5")
    void ironTwoDotOneDotOne() {
        TestCase testCase = new TestCase(6, "2.1. SMS",
                "2.1. SMS. " +
                "В этом сценарии обязательно дополнительно проверить " +
                "попадание в статистику полей " +
                "external-id1/external-id2/external-id3/external-id4/external-id5",
                new CaseBody(gen.genNumber(), "79100000023", ""),
                List.of(
                        new CaseCondition(200, "", Transport.SMS, Status.DELIVERED)
                ));

        testCaseList.add(testCase);

        String request = """
                {
                    "external-id1": "exid1",
                    "external-id2": "внешний идентификатор 2",
                    "external-id3": "3",
                    "external-id4": "4",
                    "external-id5": "5",
                    "messages": [
                        {
                            "recipient": "%s",
                            "message-id": "%s",
                            "sms": {
                                "originator": "13000",
                                "translit": false,
                                "content": {
                                    "text": "Test text"
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

    @Order(1)
    @Disabled("Требуется актуализация")
    @Execution(ExecutionMode.SAME_THREAD)
    @Story("Отправка одиночного сообщения по каждому каналу")
    @DisplayName("2.2.1. Call")
    @Test
    @Tag("Iron1")
    @Description("2.2.1. Call. " +
                 "В этом сценарии обязательно дополнительно проверить " +
                 "попадание в статистику полей " +
                 "external-id1/external-id2/external-id3/external-id4/external-id5\n\n")
    void ironTwoDotTwo() {
        TestCase testCase = new TestCase(6, "2.2.1. Call",
                "2.2.1. Call. " +
                "В этом сценарии обязательно дополнительно проверить " +
                "попадание в статистику полей " +
                "external-id1/external-id2/external-id3/external-id4/external-id5",
                new CaseBody(gen.genNumber(), "79100000024", ""),
                List.of(
                        new CaseCondition(200, "", Transport.CALL, Status.DELIVERED)
                ));

        testCaseList.add(testCase);

        String request = """
                {
                    "external-id1": "exid1",
                    "external-id2": "внешний идентификатор 2",
                    "external-id3": "3",
                    "external-id4": "4",
                    "external-id5": "5",
                    "priority": "2",
                    "messages": [
                        {
                            "recipient": "%s",
                            "message-id": "%s",
                            "call": {
                                "originator": "13000",
                                "content": {
                                    "file": "greet2.mp3"
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

    @Order(1)
    @Execution(ExecutionMode.SAME_THREAD)
    @Disabled("Требуется актуализация")
    @Story("Отправка одиночного сообщения по каждому каналу")
    @DisplayName("2.2.2. IVR")
    @Test
    @Tag("Iron1")
    @Description("2.2.2. IVR. " +
                 "В этом сценарии обязательно дополнительно проверить " +
                 "попадание в статистику полей " +
                 "external-id1/external-id2/external-id3/external-id4/external-id5\n\n")
    void ironTwoDotTwoDotTwo() {
        TestCase testCase = new TestCase(7, "2.2.2. IVR",
                "2.2.2. IVR. " +
                "В этом сценарии обязательно дополнительно проверить " +
                "попадание в статистику полей " +
                "external-id1/external-id2/external-id3/external-id4/external-id5",
                new CaseBody(gen.genNumber(), "79100000025", ""),
                List.of(
                        new CaseCondition(200, "", Transport.CALL, Status.DELIVERED)
                ));

        testCaseList.add(testCase);

        String request = """
                {
                    "external-id1": "exid1", "external-id2": "внешний идентификатор 2", "external-id3": "3", "external-id4": "4", "external-id5": "5",
                    "messages": [
                        {
                            "recipient":"%s",
                            "message-id":"%s",
                            "call": {
                                "originator": "13000",
                                "content": { "menu": "IVR" }
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

    @Order(1)
    @Disabled("Email-эмулятор в работе")
    @Execution(ExecutionMode.SAME_THREAD)
    @Story("Отправка одиночного сообщения по каждому каналу")
    @DisplayName("2.3. Email")
    @Test
    @Tag("Iron1")
    @Description("2.3. Email. " +
                 "В этом сценарии обязательно дополнительно проверить " +
                 "попадание в статистику полей " +
                 "external-id1/external-id2/external-id3/external-id4/external-id5\n\n")
    void ironTwoDotThree() {
        TestCase testCase = new TestCase(9, "2.3. Email",
                "2.3. Email. " +
                "В этом сценарии обязательно дополнительно проверить " +
                "попадание в статистику полей " +
                "external-id1/external-id2/external-id3/external-id4/external-id5",
                new CaseBody(gen.genNumber(), "test1@wsoft.ru", ""),
                List.of(
                        new CaseCondition(200, "", Transport.EMAIL, Status.TRANSMITTED)
                ));

        testCaseList.add(testCase);

        String request = """
                {
                    "external-id1": "exid1",
                    "external-id2": "внешний идентификатор 2",
                    "external-id3": "3",
                    "external-id4": "4",
                    "external-id5": "5",
                    "messages": [
                        {
                            "message-id": "%s",
                            "email-address": "%s",
                            "email": {
                                "originator": "iron@gmail.com",
                                "content": {
                                    "subscribe-rule": "all",
                                    "unsubscribe-rule": "all",
                                    "subject": "Hello, world",
                                    "text": "HTML-body",
                                    "attached-files": [
                                         { "name": "file.pdf", "file": "http://domain.com/file.pfd" }
                                    ]
                                }
                            }
                        }
                    ]
                }
                """.formatted(testCase.getCaseBody().getMessageId(),
                testCase.getCaseBody().getRecipient());

        api
                .post("/broker-api/send", request)
                .code(200);
    }

    @Order(1)
    @Execution(ExecutionMode.SAME_THREAD)
    @Story("Отправка одиночного сообщения по каждому каналу")
    @DisplayName("2.4. Push")
    @Test
    @Tag("Iron1")
    @Description("2.4. Push. " +
                 "В этом сценарии обязательно дополнительно проверить " +
                 "попадание в статистику полей " +
                 "external-id1/external-id2/external-id3/external-id4/external-id5\n\n")
    void ironTwoDotFour() {
        TestCase testCase = new TestCase(10, "2.4. Push",
                "2.4. Push. " +
                "В этом сценарии обязательно дополнительно проверить " +
                "попадание в статистику полей " +
                "external-id1/external-id2/external-id3/external-id4/external-id5",
                new CaseBody(gen.genNumber(), "79100000046", ""),
                List.of(
                        new CaseCondition(200, "", Transport.PUSH, Status.READ)
                ));

        testCaseList.add(testCase);

        String request = """
                {
                    "external-id1": "exid1",
                    "external-id2": "внешний идентификатор 2",
                    "external-id3": "3",
                    "external-id4": "4",
                    "external-id5": "5",
                    "priority": "2",
                    "messages": [
                        {
                            "message-id": "%s",
                            "recipient": "%s",
                            "push": {
                                "applications": "IronApp",
                                "content": {
                                    "text": "text message 1",
                                    "title": "title message 1",
                                    "extra-content": "extra-content message 1"
                                }
                            }
                        }
                    ]
                }
                """.formatted(testCase.getCaseBody().getMessageId(),
                testCase.getCaseBody().getRecipient());

        api
                .post("/broker-api/send", request)
                .code(200);
    }

    @Order(1)
    @Execution(ExecutionMode.SAME_THREAD)
    @Story("Отправка одиночного сообщения по каждому каналу")
    @DisplayName("2.4.1. Push с шаблоном")
    @Test
    @Tag("Iron1")
    @Description("2.4.1 Push. Отправка по обычному шаблону. " +
                 "Должны получить статусы по одному приложению - IronApp")
    void ironTwoDotFourDotOne() {
        TestCase testCase = new TestCase(11, "2.4.1. Push с шаблоном",
                "2.4.1 Push. Отправка по обычному шаблону. " +
                "Должны получить статусы по одному приложению - IronApp",
                new CaseBody(gen.genNumber(), "79100000043", String.valueOf(sqlFabric.getTemplateId("IronPushTemplate"))),
                List.of(
                        new CaseCondition(200, "", Transport.PUSH, Status.READ, new String[]{"IronApp"})
                ));

        String request = String.format("""
                        {
                            "messages": [
                                {
                                    "template-id": "%s",
                                    "recipient": "%s",
                                    "message-id": "%s"
                                }
                            ]
                        }
                        """, testCase.getCaseBody().getValue(),
                testCase.getCaseBody().getRecipient(),
                testCase.getCaseBody().getMessageId());

        api
                .post("/broker-api/send", request)
                .code(200);
    }

    @Order(1)
    @Execution(ExecutionMode.SAME_THREAD)
    @Story("Отправка одиночного сообщения по каждому каналу")
    @DisplayName("2.4.2. Push с шаблоном")
    @Test
    @Tag("Iron1")
    @Description("2.4.2 Push. Отправка по шаблону с группой приложений. " +
                 "При отправке по данной ГП, должны получать статусы по " +
                 "обоим приложениям - IronApp и WebApp")
    void ironTwoDotFourDotTwo() throws SQLException {
        TestCase testCase = new TestCase(14, "2.4.2. Push с шаблоном",
                "2.4.2 Push. Отправка по шаблону с группой приложений. " +
                "При отправке по данной ГП, должны получать статусы по " +
                "обоим приложениям - IronApp и WebApp",
                new CaseBody(gen.genNumber(), "79100000043", String.valueOf(sqlFabric.getTemplateId("IronPushTemplateGroup"))),
                List.of(
                        new CaseCondition(200, "", Transport.PUSH, Status.READ, new String[]{"IronApp", "WebApp"})
                ));

        testCaseList.add(testCase);

        String request = String.format("""
                        {
                            "messages": [
                                {
                                    "template-id": "%s",
                                    "recipient": "%s",
                                    "message-id": "%s"
                                }
                            ]
                        }
                        """, testCase.getCaseBody().getValue(),
                testCase.getCaseBody().getRecipient(),
                testCase.getCaseBody().getMessageId());

        api
                .post("/broker-api/send", request)
                .code(200);
    }

    @Order(1)
    @Execution(ExecutionMode.SAME_THREAD)
    @Story("Отправка одиночного сообщения по каждому каналу")
    @DisplayName("2.5. Viber")
    @Test
    @Tag("Iron1")
    @Description("2.5. Viber. " +
                 "В этом сценарии обязательно дополнительно проверить " +
                 "попадание в статистику полей " +
                 "external-id1/external-id2/external-id3/external-id4/external-id5")
    void ironTwoDotFive() {
        TestCase testCase = new TestCase(15, "2.5. Viber",
                "2.5. Viber. " +
                "В этом сценарии обязательно дополнительно проверить " +
                "попадание в статистику полей " +
                "external-id1/external-id2/external-id3/external-id4/external-id5",
                new CaseBody(gen.genNumber(), "79100000046", ""),
                List.of(
                        new CaseCondition(200, "", Transport.VIBER, Status.READ)
                ));

        testCaseList.add(testCase);

        String request = """
                {
                    "external-id1": "exid1",
                    "external-id2": "внешний идентификатор 2",
                    "external-id3": "3",
                    "external-id4": "4",
                    "external-id5": "5",
                    "messages": [
                        {
                            "recipient": "%s",
                            "message-id": "%s",
                            "viber": {
                                "originator": "13000",
                                "content": {
                                    "text": "Broker",
                                    "button-url": "urlurl",
                                    "button-name": "name",
                                    "image-url": "http://xcook.info/sites/default/files/products/11/lobster-5.jpg"
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

    @Order(1)
    @Execution(ExecutionMode.SAME_THREAD)
    @Story("Отправка одиночного сообщения по каждому каналу")
    @DisplayName("2.6. WhatsApp")
    @Test
    @Tag("Iron1")
    @Description("2.6. WhatsApp. " +
                 "В этом сценарии обязательно дополнительно проверить " +
                 "попадание в статистику полей " +
                 "external-id1/external-id2/external-id3/external-id4/external-id5")
    void ironTwoDotSix() {
        TestCase testCase = new TestCase(16, "2.6. WhatsApp",
                "2.6. WhatsApp. " +
                "В этом сценарии обязательно дополнительно проверить " +
                "попадание в статистику полей " +
                "external-id1/external-id2/external-id3/external-id4/external-id5",
                new CaseBody(gen.genNumber(), "79100000047", ""),
                List.of(
                        new CaseCondition(200, "", Transport.WHATSAPP, Status.READ)
                ));

        testCaseList.add(testCase);

        String request = """
                {
                    "external-id1": "exid1",
                    "external-id2": "внешний идентификатор 2",
                    "external-id3": "3",
                    "external-id4": "4",
                    "external-id5": "5",
                    "messages": [
                        {
                            "recipient": "%s",
                            "message-id": "%s",
                            "whatsapp": {
                                "originator": "13000",
                                "ttl": "10",
                                "content": {
                                    "text": "Test text"
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

    @Order(1)
    @Execution(ExecutionMode.SAME_THREAD)
    @Story("Отправка одиночного сообщения по каждому каналу")
    @DisplayName("2.7.1. Mail Notify")
    @Test
    @Tag("Iron1")
    @Description("2.7.1. Mail Notify." +
                 "В этом сценарии обязательно дополнительно проверить " +
                 "попадание в статистику полей " +
                 "external-id1/external-id2/external-id3/external-id4/external-id5")
    void ironTwoDotSeven() {
        TestCase testCase = new TestCase(18, "2.7.1. Mail Notify",
                "2.7.1. Mail Notify." +
                "В этом сценарии обязательно дополнительно проверить " +
                "попадание в статистику полей " +
                "external-id1/external-id2/external-id3/external-id4/external-id5",
                new CaseBody(gen.genNumber(), "79100000048", ""),
                List.of(
                        new CaseCondition(200, "", Transport.MAIL_NOTIFY, Status.READ)
                ));

        testCaseList.add(testCase);

        String request = """
                {
                    "external-id1": "exid1",
                    "external-id2": "внешний идентификатор 2",
                    "external-id3": "3",
                    "external-id4": "4",
                    "external-id5": "5",
                    "messages": [
                        {
                            "recipient": "%s",
                            "message-id": "%s",
                            "mail-notify": {
                                "ttl": "10",
                                "content": {
                                    "routes": "vk,ok",
                                    "mn-service": "IronServiceMN",
                                    "vk-message": "Текст сообщения для VK",
                                    "ok-message": "Текст сообщения для OK"
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

    @Order(1)
    @Execution(ExecutionMode.SAME_THREAD)
    @Story("Отправка одиночного сообщения по каждому каналу")
    @DisplayName("2.7.2. Mail Notify: внешний шаблон")
    @Test
    @Tag("Iron1")
    @Description("2.7.2. Mail Notify внешний шаблон.")
    void ironTwoDotSevenDotTwo() {
        TestCase testCase = new TestCase(19, "2.7.2. Mail Notify: внешний шаблон",
                "",
                new CaseBody(gen.genNumber(), "79100000049", ""),
                List.of(
                        new CaseCondition(200, "", Transport.MAIL_NOTIFY, Status.READ)
                ));

        testCaseList.add(testCase);

        String request = """
                {
                    "messages": [
                        {
                            "recipient": "%s",
                            "message-id": "%s",
                            "variables": {
                                "field1": "${__time()}",
                                "field2": "целых 0"
                            },
                            "mail-notify": {
                                "ttl": "10",
                                "content": {
                                    "mn-service": "IronServiceMN",
                                    "mn-template": "MNIronOperatorTemplate"
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

    @Order(1)
    @Execution(ExecutionMode.SAME_THREAD)
    @Story("Отправка одиночного сообщения по каждому каналу")
    @DisplayName("2.8. Web-Push")
    @Test
    @Tag("Iron1")
    @Description("2.8. Web-Push. " +
                 "Проверка сценария Transmitted->Read")
    void ironTwoDotEight() {
        TestCase testCase = new TestCase(20, "2.8. Web-Push",
                "2.8. Web-Push. " +
                "Проверка сценария Transmitted->Read",
                new CaseBody(gen.genNumber(), "79100000041", ""),
                List.of(
                        new CaseCondition(200, "", Transport.PUSH, Status.READ)
                ));

        testCaseList.add(testCase);

        String request = """
                {
                    "priority": "2",
                    "messages": [
                        {
                            "message-id": "%s",
                            "recipient": "%s",
                            "push": {
                                "applications": [
                                    "WebApp"
                                ],
                                "content": {
                                    "text": "message 1",
                                    "title": "message 1",
                                    "extra-content": "message 1"
                                }
                            }
                        }
                    ]
                }
                """.formatted(testCase.getCaseBody().getMessageId(),
                testCase.getCaseBody().getRecipient());

        api
                .post("/broker-api/send", request)
                .code(200);
    }

    @Order(1)
    @Execution(ExecutionMode.SAME_THREAD)
    @Story("Отправка единичного сообщения по шаблону")
    @DisplayName("3. Кастомные переменные")
    @Test
    @Tag("Iron1")
    @Description("3. Для теста подготовить несколько шаблонов по разным каналам, " +
                 "которые содержат переменные, которые не предлагает админка или ЛК. " +
                 "Эти переменные и параметры для их заполнения передаются в запросе.")
    void ironThree() throws SQLException {
//        String id = gen.genNumber(true);
//        ResultSet resultSet = msg.query("SELECT * FROM BULK_DISTR_TEMPLATES \n" +
//                                        "WHERE BULK_DISTR_TEMPLATES.NAME = 'IronTemplate'\n" +
//                                        "AND BULK_DISTR_TEMPLATES.IS_VISIBLE=1");
//        String recipient = "79100000002";
//
//        Map<String, String> statusMap = new HashMap<>();
//        statusMap.put("testName", "ironThree");
//        statusMap.put("id", id);
//        statusMap.put("recipient", recipient);
//        statusMap.put("statuses", "SMS-Transmitted,WhatsApp-Transmitted");
//        statusMap.put("var1", "value");
//        statusMap.put("var2", "значение");
//        messageList.add(statusMap);

        TestCase testCaseSms = new TestCase(21, "3. Кастомные переменные",
                "3. Для теста подготовить несколько шаблонов по разным каналам, " +
                "которые содержат переменные, которые не предлагает админка или ЛК. " +
                "Эти переменные и параметры для их заполнения передаются в запросе.",
                new CaseBody(gen.genNumber(), "79100000002", String.valueOf(sqlFabric.getTemplateId("IronTemplate", Transport.SMS))),
                List.of(
                        new CaseCondition(200, "", Transport.SMS, Status.TRANSMITTED)
                ));

        TestCase testCaseWA = new TestCase(22, "3. Кастомные переменные",
                "3. Для теста подготовить несколько шаблонов по разным каналам, " +
                "которые содержат переменные, которые не предлагает админка или ЛК. " +
                "Эти переменные и параметры для их заполнения передаются в запросе.",
                new CaseBody(gen.genNumber(), "79100000002", String.valueOf(sqlFabric.getTemplateId("IronTemplate", Transport.WHATSAPP))),
                List.of(
                        new CaseCondition(200, "", Transport.WHATSAPP, Status.TRANSMITTED)
                ));

        testCaseList.add(testCaseSms);
        testCaseList.add(testCaseWA);

        String request = """
                {
                    "template-id": "%s",
                    "messages": [
                        {
                            "recipient": "%s",
                            "message-id": "%s",
                            "variables": {
                                "var1": "value",
                                "var2": "значение"
                            }
                        }
                    ]
                }
                """.formatted(testCaseSms.getCaseBody().getValue(),
                testCaseSms.getCaseBody().getRecipient(),
                testCaseSms.getCaseBody().getMessageId());

        api
                .post("/broker-api/send", request)
                .code(200);

        request = """
                {
                    "template-id": "%s",
                    "messages": [
                        {
                            "recipient": "%s",
                            "message-id": "%s",
                            "variables": {
                                "var1": "value",
                                "var2": "значение"
                            }
                        }
                    ]
                }
                """.formatted(testCaseWA.getCaseBody().getValue(),
                testCaseWA.getCaseBody().getRecipient(),
                testCaseWA.getCaseBody().getMessageId());

        api
                .post("/broker-api/send", request)
                .code(200);
    }

    @Order(1)
    @Execution(ExecutionMode.SAME_THREAD)
    @Story("Отправка нескольких сообщений без шаблона")
    @DisplayName("4. Проверка различных статусов")
    @Test
    @Tag("Iron1")
    @Description("4. Отправка нескольких сообщений без шаблона. " +
                 "В данном примере в блоке messages сделать несколько получателей (по настройкам эмулятора):\n" +
                 "1)) Получение статуса Доставлено(Delivered) или Прочитано(Read) для Viber (проверяем, что Viber попал в статистику, а SMS – нет)\n" +
                 "2)) Получение только статуса Отправлено(Transmitted) для Viber (проверяем, что SMS тоже попробовали отправить (по статистике)).\n" +
                 "3)) Viber Сообщение доставлено, прочитано через 2 минуты\n" +
                 "4)) Сообщение Viber не доставлено\n" +
                 "5)) Ошибка (от smstraffic), у нас – Ошибка"
    )
    void ironFour() {
        TestCase testCase1 = new TestCase(22, "4. Проверка различных статусов",
                "4. Отправка нескольких сообщений без шаблона. " +
                "В данном примере в блоке messages сделать несколько получателей (по настройкам эмулятора):\n" +
                "1)) Получение статуса Доставлено(Delivered) или Прочитано(Read) для Viber (проверяем, что Viber попал в статистику, а SMS – нет)\n" +
                "2)) Получение только статуса Отправлено(Transmitted) для Viber (проверяем, что SMS тоже попробовали отправить (по статистике)).\n" +
                "3)) Viber Сообщение доставлено, прочитано через 2 минуты\n" +
                "4)) Сообщение Viber не доставлено\n" +
                "5)) Ошибка (от smstraffic), у нас – Ошибка",
                new CaseBody(gen.genNumber(), "79100000043", ""),
                List.of(
                        new CaseCondition(200, "", Transport.VIBER, Status.READ)
                ));

        TestCase testCase2 = new TestCase(23, "4. Проверка различных статусов",
                "4. Отправка нескольких сообщений без шаблона. " +
                "В данном примере в блоке messages сделать несколько получателей (по настройкам эмулятора):\n" +
                "1)) Получение статуса Доставлено(Delivered) или Прочитано(Read) для Viber (проверяем, что Viber попал в статистику, а SMS – нет)\n" +
                "2)) Получение только статуса Отправлено(Transmitted) для Viber (проверяем, что SMS тоже попробовали отправить (по статистике)).\n" +
                "3)) Viber Сообщение доставлено, прочитано через 2 минуты\n" +
                "4)) Сообщение Viber не доставлено\n" +
                "5)) Ошибка (от smstraffic), у нас – Ошибка",
                new CaseBody(gen.genNumber(), "79100000003", ""),
                List.of(
                        new CaseCondition(200, "", Transport.VIBER, Status.TRANSMITTED),
                        new CaseCondition(200, "", Transport.SMS, Status.TRANSMITTED)
                ));

        TestCase testCase3 = new TestCase(22, "4. Проверка различных статусов",
                "4. Отправка нескольких сообщений без шаблона. " +
                "В данном примере в блоке messages сделать несколько получателей (по настройкам эмулятора):\n" +
                "1)) Получение статуса Доставлено(Delivered) или Прочитано(Read) для Viber (проверяем, что Viber попал в статистику, а SMS – нет)\n" +
                "2)) Получение только статуса Отправлено(Transmitted) для Viber (проверяем, что SMS тоже попробовали отправить (по статистике)).\n" +
                "3)) Viber Сообщение доставлено, прочитано через 2 минуты\n" +
                "4)) Сообщение Viber не доставлено\n" +
                "5)) Ошибка (от smstraffic), у нас – Ошибка",
                new CaseBody(gen.genNumber(), "79100000042", ""),
                List.of(
                        new CaseCondition(200, "", Transport.VIBER, Status.READ)
                ));

        TestCase testCase4 = new TestCase(22, "4. Проверка различных статусов",
                "4. Отправка нескольких сообщений без шаблона. " +
                "В данном примере в блоке messages сделать несколько получателей (по настройкам эмулятора):\n" +
                "1)) Получение статуса Доставлено(Delivered) или Прочитано(Read) для Viber (проверяем, что Viber попал в статистику, а SMS – нет)\n" +
                "2)) Получение только статуса Отправлено(Transmitted) для Viber (проверяем, что SMS тоже попробовали отправить (по статистике)).\n" +
                "3)) Viber Сообщение доставлено, прочитано через 2 минуты\n" +
                "4)) Сообщение Viber не доставлено\n" +
                "5)) Ошибка (от smstraffic), у нас – Ошибка",
                new CaseBody(gen.genNumber(), "79100000101", ""),
                List.of(
                        new CaseCondition(200, "", Transport.VIBER, Status.NOT_DELIVERED)
                ));

        TestCase testCase5 = new TestCase(22, "4. Проверка различных статусов",
                "4. Отправка нескольких сообщений без шаблона. " +
                "В данном примере в блоке messages сделать несколько получателей (по настройкам эмулятора):\n" +
                "1)) Получение статуса Доставлено(Delivered) или Прочитано(Read) для Viber (проверяем, что Viber попал в статистику, а SMS – нет)\n" +
                "2)) Получение только статуса Отправлено(Transmitted) для Viber (проверяем, что SMS тоже попробовали отправить (по статистике)).\n" +
                "3)) Viber Сообщение доставлено, прочитано через 2 минуты\n" +
                "4)) Сообщение Viber не доставлено\n" +
                "5)) Ошибка (от smstraffic), у нас – Ошибка",
                new CaseBody(gen.genNumber(), "79100000140", ""),
                List.of(
                        new CaseCondition(200, "", Transport.VIBER, Status.FAILED)
                ));

        testCaseList.addAll(List.of(testCase1, testCase2, testCase3, testCase4, testCase5));

        String request = """
                {
                    "priority": "4",
                    "timing": {
                        "start-datetime": "%s",
                        "end-datetime": "%s",
                        "allowed-starttime": "10:00",
                        "allowed-endtime": "19:00"
                    },
                    "viber": {
                        "originator": "13000",
                        "ttl": "10",
                        "content": {
                            "text": "Test text",
                            "image-url": "test.domain/picture.jpg",
                            "button-name": "button",
                            "button-url": "test.domain"
                        }
                    },
                    "sms": {
                        "originator": "13000",
                        "ttl": "10",
                        "translit":false,
                        "content": {
                            "text": "Test text"
                        }
                    },
                    "messages": [
                        { "recipient": "%s", "message-id": "%s" },
                        { "recipient": "%s", "message-id": "%s" },
                        { "recipient": "%s", "message-id": "%s" },
                        { "recipient": "%s", "message-id": "%s" },
                        { "recipient": "%s", "message-id": "%s" }
                    ]
                }
                """.formatted(now, nextWeek,
                recipient1, id1,
                recipient2, id2,
                recipient3, id3,
                recipient4, id4,
                recipient6, id6);

        api
                .post("/broker-api/send", request)
                .code(200);
    }

    @Order(1)
    @Execution(ExecutionMode.SAME_THREAD)
    @Story("Отправка массовой рассылки по шаблону")
    @DisplayName("5. Переменные")
    @Test
    @Tag("Iron1")
    @Description("5. Отправка массовой рассылки по шаблону с переменными. " +
                 "Аналогично пункту 3, только сообщений несколько " +
                 "(можно использовать те-же шаблоны и переменные, " +
                 "только передавать разные значения для них)"
    )
    void ironFive() throws SQLException {
        ResultSet resultSet = msg.query("SELECT ID FROM BULK_DISTR_TEMPLATES WHERE NAME='IronTemplate' AND BULK_DISTR_TEMPLATES.IS_VISIBLE=1");
        assertTrue(resultSet.next());
        String templateId = resultSet.getString(1);
        resultSet = msg.query("SELECT NAME FROM transports WHERE id=(select transport_id from bulk_distr_templates where id='" + templateId + "')");
        assertTrue(resultSet.next());
        String transportName = resultSet.getString(1);
        String recipient1 = "79100000004",
                recipient2 = "79100000005";
        String id1 = gen.genNumber(true);
        String id2 = gen.genNumber(true);

        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("testName", "ironFive");
        statusMap.put("id", id1);
        statusMap.put("recipient", recipient1);
        statusMap.put("statuses", transportName + "-Transmitted");
        statusMap.put("var1", "PETR");
        statusMap.put("var2", "KUZIN");
        messageList.add(statusMap);

        Map<String, String> statusMap2 = new HashMap<>();
        statusMap2.put("testName", "ironFive");
        statusMap2.put("id", id2);
        statusMap2.put("recipient", recipient2);
        statusMap2.put("statuses", transportName + "-Transmitted");
        statusMap2.put("var1", "VLADIMIR");
        statusMap2.put("var2", "NESTEROV");
        messageList.add(statusMap2);

        String request = """
                {
                    "template-id": "%s",
                    "messages": [
                        {
                            "recipient": "%s",
                            "message-id": "%s",
                            "variables": {
                                "var1": "PETR",
                                "var2": "KUZIN"
                            }
                        },
                        {
                            "recipient": "%s",
                            "message-id": "%s",
                            "variables": {
                                "var1": "VLADIMIR",
                                "var2": "NESTEROV"
                            }
                        }
                    ]
                }
                """.formatted(templateId, recipient1, id1, recipient2, id2);

        api
                .post("/broker-api/send", request)
                .code(200);
        resultSet.close();
    }

    @Order(1)
    @Execution(ExecutionMode.SAME_THREAD)
    @Story("Отложенная отправка")
    @DisplayName("6.1. По дате")
    @Test
    @Tag("Iron1")
    @Description("6.1. Отложенная отправка по дате. " +
                 "Делаем отложенную отправку на 5 минут от текущего времени. " +
                 "Сначала проверить формирование статуса Deferred (в статистике). " +
                 "Затем его обновление после отправки "
    )
    synchronized void ironSix() throws InterruptedException {
        String recipient = "79100000006";
        String id = gen.genNumber(true);

        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("testName", "ironSix");
        statusMap.put("id", id);
        statusMap.put("recipient", recipient);
        statusMap.put("statuses", "SMS-Transmitted");
        messageList.add(statusMap);

        String request = """
                {
                    "timing": {
                        "localtime": "1",
                        "start-datetime": "%s",
                        "end-datetime": "%s",
                        "allowed-starttime": "",
                        "allowed-endtime": ""
                    },
                    "messages": [
                        {
                            "recipient": "%s",
                            "message-id": "%s",
                            "sms": {
                                "originator": "13000",
                                "translit": false,
                                "content": {
                                    "text": "Broker SMS with Deffered Date"
                                }
                            }
                        }
                    ]
                }
                """.formatted(nowLDT.plusMinutes(5).format(formatter), nextWeek.formatted(hoursFormatter), recipient, id);

        api
                .post("/broker-api/send", request)
                .code(200);

        wait(10_000);

        assertTrue(CallbackServer.getCallBack(new CallbackKey(id, "SMS", "Deferred")).path("messages").path(0)
                .path("status").asText().equals("Deferred"));
    }

    @Order(1)
    @Execution(ExecutionMode.SAME_THREAD)
    @Story("Отложенная отправка")
    @DisplayName("6.2. По времени")
    @Test
    @Tag("Iron1")
    @Description("6.2. Отложенная отправка по времени. " +
                 "Делаем отложенную отправку на 5 минут от текущего времени. Сначала проверить формирование статуса Deferred (в статистике). Затем его обновление после отправки "
    )
    synchronized void ironSixDotTwo() throws InterruptedException {
        String recipient = "79100000030";
        String id = gen.genNumber(true);

        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("testName", "ironSixDotTwo");
        statusMap.put("id", id);
        statusMap.put("recipient", recipient);
        statusMap.put("statuses", "Viber-Delivered");
        messageList.add(statusMap);

        String request = """
                {
                    "timing": {
                        "localtime": "1",
                        "start-datetime": "",
                        "end-datetime": "",
                        "allowed-starttime": "%s"
                    },
                    "messages": [
                        {
                            "recipient": "%s",
                            "message-id": "%s",
                            "viber": {
                                "originator": "13000",
                                "content": {
                                    "text": "Broker",
                                    "button-url": "urlurl",
                                    "button-name": "name",
                                    "image-url": "http://xcook.info/sites/default/files/products/11/lobster-5.jpg"
                                }
                            }
                        }
                    ]
                }
                """.formatted(nowLDT.plusMinutes(5).format(hoursFormatter), recipient, id);

        api
                .post("/broker-api/send", request)
                .code(200);

        wait(10_000);

        assertTrue(CallbackServer.getCallBack(new CallbackKey(id, "Viber", "Deferred")).path("messages").path(0)
                .path("status").asText().equalsIgnoreCase("Deferred"));
    }

    @Order(1)
    @Execution(ExecutionMode.SAME_THREAD)
    @Story("Отложенная отправка")
    @DisplayName("6.3. По дню недели")
    @Test
    @Tag("Iron1")
    @Description("6.3. Отложенная отправка по дню недели. " +
                 "В этом сценарии указываем все дни недели, кроме текущего. " +
                 "Проверяем, что сформировался статус Deferred (в статистике)."
    )
    void ironSixDotThree() {
        String recipient = "999613";
        // Генерация уникального ID
        String id = gen.genNumber(true);

        // Определяем текущий день недели
        DayOfWeek currentDay = LocalDate.now().getDayOfWeek();

        // Формируем строку с днями недели, исключая текущий день
        StringBuilder allowedDays = new StringBuilder();
        for (DayOfWeek day : DayOfWeek.values()) {
            if (day != currentDay) {
                if (!allowedDays.isEmpty()) {
                    allowedDays.append(",");
                }
                allowedDays.append(day.toString().substring(0, 3).toUpperCase()); // Преобразуем в формат "MON", "TUE" и т.д.
            }
        }

        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("testName", "ironSixDotThree");
        statusMap.put("id", id);
        statusMap.put("recipient", recipient);
        statusMap.put("statuses", "Mail Notify-Deferred");
        messageList.add(statusMap);

        // Формируем JSON-запрос
        String request = """
                {
                    "timing": {
                        "localtime": "1",
                        "start-datetime": "",
                        "end-datetime": "",
                        "allowed-starttime": "",
                        "allowed-endtime": "",
                        "allowed-days": "%s"
                    },
                    "messages": [
                        {
                            "recipient": "%s",
                            "message-id": "%s",
                            "mail-notify": {
                                "ttl": "10",
                                "content": {
                                    "routes": "vk",
                                    "mn-service": "IronServiceMN",
                                    "vk-message": "Отложенная отправка c днём недели"
                                }
                            }
                        }
                    ]
                }""".formatted(allowedDays.toString(), recipient, id);

        api
                .post("/broker-api/send", request)
                .code(200);
    }

    @Order(2)
    @Test
    synchronized void waiting() {
        try {
            wait(40_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Order(3)
    @TestFactory
    @DisplayName("Проверка статусов")
    @Story("Проверка статусов")
    Stream<DynamicTest> checkStatuses() {
        return testCaseList.stream()
                .map(testCase -> DynamicTest
                        .dynamicTest(testCase.getCaseId() + " - " + testCase.getName(),
                                () -> executeCheckStatus(testCase)));
    }

    void executeCheckStatus(TestCase testCase) {
        checker.checkCase(testCase);
    }
}
