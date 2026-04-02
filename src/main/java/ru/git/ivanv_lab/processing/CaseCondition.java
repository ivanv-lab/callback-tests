package ru.git.ivanv_lab.processing;

import ru.git.ivanv_lab.model.general.Status;
import ru.git.ivanv_lab.model.general.Transport;

public class CaseCondition {
    private int expectedHttpStatusCode;
    private String conditionErrorDescription;
    private Transport conditionTransport;
    private Status conditionStatus;
    private String[] pushAppName;

    public CaseCondition(int expectedHttpStatusCode, String conditionErrorDescription, Transport conditionTransport, Status conditionStatus,
                         String[] pushAppName) {
        this.expectedHttpStatusCode = expectedHttpStatusCode;
        this.conditionErrorDescription = conditionErrorDescription;
        this.conditionTransport = conditionTransport;
        this.conditionStatus = conditionStatus;
        this.pushAppName=pushAppName;
    }

    public CaseCondition(int expectedHttpStatusCode, String conditionErrorDescription, Transport conditionTransport, Status conditionStatus) {
        this.expectedHttpStatusCode = expectedHttpStatusCode;
        this.conditionErrorDescription = conditionErrorDescription;
        this.conditionTransport = conditionTransport;
        this.conditionStatus = conditionStatus;
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

    public String[] getPushAppName(){
        return pushAppName;
    }
}
