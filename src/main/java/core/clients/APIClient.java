package core.clients;

import core.settings.ApiEndpoints;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.filter.Filter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class APIClient {

    private final String baseUrl;

    public void setToken(String token) {
        this.token = token;
    }

    private String token;

    public String getToken() {
        return token;
    }


    public APIClient() {
        this.baseUrl = determineBaseUrl();
    } //определение базового URl на основе файла конфигураций

    private String determineBaseUrl() {
        String environment = System.getProperty("env", "test");
        String configFileName = "application-" + environment + ".properties";

        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFileName)) {
            if (input == null) {
                throw new IllegalStateException("Configuration file not found: " + configFileName);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load configuration file:" + configFileName, e);
        }

        return properties.getProperty("baseUrl");
    }



    private Filter addAuthTokenFilter() {
        return (requestSpec, responseSpec, ctx) -> {
            if (token != null) {
                requestSpec.header("Cookie", "token=" + token); // Исправлено форматирование
            }
            return ctx.next(requestSpec, responseSpec);
        };
    }


    private RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .baseUri(baseUrl)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .filter(addAuthTokenFilter());

    }

    //Метод для получения токена
    public void createToken(String username, String password) {
        String requestBody = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);
        Response response = getRequestSpec()
                .body(requestBody)
                .when()
                .post(ApiEndpoints.AUTH.getPath());
        if (response.getStatusCode() == 200) {
            token = response.jsonPath().getString("token");
        } else {
            throw new RuntimeException("Failed to get token: " + response.getStatusCode());
        }
    }


    public Response ping() {
        return getRequestSpec()
                .when()
                .get(ApiEndpoints.PING.getPath())
                .then()
                .statusCode(201)
                .extract()
                .response();


    }

    public Response getBooking() {
        return getRequestSpec()
                .when()
                .get(ApiEndpoints.BOOKING.getPath())
                .then()
                .statusCode(200)
                .extract()
                .response();


    }

    public Response getBookingById(int bookingId) {
        return getRequestSpec()
                .log().all()
                .when()
                .get(ApiEndpoints.BOOKING.getPath() + "/" + bookingId)
                .then()
                .log().all()
                .extract()
                .response();


    }

    public Response deleteBooking(int bookingId) {
        return getRequestSpec()
                .log().all()
                .pathParam("id",bookingId)
                .when()
                .delete(ApiEndpoints.BOOKING.getPath() + "/{id}")
                .then()
                .log().all()
                .extract()
                .response();


    }
    public Response createBooking(String firstname, String lastname, int totalprice, boolean depositpaid,String checkin, String checkout, String additionalneeds) {
    String requestBody = String.format("{\"firstname\": \"%s\", \"lastname\": \"%s\", \"totalprice\": %d, \"depositpaid\": %b, \"bookingdates\" : { \"checkin\": \"%s\", \"checkout\": \"%s\"}, \"additionalneeds\": \"%s\"}", firstname, lastname, totalprice, depositpaid, checkin, checkout, additionalneeds);
    return getRequestSpec()
                .log().all()
                .when()
                .body(requestBody)
                .post(ApiEndpoints.BOOKING.getPath())
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();


    }


}


