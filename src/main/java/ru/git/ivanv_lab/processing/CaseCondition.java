package ru.git.ivanv_lab.processing;

import ru.git.ivanv_lab.model.Status;
import ru.git.ivanv_lab.model.Transport;

public class CaseCondition {
    private int expectedHttpStatusCode;
    private String conditionErrorDescription;
    private Transport conditionTransport;
    private Status conditionStatus;

    public CaseCondition(int expectedHttpStatusCode, String conditionErrorDescription, Transport conditionTransport, Status conditionStatus) {
        this.expectedHttpStatusCode = expectedHttpStatusCode;
        this.conditionErrorDescription = conditionErrorDescription;
        this.conditionTransport = conditionTransport;
        this.conditionStatus = conditionStatus;
    }

    public CaseCondition(int expectedHttpStatusCode, String conditionErrorDescription, Transport conditionTransport) {
        this.expectedHttpStatusCode = expectedHttpStatusCode;
        this.conditionErrorDescription = conditionErrorDescription;
        this.conditionTransport = conditionTransport;
    }

    public int getExpectedHttpStatusCode() {
        return expectedHttpStatusCode;
    }

    public String getConditionErrorDescription() {
        return conditionErrorDescription;
    }

    public Transport getConditionTransport() {
        return conditionTransport;
    }

    public Status getConditionStatus() {
        return conditionStatus;
    }
}
