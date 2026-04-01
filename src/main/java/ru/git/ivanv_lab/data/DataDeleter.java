package ru.git.ivanv_lab.data;

import ru.git.ivanv_lab.api.ApiWorker;
import ru.git.ivanv_lab.model.api.push.PushTariff;
import ru.git.ivanv_lab.model.api.settings.Account;
import ru.git.ivanv_lab.model.api.settings.Partner;
import ru.git.ivanv_lab.model.general.Transport;

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

    public void deleteAccount(Account account){
        long accountId=sqlFabric.getAccountId(account.getName(), account.getPartnerId());

        if(accountId!=0)
            adminApi.get()
                    .delete("/acapi/accounts/"+accountId)
                    .code(200);
    }

    public void deletePushTariff(PushTariff pushTariff){
        long pushTariffId=sqlFabric.getTariffId(Transport.PUSH,pushTariff.getPartnerId());

        if(pushTariffId!=0)
            adminApi.get()
                    .delete("/acapi/push/partners/tariffs/"+pushTariffId)
                    .code(200);
    }
}
