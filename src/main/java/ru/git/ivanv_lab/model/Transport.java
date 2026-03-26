package ru.git.ivanv_lab.model;

public enum Transport {
    SMS("sms", "СМС"),
    CALL("call", "Звонок"),
    EMAIL("email", "Эмейл"),
    PUSH("push", "Пуш"),
    VIBER("viber", "Вайбер"),
    MAIL_NOTIFY("mail-notify", "Мейл Нотифай"),
    WHATSAPP("whatsapp", "Ватсапп");

    private String name;
    private String rusName;

    Transport(String name, String rusName) {
        this.name = name;
        this.rusName = rusName;
    }

    public String getName(){
        return name;
    }

    public String getRusName(){
        return rusName;
    }
}
