package ru.git.ivanv_lab.data;

import ru.git.ivanv_lab.api.ApiWorker;
import ru.git.ivanv_lab.model.api.settings.Partner;

public class DataFabric {

    private final ThreadLocal<ApiWorker> adminApi=ThreadLocal
            .withInitial(()->new ApiWorker("acapi","admin@admin.com","Admin"));

    public DataFabric addPartner(Partner partner){

    }
}
