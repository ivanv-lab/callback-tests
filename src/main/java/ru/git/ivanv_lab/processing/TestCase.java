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
        this.name=name;
        this.description = description;
        this.caseBody = caseBody;
        this.caseConditions = caseConditions;
    }
}
