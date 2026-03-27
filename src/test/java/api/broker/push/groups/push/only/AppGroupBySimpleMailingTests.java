package api.broker.push.groups.push.only;

import api.BaseBrokerTests;
import org.junit.jupiter.api.DynamicTest;
import ru.git.ivanv_lab.model.Transport;
import ru.git.ivanv_lab.processing.CaseBody;
import ru.git.ivanv_lab.processing.CaseCondition;
import ru.git.ivanv_lab.processing.TestCase;

import java.util.List;
import java.util.stream.Stream;

public class AppGroupBySimpleMailingTests extends BaseBrokerTests {

    Stream<DynamicTest> priorityTests(){
        List<TestCase> testCases=List.of(
                new TestCase(1,"Приоритет = '-6'","Некорректное значение параметра priority. Должны получить 400 ошибку при отправке запроса",
                        new CaseBody(dataGen.genNumber(),"79100000041","-6"),
                        List.of(
                                new CaseCondition(400,"Invalid priority", Transport.PUSH)
                        ))
        );

        return testCases
                .stream()
                .map(testCase -> DynamicTest.dynamicTest(
                        testCase.getCaseId()+" - "+testCase.getName(),
                        ()->executePriorityTests(testCase)
                ));
    }

    private void executePriorityTests(TestCase testCase){
        testCase.logTestCase();
    }
}
