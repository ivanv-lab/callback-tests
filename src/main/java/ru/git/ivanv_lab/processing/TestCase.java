package ru.git.ivanv_lab.processing;

import java.util.List;

public class TestCase {
    private long caseId;
    private String name;
    private String description;

    private CaseBody caseBody;
    private List<CaseCondition> caseConditions;

    public TestCase(long caseId, String name, String description, CaseBody caseBody, List<CaseCondition> caseConditions) {
        this.caseId = caseId;
        this.name = name;
        this.description = description;
        this.caseBody = caseBody;
        this.caseConditions = caseConditions;
    }

    public long getCaseId() {
        return caseId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String logTestCase() {
        StringBuilder log = new StringBuilder(String.format("""
                TestCase ID: %s
                Name: %s
                Description: %s
                
                Case:::
                Message ID: %s
                Recipient: %s
                Test value: %s
                
                Conditions:::
                """, caseId, name, description,

                caseBody.getMessageId(),
                caseBody.getRecipient(),
                caseBody.getValue()));

        for(int i=0;i<caseConditions.size();i++){
            CaseCondition condition=caseConditions.get(i);
            log.append(String.format("""
                    \nCondition %s:
                    Http status code: %s
                    Response error description: %s
                    Transport: %s
                    Status: %s
                    """,
                    i,
                    condition.getExpectedHttpStatusCode(),
                    condition.getConditionErrorDescription(),
                    condition.getConditionTransport(),
                    condition.getConditionStatus()));
        }

        return log.toString();
    }
}
