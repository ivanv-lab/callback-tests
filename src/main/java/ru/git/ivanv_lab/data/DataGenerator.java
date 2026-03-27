package ru.git.ivanv_lab.data;

import com.github.javafaker.Faker;

public class DataGenerator {
    private static final Faker faker=new Faker();

    public String genNumber(){
        return faker.bothify("1###########");
    }
}
