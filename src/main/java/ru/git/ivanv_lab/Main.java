package ru.git.ivanv_lab;

import ru.git.ivanv_lab.callback.CallbackServer;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        CallbackServer server=CallbackServer.getInstance(CallbackServer.getIPv4Address(8484),8484,"/");
    }
}