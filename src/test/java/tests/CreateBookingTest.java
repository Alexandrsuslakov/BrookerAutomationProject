package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.CreatedBooking;
import io.qameta.allure.Epic;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import static io.qameta.allure.Allure.step;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Создание новых сущностей")
public class CreateBookingTest {
    private APIClient apiClient;
    private CreatedBooking createdBooking;

    @BeforeEach
    public void setup() {
        // Инициализация APIClient
        apiClient = new APIClient();
    }

    @Test
    public void testCreateBookingSuccess() throws JsonProcessingException {
       step("Отправка запроса создание брони");
        Response response = apiClient.createBooking("Jim", "Brown", 111, true, "2018-01-01", "2019-01-01", "Breakfast");
        assertThat(response.getStatusCode()).isEqualTo(200);
        String responseBody = response.asString();

       ObjectMapper objectMapper = new ObjectMapper();
        createdBooking = objectMapper.readValue(responseBody, CreatedBooking.class);
        step("Проверка наличия тела ответа");
        assertThat(createdBooking).isNotNull();

        step("Проверка поля firstname");
        assertEquals("Jim",createdBooking.getBooking().getFirstname());

        step("Проверка поля lastname");
        assertEquals("Brown",createdBooking.getBooking().getLastname());

        step("Проверка поля totalprice");
        assertEquals(111,createdBooking.getBooking().getTotalprice());

        step("Проверка поля depositpaid");
        assertEquals(true,createdBooking.getBooking().isDepositpaid());

        step("Проверка поля checkin");
        assertEquals("2018-01-01",createdBooking.getBooking().getBookingdates().getCheckin());

        step("Проверка поля checkout");
        assertEquals("2019-01-01",createdBooking.getBooking().getBookingdates().getCheckout());
    }
    @AfterEach
    @Step("Удаление тестовых данных")
            public void deleteObject() {
        apiClient.createToken("admin", "password123");
        Response deleteResponse = apiClient.deleteBooking(createdBooking.getBookingid());
        assertThat(apiClient.getBookingById(createdBooking.getBookingid()).getStatusCode()).isEqualTo(404);

    }

}
