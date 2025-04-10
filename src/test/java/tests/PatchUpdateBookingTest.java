package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.BookingById;
import core.models.CreatedBooking;
import core.models.NewBooking;
import core.models.Bookingdates;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PatchUpdateBookingTest {
    private APIClient apiClient;
    private int idBook;
    private CreatedBooking createdBooking;

    @BeforeEach
    public void setup() throws JsonProcessingException {
        apiClient = new APIClient();
        apiClient.createToken("admin", "password123");

        step("Создание тестовых данных перед выполнением теста");
        Response response = apiClient.createBooking("Jim", "Brown", 111, true, "2018-01-01", "2019-01-01", "Breakfast");
        assertThat(response.getStatusCode()).isEqualTo(200);

        createdBooking = response.as(CreatedBooking.class);
        idBook = createdBooking.getBookingid();

    }

    @ParameterizedTest
    @CsvSource({
            "'ВАСЯ','', ,'','','',''",
            "'', 'ИВАНОВ', ,'','','',''",
            "'', '', 1000,'','','',''",
            "'', '', ,'false','','',''",
            "'ВАСЯ', 'ИВАНОВ', 2000,'false','2020-01-01','2020-01-02','Dinner'"
    })
    public void testPartialUpdateBooking(String firstname, String lastname, Integer totalprice, String depositpaid, String checkin, String checkout, String additionalneeds) throws JsonProcessingException {
        NewBooking newBooking = new NewBooking();

        if (firstname != null && !firstname.isEmpty()) {
            newBooking.setFirstname(firstname);
        }

        if (lastname != null && !lastname.isEmpty()) {
            newBooking.setLastname(lastname);
        }
        if (totalprice != null) {
            newBooking.setTotalprice(totalprice);
        }

        if (depositpaid != null && !depositpaid.isEmpty()) {
            newBooking.setDepositpaid(Boolean.parseBoolean(depositpaid));
        }
        if ((checkin != null && !checkin.isEmpty()) || (checkout != null && !checkout.isEmpty())) {
            Bookingdates bookingdates = new Bookingdates();
            if (checkin != null && !checkin.isEmpty()) {
                bookingdates.setCheckin(checkin);
            }
            if (checkout != null && !checkout.isEmpty()) {
                bookingdates.setCheckout(checkout);
            }
            newBooking.setBookingdates(bookingdates);
        }
        if (additionalneeds != null && !additionalneeds.isEmpty()) {
                newBooking.setAdditionalneeds(additionalneeds);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        step("Обновления бронирования");
        Response updateResponse = apiClient.patchUpdateBooking(idBook, newBooking);
        assertThat(updateResponse.getStatusCode()).isEqualTo(200);

        NewBooking updatedBooking = objectMapper.readValue(updateResponse.asString(), NewBooking.class);

        step("Проверяем полученный ответ");
            if (firstname != null && !firstname.isEmpty()) {
                assertEquals(updatedBooking.getFirstname(), newBooking.getFirstname(), "Имя пользователя не совпадает");
            } else {
                assertEquals(updatedBooking.getFirstname(), createdBooking.getBooking().getFirstname(), "Имя пользователя не совпадает");
            }

            if (lastname != null && !lastname.isEmpty()) {
                assertEquals(updatedBooking.getLastname(), newBooking.getLastname(), "Неверная Фамилия");
            } else {
                assertEquals(updatedBooking.getLastname(), createdBooking.getBooking().getLastname(), "Неверная Фамилия");
            }

            if (totalprice != null) {
                assertEquals(updatedBooking.getTotalprice(), newBooking.getTotalprice(), "Неверная сумма");
            } else {
                assertEquals(updatedBooking.getTotalprice(), createdBooking.getBooking().getTotalprice(), "Неверная сумма");
            }

            if (depositpaid != null && !depositpaid.isEmpty()) {
                assertEquals(updatedBooking.isDepositpaid(), newBooking.isDepositpaid(), "Неверное значение");
            } else {
                assertEquals(updatedBooking.isDepositpaid(), createdBooking.getBooking().isDepositpaid(), "Неверное значение");
            }
            if (newBooking.getBookingdates() != null) {

            if (checkin != null && !checkin.isEmpty()) {
                assertEquals(updatedBooking.getBookingdates().getCheckin(), newBooking.getBookingdates().getCheckin(), "Неверная дата заезда");
            } else {
                assertEquals(updatedBooking.getBookingdates().getCheckin(), createdBooking.getBooking().getBookingdates().getCheckin(), "Неверная дата заезда");
            }
            if (checkout != null && !checkout.isEmpty()) {
                assertEquals(updatedBooking.getBookingdates().getCheckout(), newBooking.getBookingdates().getCheckout(), "Неверная дата выезда");
            } else {
                assertEquals(updatedBooking.getBookingdates().getCheckout(), createdBooking.getBooking().getBookingdates().getCheckout(), "Неверная дата выезда");
            }
        }

            if (additionalneeds != null && !additionalneeds.isEmpty()) {
                assertEquals(updatedBooking.getAdditionalneeds(), newBooking.getAdditionalneeds(), "Неверное значение");
            } else {
                assertEquals(updatedBooking.getAdditionalneeds(), createdBooking.getBooking().getAdditionalneeds(), "Неверное значение");
            }
        }

    @Test
    public void testUpdateBookingNoToken() throws JsonProcessingException {
        apiClient.setToken(null);

        NewBooking newBooking = new NewBooking();

        step("Попытка обновления бронироваиня без токена");
        Response updateResponse = apiClient.patchUpdateBooking(idBook,newBooking);
        assertThat(updateResponse.getStatusCode()).isEqualTo(403);

        String responseBody = updateResponse.asString();
        Assertions.assertThat(responseBody).isEqualTo("Forbidden");


    }
    @AfterEach
    public void deleteObject() {
        apiClient.createToken("admin", "password123");

        step("Очистка тестовых данных после завершения тестов");
        Response deleteResponse = apiClient.deleteBooking(createdBooking.getBookingid());
        assertThat(apiClient.getBookingById(createdBooking.getBookingid()).getStatusCode()).isEqualTo(404);
    }
}
