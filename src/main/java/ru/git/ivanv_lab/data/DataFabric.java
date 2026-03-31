package ru.git.ivanv_lab.data;

import ru.git.ivanv_lab.api.ApiWorker;
import ru.git.ivanv_lab.model.api.settings.Partner;
import ru.git.ivanv_lab.model.mapping.Mapper;

public class DataFabric {

    private final ThreadLocal<ApiWorker> adminApi=ThreadLocal
            .withInitial(()->new ApiWorker("acapi","admin@admin.com","Admin"));
    private final ThreadLocal<DataDeleter> deleter=ThreadLocal
            .withInitial(DataDeleter::new);

    public DataFabric addPartner(Partner partner){
        deleter.get().deletePartner(partner);

        String json= Mapper.toJson(partner);

        adminApi.get()
                .post("/acapi/partners",json)
                .code(200);

        return this;
    }
}
