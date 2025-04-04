package tests;

import core.clients.APIClient;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeleteBookingTest {

    private APIClient apiClient;
    private int idBook;

    @BeforeEach
    public void setup() {
        // Инициализация APIClient
        apiClient = new APIClient();
        apiClient.createToken("admin", "password123");

        Response createResponse = apiClient.createBooking("Jim", "Brown", 111, true, "2018-01-01", "2019-01-01", "Breakfast");
        assertEquals(200, createResponse.getStatusCode(), "Статус код неверный");// Отображаем токен
        idBook = createResponse.path("bookingid");

        Response responseGetBookingFound = apiClient.getBookingById(idBook);
        assertEquals(200, responseGetBookingFound.getStatusCode(), "Статус код неверный");

    }

    @Test
    public void testDeleteBookingSuccess() {

        Response deleteResponse = apiClient.deleteBooking(idBook);
        assertEquals(201, deleteResponse.getStatusCode(), "Статус код неверный");

        Response responseGetBookingIdNotFound = apiClient.getBookingById(idBook);
        assertEquals(404, responseGetBookingIdNotFound.getStatusCode(), "Статус код неверный");

    }

    @Test
    public void testDeleteBookingNoBook() {

        Response deleteResponse = apiClient.deleteBooking(idBook);
        assertEquals(201, deleteResponse.getStatusCode(), "Статус код неверный");

        Response deleteResponseFail = apiClient.deleteBooking(idBook);
        assertEquals(405, deleteResponseFail.getStatusCode(), "Статус код неверный");

        Response responseGetBookingIdNotFound = apiClient.getBookingById(idBook);
        assertEquals(404, responseGetBookingIdNotFound.getStatusCode(), "Статус код неверный");

    }

    @Test
    public void testDeleteBookingNoToken() {
        apiClient.setToken(null);

        Response deleteResponse = apiClient.deleteBooking(idBook);
        assertEquals(403, deleteResponse.getStatusCode(), "Статус код неверный");

        Response responseGetBookingFound = apiClient.getBookingById(idBook);
        assertEquals(200, responseGetBookingFound.getStatusCode(), "Статус код неверный");

    }

    @Test
    public void testDeleteBookingInvalidToken() {
        apiClient.setToken("67730685d432008");

        Response deleteResponse = apiClient.deleteBooking(idBook);
        assertEquals(403, deleteResponse.getStatusCode(), "Статус код неверный");

        Response responseGetBookingFound = apiClient.getBookingById(idBook);
        assertEquals(200, responseGetBookingFound.getStatusCode(), "Статус код неверный");

    }

    @Test
    public void testDeleteBookingOldToken() {

        String token = apiClient.getToken();

        apiClient.createToken("admin", "password123"); // Создаем новый токен

        apiClient.setToken(token);// Присваиваем старое значение токена для проверки удаления по старому токену

        Response deleteResponse = apiClient.deleteBooking(idBook);
        assertEquals(201, deleteResponse.getStatusCode(), "Статус код неверный");

        Response responseGetBookingIdNotFound = apiClient.getBookingById(idBook);
        assertEquals(404, responseGetBookingIdNotFound.getStatusCode(), "Статус код неверный");

    }
}
