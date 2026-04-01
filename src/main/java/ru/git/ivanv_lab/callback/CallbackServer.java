package ru.git.ivanv_lab.callback;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.git.ivanv_lab.model.general.Status;
import ru.git.ivanv_lab.model.general.Transport;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CallbackServer {

    private static ConcurrentHashMap<CallbackKey, JsonNode> callBacks = new ConcurrentHashMap<>();
    private static CallbackServer instance;
    private static HttpServer httpServer;

    private static final String SAVE_FILE = "src/test/resources/callback_stat.ser";

    private CallbackServer(HttpServer httpServer) {
        CallbackServer.httpServer = httpServer;
    }

    public static synchronized CallbackServer getInstance(String host, int port, String path) {
        if (instance != null)
            return instance;

        try {
            InetSocketAddress socketAddress = new InetSocketAddress(host, port);
            HttpServer server = HttpServer.create(socketAddress, 0);
            instance = new CallbackServer(server);

            server.createContext(path, instance::handle);
            server.start();
            System.out.println("CallbackServer на http://" + host + ":" + port + path);

            return instance;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании сервера", e);
        }
    }

    private void handle(HttpExchange exchange) {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes());
            System.out.println("\n" + body);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(body)
                    .path("messages")
                    .get(0);

            String messageId = rootNode
                    .path("message-id")
                    .asString().toLowerCase();

            Transport transport = Transport.valueOf(rootNode
                    .path("channel")
                    .asString().toLowerCase());

            Status status = Status.valueOf(rootNode
                    .path("status")
                    .asString().toLowerCase());

            String pushAppName = null;
            if (rootNode.has("application"))
                pushAppName = rootNode
                        .path("application")
                        .asString().toLowerCase();

            exchange.sendResponseHeaders(200, "OK".length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write("OK".getBytes());
            }

            if (pushAppName != null)
                callBacks.put(new CallbackKey(messageId, transport, status, pushAppName), rootNode);
            else callBacks.put(new CallbackKey(messageId, transport, status), rootNode);

        } catch (IOException e) {
            throw new RuntimeException("Не удалось получить тело коллбэка", e);
        }
    }

    public static JsonNode getCallBack(CallbackKey key) {
        List<Map.Entry<CallbackKey, JsonNode>> matches = new ArrayList<>();

        for (Map.Entry<CallbackKey, JsonNode> entry : callBacks.entrySet()) {
            if(entry.getKey().matches(key))
                matches.add(entry);
        }

        if(!matches.isEmpty()) return matches.get(0).getValue();

        return null;
    }

    /**
     * Сохраняет коллбэки в файл
     */
    public static void saveCallbacksToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE, false))) {
            oos.writeObject(callBacks);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении коллбэков в файл", e);
        }
    }

    /**
     * Считывает коллбэки из файла в память
     */
    public static void loadCallbacksToMapFromFile() {
        File file = new File(SAVE_FILE);
        if (file.exists() && file.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
                callBacks = (ConcurrentHashMap<CallbackKey, JsonNode>) ois.readObject();
            } catch (EOFException e) {
                System.err.println(String.format("Файл '%s' повреждён. Создание нового файла"));
                callBacks = new ConcurrentHashMap<>();

                file.delete();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("Ошибка при записи коллбэков в память", e);
            }
        } else callBacks = new ConcurrentHashMap<>();
    }

    public static String getIPv4Address(int port) {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), port);
            return socket.getLocalAddress().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException("Не удалось получить значение IPv4 текущей машины", e);
        }
    }
}
