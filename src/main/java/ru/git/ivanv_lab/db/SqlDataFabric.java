package ru.git.ivanv_lab.db;

public class SqlDataFabric {
    private final ThreadLocal<SqlWorker> workerThreadLocal=
            ThreadLocal.withInitial(()->new SqlWorker("msg","msg","msg"));


}
