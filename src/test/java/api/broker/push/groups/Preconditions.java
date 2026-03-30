package api.broker.push.groups;

import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import ru.git.ivanv_lab.api.ApiWorker;
import ru.git.ivanv_lab.model.Transport;
import ru.git.ivanv_lab.model.api.settings.Partner;
import ru.git.ivanv_lab.model.api.settings.PartnerTransport;

@Epic("broker-api")
@Feature("Группы Push-приложений. Предусловия")
public class Preconditions {

    private final ApiWorker worker = new ApiWorker("acapi", "admin@admin.com", "Admin");

    @Execution(ExecutionMode.SAME_THREAD)
    @DisplayName("Подготовка сущностей")
    @Test
    void preconditions() {
        Allure.step("Создание необходимых сущностей для тестирования Push-рассылок",
                () -> {
                    Allure.step("Создание клиента", this::createClient);
                    Allure.step("Добавление Custom в транспорты", this::addCustomTransport);
                    getPartnerId();
                    Allure.step("Создание аккаунта", this::createAccount);
                    Allure.step("Создание тарификации Push для клиента", this::createPushTariff);
                    Allure.step("Создание адресов отправителя", this::createSenderAddresses);
                    Allure.step("Создание пользователя Личного кабинета", this::createLKUser);
                    Allure.step("Добавление клиента к Push-приложениям", this::addPartnerToPushApp);
                    Allure.step("Создание подписок на Web-Push приложение", this::createSubscriptionsOnWPApplication);
                    Allure.step("Создание группы приложений", this::createPushAppGroup);
                    Allure.step("Создание черного списка клиента", this::createBlackList);
                    Allure.step("Добавление контакта в черный список клиента", this::addContactToBlackList);
                    Allure.step("Создание общего черного списка", this::createGeneralBlackList);
                    Allure.step("Создание типа сообщения", this::createMessageType);
                    Allure.step("Создание типа сообщения с черным списком", this::createMessageTypeWithBlackList);
                    Allure.step("Создание поставщика услуг", this::createServiceProvider);
                    Allure.step("Создание MN сервиса для клиента", this::createMailNotifyService);
                });
    }

    private void createClient() {
        Partner partner=new Partner.Builder()
                .withName("PushPartner")
                .withTransports(new PartnerTransport[]{
                        new PartnerTransport.Builder()
                                .withName(Transport.PUSH)
                                .withModeration(false)
                                .withMultiSignature(false)
                                .withTemplateOnly(false)
                                .build()
                })
                .withPrepaid(false)
                .withStatus(true)
                .build();


    }

    private void addCustomTransport() {

    }

    private void getPartnerId() {

    }

    private void createAccount() {

    }

    private void createPushTariff() {

    }

    private void createSenderAddresses() {

    }

    private void createLKUser() {

    }

    private void addPartnerToPushApp() {

    }

    private void createSubscriptionsOnWPApplication() {

    }

    private void createPushAppGroup() {

    }

    private void createBlackList() {

    }

    private void addContactToBlackList() {

    }

    private void createGeneralBlackList() {

    }

    private void createMessageType() {

    }

    private void createMessageTypeWithBlackList() {

    }

    private void createServiceProvider() {

    }

    private void createMailNotifyService() {

    }
}
