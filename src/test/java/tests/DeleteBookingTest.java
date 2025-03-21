package tests;

import core.clients.APIClient;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeleteBookingTest {

    private APIClient apiClient;

    @BeforeEach
    public void setup() {
        // Инициализация APIClient
        apiClient = new APIClient();
        apiClient.createToken("admin", "password123");

    }

    @Test
    public void testDeleteBookingSuccess() {

        Response createResponse = apiClient.createBooking("Jim", "Brown", 111, true, "2018-01-01", "2019-01-01", "Breakfast");
        assertEquals(200, createResponse.getStatusCode(), "Статус код неверный");// Отображаем токен
        int idBook = createResponse.path("bookingid");


        Response deleteResponse = apiClient.deleteBooking(idBook);
        assertEquals(201, deleteResponse.getStatusCode(), "Статус код неверный");

    }

    @Test
    public void testDeleteBookingNoBook() {

        Response createResponse = apiClient.createBooking("Jim", "Brown", 111, true, "2018-01-01", "2019-01-01", "Breakfast");
        assertEquals(200, createResponse.getStatusCode(), "Статус код неверный");// Отображаем токен
        int idBook = createResponse.path("bookingid");

        Response deleteResponse = apiClient.deleteBooking(idBook);
        assertEquals(201, deleteResponse.getStatusCode(), "Статус код неверный");

        Response deleteResponseFail = apiClient.deleteBooking(idBook);
        assertEquals(405, deleteResponseFail.getStatusCode(), "Статус код неверный");

    }

    @Test
    public void testDeleteBookingNoToken() {
        apiClient.setToken(null);

        Response createResponse = apiClient.createBooking("Jim", "Brown", 111, true, "2018-01-01", "2019-01-01", "Breakfast");
        assertEquals(200, createResponse.getStatusCode(), "Статус код неверный");// Отображаем токен
        int idBook = createResponse.path("bookingid");


        Response deleteResponse = apiClient.deleteBooking(idBook);
        assertEquals(403, deleteResponse.getStatusCode(), "Статус код неверный");


    }

    @Test
    public void testDeleteBookingInvalidToken() {
        apiClient.setToken("67730685d432008");

        Response createResponse = apiClient.createBooking("Jim", "Brown", 111, true, "2018-01-01", "2019-01-01", "Breakfast");
        assertEquals(200, createResponse.getStatusCode(), "Статус код неверный");// Отображаем токен
        int idBook = createResponse.path("bookingid");


        Response deleteResponse = apiClient.deleteBooking(idBook);
        assertEquals(403, deleteResponse.getStatusCode(), "Статус код неверный");


    }

    @Test
    public void testDeleteBookingOldToken() {

        String token = apiClient.getToken();

        apiClient.createToken("admin", "password123"); // Создаем новый токен

        apiClient.setToken(token);// Присваиваем старое значение токена для проверки удаления по старому токену

        Response createResponse = apiClient.createBooking("Jim", "Brown", 111, true, "2018-01-01", "2019-01-01", "Breakfast");

        assertEquals(200, createResponse.getStatusCode(), "Статус код неверный");// Отображаем токен
        int idBook = createResponse.path("bookingid");

        Response deleteResponse = apiClient.deleteBooking(idBook);
        assertEquals(201, deleteResponse.getStatusCode(), "Статус код неверный");


    }
}