package ru.git.ivanv_lab.model.mapping;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public class Mapper {
    private static final ObjectMapper OBJECT_MAPPER=new ObjectMapper();

    public static String toJson(Object model){
        try{
            return OBJECT_MAPPER.writeValueAsString(model);
        } catch (JacksonException e){
            throw new RuntimeException("Не удалось преобразовать сущность '%s' в JSON"
                    .formatted(model), e);
        }
    }
}
