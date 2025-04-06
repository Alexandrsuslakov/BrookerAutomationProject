package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.BookingById;
import core.models.CreatedBooking;
import core.models.NewBooking;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static io.qameta.allure.Allure.step;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PutUpdateBookingTest {
    private APIClient apiClient;
    private CreatedBooking createdBooking;
    private NewBooking updatedBooking;
    private int idBooking;

    @BeforeEach
    public void setup() throws JsonProcessingException {
        // Инициализация APIClient
        apiClient = new APIClient();
        apiClient.createToken("admin", "password123");
        Response response = apiClient.createBooking("Jim", "Brown", 111, true, "2018-01-01", "2019-01-01", "Breakfast");
        assertThat(response.getStatusCode()).isEqualTo(200);

        String responseBody = response.asString();

        ObjectMapper objectMapper = new ObjectMapper();

        createdBooking = objectMapper.readValue(responseBody, CreatedBooking.class);
        idBooking = createdBooking.getBookingid();
    }

    @Test
    public void testUpdateBookingSuccess() throws JsonProcessingException {
        Response response = apiClient.putUpdateBooking(idBooking,"BOBBY","HENDRICSON",222,false,"2018-01-02","2019-01-02","Dinner");
        assertThat(response.getStatusCode()).isEqualTo(200);
        String responseBody = response.asString();


        ObjectMapper objectMapper = new ObjectMapper();
        updatedBooking = objectMapper.readValue(responseBody, NewBooking.class);

        step("Проверка тела ответа");
        assertThat(updatedBooking).isNotNull();
        assertEquals("BOBBY",updatedBooking.getFirstname());
        assertEquals("HENDRICSON",updatedBooking.getLastname());
        assertEquals(222,updatedBooking.getTotalprice());
        assertEquals(false,updatedBooking.isDepositpaid());
        assertEquals("2018-01-02",updatedBooking.getBookingdates().getCheckin());
        assertEquals("2019-01-02",updatedBooking.getBookingdates().getCheckout());

        ObjectMapper objectMapperGet = new ObjectMapper();
        Response responseGet = apiClient.getBookingById(idBooking);

        step("Проверка корректности обновленных данных");
        BookingById bookingById = objectMapperGet.readValue(responseGet.asString(), BookingById.class);
        assertEquals("BOBBY", bookingById.getFirstname(), "Имя пользователя не совпадает");
        assertEquals("HENDRICSON", bookingById.getLastname(), "Неверная Фамилия");
        assertEquals(222, bookingById.getTotalprice(), "Неверная сумма");
        assertEquals(false, bookingById.isDepositpadid(), "Неверное значение");
        assertEquals("Dinner", bookingById.getAdditionalneeds(), "Неверное значение");
        assertEquals("2018-01-02", bookingById.getBookingdates().getCheckin(), "Неверная дата заезда");
        assertEquals("2019-01-02", bookingById.getBookingdates().getCheckout(), "Неверная дата выезда");

    }
    @Test
    public void testUpdateBookingNoToken() throws JsonProcessingException {
        apiClient.setToken(null);

        step("Попытка обновления бронироваиня без токена");
        Response response = apiClient.putUpdateBooking(idBooking,"LOLA","CATCH",6667,true,"2018-01-02","2019-01-02","Dinner");
        assertThat(response.getStatusCode()).isEqualTo(403);

        String responseBody = response.asString();
        Assertions.assertThat(responseBody).isEqualTo("Forbidden");


    }
    @AfterEach
    public void deleteObject() {
        apiClient.createToken("admin", "password123");

        step("Удаление данных после завершения теста");
        Response deleteResponse = apiClient.deleteBooking(createdBooking.getBookingid());
        assertThat(apiClient.getBookingById(createdBooking.getBookingid()).getStatusCode()).isEqualTo(404);

    }
}