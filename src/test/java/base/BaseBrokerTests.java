package base;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import ru.git.ivanv_lab.callback.CallbackServer;
import ru.git.ivanv_lab.data.DataGenerator;

public class BaseBrokerTests {
    protected DataGenerator gen=new DataGenerator();
    private static CallbackServer server;

    @BeforeAll
    static void setUp(){
        server=CallbackServer.getInstance(CallbackServer.getIPv4Address(8484),
                8484, "/");
    }

    @AfterAll
    static void tearDown(){

    }
}
