package ru.git.ivanv_lab.data;

import ru.git.ivanv_lab.api.ApiWorker;
import ru.git.ivanv_lab.model.api.settings.Partner;

import static ru.git.ivanv_lab.BaseTest.sqlFabric;

public class DataDeleter {

    private final ThreadLocal<ApiWorker> adminApi = ThreadLocal
            .withInitial(() -> new ApiWorker("acapi", "admin@admin.com", "Admin"));

    public void deletePartner(Partner partner) {
        long partnerId = sqlFabric.getPartnerId(partner.getName());

        if (partnerId != 0)
            adminApi.get()
                    .delete("/acapi/partners/" + partnerId)
                    .code(200);
    }
}
