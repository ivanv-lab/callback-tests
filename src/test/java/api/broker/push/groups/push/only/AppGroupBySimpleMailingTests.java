package api.broker.push.groups.push.only;

import api.BaseBrokerTests;
import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import ru.git.ivanv_lab.api.ApiWorker;
import ru.git.ivanv_lab.model.Transport;
import ru.git.ivanv_lab.processing.CaseBody;
import ru.git.ivanv_lab.processing.CaseCondition;
import ru.git.ivanv_lab.processing.TestCase;
import ru.git.ivanv_lab.processing.TestCaseChecker;

import java.util.List;
import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@Epic("broker-api")
@Feature("Группа Push-приложений." +
         "Push-приложения." +
         "Рассылка по ГП без шаблона")
public class AppGroupBySimpleMailingTests extends BaseBrokerTests {

    private final String pushGroupName = "PushAppGroup";
    private final ThreadLocal<TestCaseChecker> testCaseCheckerThreadLocal=ThreadLocal
            .withInitial(TestCaseChecker::new);
    private final ThreadLocal<ApiWorker> apiWorkerThreadLocal = ThreadLocal
            .withInitial(() -> new ApiWorker("broker", "PushClientAcc", "PushClientAcc"));

    @TestFactory
    @Story("Приоритет (priority)")
    @Order(1)
    Stream<DynamicTest> priorityTests() {
        List<TestCase> testCases = List.of(
                new TestCase(1, "Приоритет = '-6'", "Некорректное значение параметра priority. Должны получить 400 ошибку при отправке запроса",
                        new CaseBody(dataGen.genNumber(), "79100000041", "-6"),
                        List.of(
                                new CaseCondition(400, "Invalid priority", Transport.PUSH)
                        )),
                new TestCase(2, "Приоритет = '0'", "Некорректное значение параметра priority. Должны получить 400 ошибку при отправке запроса",
                        new CaseBody(dataGen.genNumber(), "79100000041", "0"),
                        List.of(
                                new CaseCondition(400, "Invalid priority", Transport.PUSH)
                        )),
                new TestCase(3, "Приоритет = '2'", "Корректное значение. Должны успешно отправить сообщение",
                        new CaseBody(dataGen.genNumber(), "79100000041", "2"),
                        List.of(
                                new CaseCondition(200, "", Transport.PUSH)
                        )),
                new TestCase(4, "Приоритет = '1000'", "Некорректное значение параметра priority. Должны получить 400 ошибку при отправке запроса",
                        new CaseBody(dataGen.genNumber(), "79100000041", "1000"),
                        List.of(
                                new CaseCondition(400, "Invalid priority", Transport.PUSH)
                        ))
        );

        return testCases
                .stream()
                .map(testCase -> DynamicTest.dynamicTest(
                        testCase.getCaseId() + " - " + testCase.getName(),
                        () -> executePriorityTests(testCase)
                ));
    }

    private void executePriorityTests(TestCase testCase) {
        Allure.attachment("TestCase", testCase.logTestCase());

        String request = String.format("""
                        {
                            "priority": "%s",
                            "messages": [
                                {
                                    "message-id": "%s",
                                    "recipient": "%s",
                                    "push": {
                                        "groups-applications": [
                                            "%s"
                                        ],
                                        "content": {
                                            "text": "text"
                                        }
                                    }
                                }
                            ]
                        }
                        """,
                testCase.getCaseBody().getValue(),
                testCase.getCaseBody().getMessageId(),
                testCase.getCaseBody().getRecipient(),
                pushGroupName);

        apiWorkerThreadLocal.get()
                .post("/broker-api/send", request)
                .code(testCase.getCaseConditions().get(0).getExpectedHttpStatusCode());

        testCaseCheckerThreadLocal.get()
                .checkCase(testCase);
    }
}
