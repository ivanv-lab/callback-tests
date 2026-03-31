package ru.git.ivanv_lab.model;

public enum Transport {
    SMS("sms", "SMS","СМС"),
    CALL("call", "Call","Звонок"),
    EMAIL("email", "Email","Эмейл"),
    PUSH("push", "Push","Пуш"),
    VIBER("viber", "Viber","Вайбер"),
    MAIL_NOTIFY("mail-notify", "Mail Notify","Мейл Нотифай"),
    WHATSAPP("whatsapp", "WhatsApp","Ватсапп");

    private String name;
    private String dbName;
    private String rusName;

    Transport(String name, String dbName, String rusName) {
        this.name = name;
        this.dbName=dbName;
        this.rusName = rusName;
    }

    public String getName(){
        return name;
    }

    public String getDbName(){
        return dbName;
    }

    public String getRusName(){
        return rusName;
    }
}
