package tests;

import core.clients.APIClient;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeleteBookingTest {

    private APIClient apiClient;
    private int idBook;

    @BeforeEach
    public void setup() {
        // Инициализация APIClient
        apiClient = new APIClient();

        step ("Создание токена");
        apiClient.createToken("admin", "password123");

        step ("Создание тестовых данных");
        Response createResponse = apiClient.createBooking("Jim", "Brown", 111, true, "2018-01-01", "2019-01-01", "Breakfast");
        assertEquals(200, createResponse.getStatusCode(), "Статус код неверный");
        idBook = createResponse.path("bookingid");

        Response responseGetBookingFound = apiClient.getBookingById(idBook);
        assertEquals(200, responseGetBookingFound.getStatusCode(), "Статус код неверный");

    }

    @Test
    public void testDeleteBookingSuccess() {

        step ("Удаление брони");
        Response deleteResponse = apiClient.deleteBooking(idBook);

        step ("Проверка статус кода упешного удаления");
        assertEquals(201, deleteResponse.getStatusCode(), "Статус код неверный");

        step ("Проверка отсутствия сущности после удаления");
        Response responseGetBookingIdNotFound = apiClient.getBookingById(idBook);
        assertEquals(404, responseGetBookingIdNotFound.getStatusCode(), "Статус код неверный");
    }

    @Test
    public void testDeleteBookingNoBook() {

        step ("Удаление брони");
        Response deleteResponse = apiClient.deleteBooking(idBook);
        assertEquals(201, deleteResponse.getStatusCode(), "Статус код неверный");
        step ("Проверка ошибки 405 ошибки пр повторном удалении сущности");
        Response deleteResponseFail = apiClient.deleteBooking(idBook);
        assertEquals(405, deleteResponseFail.getStatusCode(), "Статус код неверный");

        step ("Проверка отсутствия сущности после удаления");
        Response responseGetBookingIdNotFound = apiClient.getBookingById(idBook);
        assertEquals(404, responseGetBookingIdNotFound.getStatusCode(), "Статус код неверный");

    }

    @Test
    public void testDeleteBookingNoToken() {
        apiClient.setToken(null);

        step ("Неуспешная попытка удаление брони (no token)");
        Response deleteResponse = apiClient.deleteBooking(idBook);
        assertEquals(403, deleteResponse.getStatusCode(), "Статус код неверный");

        step ("Проверка, что сущность не удалилась");
        Response responseGetBookingFound = apiClient.getBookingById(idBook);
        assertEquals(200, responseGetBookingFound.getStatusCode(), "Статус код неверный");

    }

    @Test
    public void testDeleteBookingInvalidToken() {
        apiClient.setToken("67730685d432008");

        step ("Неуспешная попытка удаление брони (InvalidToke)");
        Response deleteResponse = apiClient.deleteBooking(idBook);
        assertEquals(403, deleteResponse.getStatusCode(), "Статус код неверный");

        step ("Проверка, что сущность не удалилась");
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
