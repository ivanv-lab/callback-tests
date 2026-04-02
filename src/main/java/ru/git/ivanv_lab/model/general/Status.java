package ru.git.ivanv_lab.model.general;

import java.util.Arrays;

public enum Status {
    BUFFERED("buffered","Отложено(звонок)"),
    DEFERRED("deferred","Отложено"),
    ERROR("error","Ошибка(звонок)"),
    REJECTED("rejected","Отклонено"),
    FINISHED("finished","Завершено(звонок)"),
    NOT_ANSWERED("not answered","Не отвечено(звонок)"),
    FAILED("failed","Ошибка"),
    TRANSMITTED("transmitted","Отправлено"),
    DELIVERED("delivered","Доставлено"),
    NOT_DELIVERED("not delivered","Недоставлено"),
    READ("read","Прочитано"),
    EXPIRED("expired","Просрочено");

    private String name;
    private String rusName;

    Status(String name, String rusName){
        this.name=name;
        this.rusName=rusName;
    }

    public static Status getFromName(String statusName){
        return Arrays.stream(Status.values())
                .filter(status -> status.name.equals(statusName))
                .findFirst()
                .get();
    }

    public String getName() {
        return name;
    }

    public String getRusName() {
        return rusName;
    }
}
