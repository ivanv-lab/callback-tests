package ru.git.ivanv_lab.processing;

import ru.git.ivanv_lab.model.Status;
import ru.git.ivanv_lab.model.Transport;

public class CaseCondition {
    private int expectedHttpStatusCode;
    private Status conditionErrorDescription;
    private Transport conditionTransport;
    private Status conditionStatus;

    public CaseCondition(int expectedHttpStatusCode, Status conditionErrorDescription, Transport conditionTransport, Status conditionStatus) {
        this.expectedHttpStatusCode = expectedHttpStatusCode;
        this.conditionErrorDescription = conditionErrorDescription;
        this.conditionTransport = conditionTransport;
        this.conditionStatus = conditionStatus;
    }

    public int getExpectedHttpStatusCode() {
        return expectedHttpStatusCode;
    }

    public Status getConditionErrorDescription() {
        return conditionErrorDescription;
    }

    public Transport getConditionTransport() {
        return conditionTransport;
    }

    public Status getConditionStatus() {
        return conditionStatus;
    }
}
