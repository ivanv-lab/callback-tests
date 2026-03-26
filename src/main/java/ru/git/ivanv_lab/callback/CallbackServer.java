package ru.git.ivanv_lab.callback;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class CallbackServer {

    private static ConcurrentHashMap<CallbackKey, JsonNode> callBacks=new ConcurrentHashMap<>();
    private static CallbackServer instance;
    private static HttpServer httpServer;

    private static final String SAVE_FILE="src/test/resources/callback_stat.ser";

    private CallbackServer(HttpServer httpServer){CallbackServer.httpServer=httpServer;}

    public static synchronized CallbackServer newInstance(String host, int port, String path){
        if(instance!=null)
            return instance;

        try {
            InetSocketAddress socketAddress = new InetSocketAddress(host, port);
            HttpServer server = HttpServer.create(socketAddress, 0);
            instance=new CallbackServer(server);

            server.createContext(path, instance::handle);
            server.start();
            System.out.println("CallbackServer на http://"+host+":"+port+path);

            return instance;
        } catch (IOException e){
            throw new RuntimeException("Ошибка при создании сервера", e);
        }
    }

    public static CallbackServer getInstance(){
        if(instance==null){
            System.err.println("Сервер не запущен");
            return null;
        }
        return instance;
    }

    private void handle(HttpExchange exchange){
        try {
            String body = new String(exchange.getRequestBody().readAllBytes());
            System.out.println("\n"+body);

            exchange.sendResponseHeaders(200, "OK".length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write("OK".getBytes());
            }
        } catch (IOException e){
            throw new RuntimeException("Не удалось получить тело коллбэка", e);
        }
    }

    /**
     * Сохраняет коллбэки в файл
     */
    public static void saveCallbacksToFile(){
        try(ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(SAVE_FILE,false))){
            oos.writeObject(callBacks);
        } catch (IOException e){
            throw new RuntimeException("Ошибка при сохранении коллбэков в файл", e);
        }
    }

    /**
     * Считывает коллбэки из файла в память
     */
    public static void loadCallbacksToMapFromFile(){
        File file = new File(SAVE_FILE);
        if(file.exists() && file.length()>0){
            try(ObjectInputStream ois=new ObjectInputStream(new FileInputStream(SAVE_FILE))){
                callBacks = (ConcurrentHashMap<CallbackKey, JsonNode>) ois.readObject();
            } catch (EOFException e){
                System.err.println(String.format("Файл '%s' повреждён. Создание нового файла"));
                callBacks=new ConcurrentHashMap<>();

                file.delete();
            } catch (IOException | ClassNotFoundException e){
                throw new RuntimeException("Ошибка при записи коллбэков в память", e);
            }
        } else callBacks=new ConcurrentHashMap<>();
    }
}
