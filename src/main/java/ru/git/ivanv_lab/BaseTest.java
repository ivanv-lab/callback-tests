package ru.git.ivanv_lab;

import ru.git.ivanv_lab.db.SqlDataFabric;
import ru.git.ivanv_lab.db.SqlWorker;

public class BaseTest {

    protected static SqlWorker worker=new SqlWorker("msg","msg","msg");
    public static SqlDataFabric fabric=new SqlDataFabric(worker);
}
