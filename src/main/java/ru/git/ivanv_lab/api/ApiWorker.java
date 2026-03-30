package ru.git.ivanv_lab.api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.git.ivanv_lab.data.PropertyProvider;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiWorker {
    private static final Logger log = LoggerFactory.getLogger(ApiWorker.class);
    private final String baseURL = PropertyProvider.getBaseUrl();

    private static ConcurrentHashMap<ApiContract, String> tokenMap = new ConcurrentHashMap<ApiContract, String>();
    private ThreadLocal<ApiContract> contract = new ThreadLocal<>();
    private ThreadLocal<Response> response = new ThreadLocal<>();

    public ApiWorker(String apiName, String login, String password) {
        initialize(apiName, login, password);
    }

    private void initialize(String apiName, String login, String password){
        synchronized (ApiWorker.class) {
            contract.set(new ApiContract(apiName, login, password));
            if (!tokenMap.containsKey(contract.get())) {
                getToken(contract.get());
            }
        }
    }

    private void getToken(ApiContract contract) {
        try {
            String tempToken = null;
            String url = baseURL + "/" + contract.apiName + "/auth";

            if (contract.apiName.equals("broker")) {
                String toEncode = contract.login + ":" + contract.password;
                tempToken = "Basic " + Base64.getEncoder().encodeToString(toEncode.getBytes(StandardCharsets.UTF_8));
            } else if (contract.apiName.equals("multisignature-broker")) {
                String toEncode = contract.login + ":" + contract.password;
                tempToken = "Basic " + Base64.getEncoder().encodeToString(toEncode.getBytes(StandardCharsets.UTF_8));
            } else {
                Response resp = given()
                        .filter(new AllureRestAssured())
                        .body(String.format("""
                                {
                                    "username":"%s",
                                    "password":"%s"
                                }
                                """, contract.login, contract.password))
                        .contentType("application/json")
                        .post(url)
                        .then()
                        .extract().response();

                log.warn("Получен ответ от API: \n{}", resp.body().prettyPrint());
                tempToken = resp.body().jsonPath().getString("token");
                response.set(resp);
            }

            if (tempToken != null) {
                tokenMap.put(contract, tempToken);
            } else {
                throw new RuntimeException("Токен пуст");
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении токена", e);
        }
    }

    public ApiWorker get(String url) {
        try {
            Response resp = given()
                    .filter(new AllureRestAssured())
                    .header("Authorization", getTokenFromMap(contract.get()))
                    .contentType("application/json")
                    .get(baseURL + url)
                    .then()
                    .extract().response();
            resp.body().print();
            if(resp.getStatusCode()==401){
                resp=getWithRetry(url);
            }
            response.set(resp);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        return this;
    }

    private Response getWithRetry(String url){
        tokenMap.remove(contract.get());
        getToken(contract.get());
        for(int attempt=1;attempt<=3;attempt++){
            Response resp=given()
                    .filter(new AllureRestAssured())
                    .header("Authorization", getTokenFromMap(contract.get()))
                    .contentType("application/json")
                    .get(baseURL + url)
                    .then()
                    .extract().response();

            if(resp.getStatusCode()!=401)
                return resp;
        }
        throw new RuntimeException("Ошибка авторизации");
    }

    public ApiWorker get(String url, String body) {
        Response resp = given()
                .filter(new AllureRestAssured())
                .header("Authorization", getTokenFromMap(contract.get()))
                .get(baseURL + url)
                .then()
                .extract().response();
        resp.body().print();
        response.set(resp);
        return this;
    }

    public ApiWorker put(String url, String body) {
        try {
            Response resp = given()
                    .filter(new AllureRestAssured())
                    .header("Authorization", getTokenFromMap(contract.get()))
                    .contentType("application/json")
                    .and()
                    .body(body)
                    .when()
                    .put(baseURL + url)
                    .then()
                    .extract().response();
            System.out.println("Ответ от api: ");
            resp.body().print();
            if(resp.getStatusCode()==401)
                resp=putWithRetry(url,body);

            response.set(resp);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        return this;
    }

    private Response putWithRetry(String url,String body){
        tokenMap.remove(contract.get());
        getToken(contract.get());
        for(int attempt=1;attempt<=3;attempt++){
            Response resp=given()
                    .filter(new AllureRestAssured())
                    .header("Authorization", getTokenFromMap(contract.get()))
                    .contentType("application/json")
                    .and()
                    .body(body)
                    .when()
                    .put(baseURL + url)
                    .then()
                    .extract().response();

            if(resp.getStatusCode()!=401)
                return resp;
        }
        throw new RuntimeException("Ошибка авторизации");
    }

    public ApiWorker post(String url, String body) {
        try {
            Response resp = given()
                    .filter(new AllureRestAssured())
                    .header("Authorization", getTokenFromMap(contract.get()))
                    .contentType("application/json;charset=utf-8;")
                    .and()
                    .body(body)
                    .when()
                    .post(baseURL + url)
                    .then()
                    .extract().response();
            resp.body().print();
            if(resp.getStatusCode()==401)
                resp=postWithRetry(url,body);

            response.set(resp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private Response postWithRetry(String url,String body){
        tokenMap.remove(contract.get());
        getToken(contract.get());
        for(int attempt=1;attempt<=3;attempt++){
            Response resp = given()
                    .filter(new AllureRestAssured())
                    .header("Authorization", getTokenFromMap(contract.get()))
                    .contentType("application/json;charset=utf-8;")
                    .and()
                    .body(body)
                    .when()
                    .post(baseURL + url)
                    .then()
                    .extract().response();

            if(resp.getStatusCode()!=401)
                return resp;
        }
        throw new RuntimeException("Ошибка авторизации");
    }

    public ApiWorker patch(String url, String body) {
        try {
            Response resp = given()
                    .filter(new AllureRestAssured())
                    .header("Authorization", getTokenFromMap(contract.get()))
                    .contentType("application/json")
                    .and()
                    .body(body)
                    .when()
                    .patch(baseURL + url)
                    .then()
                    .extract().response();
            resp.body().print();

            if(resp.getStatusCode()==401)
                resp=patchWithRetry(url,body);
            response.set(resp);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        return this;
    }

    private Response patchWithRetry(String url,String body){
        tokenMap.remove(contract.get());
        getToken(contract.get());
        for(int attempt=1;attempt<=3;attempt++) {
            Response resp = given()
                    .filter(new AllureRestAssured())
                    .header("Authorization", getTokenFromMap(contract.get()))
                    .contentType("application/json")
                    .and()
                    .body(body)
                    .when()
                    .patch(baseURL + url)
                    .then()
                    .extract().response();

            if(resp.getStatusCode()!=401)
                return resp;
        }
        throw new RuntimeException("Ошибка авторизации");
    }

    public ApiWorker delete(String url) {
        try {
            Response resp = given()
                    .filter(new AllureRestAssured())
                    .header("Authorization", getTokenFromMap(contract.get()))
                    .when()
                    .delete(baseURL + url)
                    .then()
                    .extract().response();
            resp.body().print();

            if(resp.getStatusCode()==401)
                resp=deleteWithRetry(url);

            response.set(resp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private Response deleteWithRetry(String url){
        tokenMap.remove(contract.get());
        getToken(contract.get());
        for(int attempt=1;attempt<=3;attempt++) {
            Response resp = given()
                    .filter(new AllureRestAssured())
                    .header("Authorization", getTokenFromMap(contract.get()))
                    .when()
                    .delete(baseURL + url)
                    .then()
                    .extract().response();

            if(resp.getStatusCode()!=401)
                return resp;
        }
        throw new RuntimeException("Ошибка авторизации");
    }

    public ApiWorker code(int code) {
        try {
            assertEquals(code, response.get().statusCode());
        } catch (AssertionError ae) {
            throw new RuntimeException("Ответ:\n"
                                       + "Статус: "+response.get().getStatusCode()+"\n"
                                       + response.get().asPrettyString());
        }
        return this;
    }

    public ApiWorker codeDescription(int code, String errorDescription){
        try {
            assertEquals(code, response.get().statusCode());
            assertEquals(errorDescription, response.get().body()
                    .jsonPath().get("error-description").toString());
        } catch (AssertionError ae) {
            throw new RuntimeException("Ответ:\n"
                                       + "Статус: "+response.get().getStatusCode()+"\n"
                                       + response.get().asPrettyString());
        }
        return this;
    }

    private static class ApiContract {
        String apiName;
        String login;
        String password;

        public ApiContract(String apiName, String login, String password) {
            this.apiName = apiName;
            this.login = login;
            this.password = password;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ApiContract that = (ApiContract) o;
            return Objects.equals(apiName, that.apiName) &&
                   Objects.equals(login, that.login) &&
                   Objects.equals(password, that.password);
        }

        @Override
        public int hashCode() {
            return Objects.hash(apiName, login, password);
        }
    }

    private String getTokenFromMap(ApiContract contract){
        if(!tokenMap.containsKey(contract)){
            getToken(contract);
        }

        return tokenMap.get(contract);
    }
}
