package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.Booking;
import core.models.CreatedBooking;
import core.models.GetBookingRQ;
import io.restassured.response.Response;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.*;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;


import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GetBookingTest {

    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private ObjectMapper objectMapperGet;
    private CreatedBooking createdBookingJim;
    private CreatedBooking createdBookingBoby;
    private CreatedBooking createdBookingAlex;


    @BeforeAll
    public void setup() throws JsonProcessingException {
        apiClient = new APIClient();

        step("Создание тестовых данных");
        Response responseJim = apiClient.createBooking("Jim", "Brown", 111, true, "2018-01-01", "2018-01-02", "Breakfast");
        AssertionsForClassTypes.assertThat(responseJim.getStatusCode()).isEqualTo(200);

        createdBookingJim = responseJim.as(CreatedBooking.class);

        step("Создание тестовых данных");
        Response responseBoby = apiClient.createBooking("Boby", "Brown", 222, false, "2022-01-01", "2022-02-01", "Breakfast");
        AssertionsForClassTypes.assertThat(responseBoby.getStatusCode()).isEqualTo(200);

        createdBookingBoby = responseBoby.as(CreatedBooking.class);

        step("Создание тестовых данных");
        Response responseAlex = apiClient.createBooking("Alex", "Brown", 333, true, "2021-01-01", "2021-02-01", "Breakfast");
        AssertionsForClassTypes.assertThat(responseAlex.getStatusCode()).isEqualTo(200);

        createdBookingAlex = responseAlex.as(CreatedBooking.class);

    }

    private Stream<Arguments> provideRandomBookingData() {
        Random random = new Random();
        LocalDate checkin = LocalDate.now().minusDays(random.nextInt(730));
        LocalDate checkout = checkin.plusDays(1 + random.nextInt(30));

        return Stream.of(
                Arguments.of("User" + UUID.randomUUID().toString().substring(0, 6), null, null, null),
                Arguments.of(null, "Last" + random.nextInt(10000), null, null),
                Arguments.of(null,null, checkin.toString(), null),
                Arguments.of("User" + UUID.randomUUID().toString().substring(0, 6), "Last" + random.nextInt(10000), checkin.toString(), checkout.toString()),
                Arguments.of("Boby", "Last" + random.nextInt(10000), checkin.toString(), checkout.toString())
        );
    }

    @ParameterizedTest
    @MethodSource("provideRandomBookingData")
    public void testGetBookingNotFound(String firstname, String lastname, String checkin, String checkout) throws Exception {

        GetBookingRQ getBookingRQ = new GetBookingRQ();

        if (firstname != null) {
            getBookingRQ.setFirstname(firstname);
        }
        if (lastname != null) {
            getBookingRQ.setLastname(lastname);
        }
        if (checkin != null) {
            getBookingRQ.setCheckin(checkin);
        }
        if (checkout != null) {
            getBookingRQ.setCheckout(checkout);
        }

        step("Отправка Get-запроса");
        Response responseGet = apiClient.getBooking(getBookingRQ);

        ObjectMapper objectMapper = new ObjectMapper();
        List<Booking> bookings = objectMapper.readValue(
                responseGet.getBody().asString(),
                new TypeReference<List<Booking>>() {}
        );

        assertThat(responseGet.getStatusCode()).isEqualTo(200);
        assertThat(bookings).isEmpty();
    }
    @ParameterizedTest
    @CsvSource({"'Jim','Brown','2018-01-01','2018-01-02'",
            ",'Brown',,",
            ",,,'2019-01-01'"
    })
    public void testGetBookingFound(String firstname, String lastname, String checkin, String checkout) throws Exception {

        GetBookingRQ getBookingRQ = new GetBookingRQ();

        if (firstname != null) {
            getBookingRQ.setFirstname(firstname);
        }
        if (lastname != null) {
            getBookingRQ.setLastname(lastname);
        }
        if (checkin != null) {
            getBookingRQ.setCheckin(checkin);
        }
        if (checkout != null) {
            getBookingRQ.setCheckout(checkout);
        }
        step("Отправка Get-запроса");
        Response responseGet = apiClient.getBooking(getBookingRQ);

        ObjectMapper objectMapper = new ObjectMapper();
        List<Booking> bookings = objectMapper.readValue(
                responseGet.getBody().asString(),
                new TypeReference<List<Booking>>() {}
        );

        assertThat(responseGet.getStatusCode()).isEqualTo(200);

        step("Проверка длинны массива");
        assertThat(bookings).hasSize(1);

        step("Проверка данных в ответе");
        assertThat(bookings.get(0)).isEqualTo(createdBookingJim.getBookingid());
    }
    @AfterAll
    public void deleteObject() {
        apiClient.createToken("admin", "password123");

        step("Очистка данных после выполнения тестов");
        Response deleteResponseJim = apiClient.deleteBooking(createdBookingJim.getBookingid());
        AssertionsForClassTypes.assertThat(deleteResponseJim.getStatusCode()).isEqualTo(200);

        step("Очистка данных после выполнения тестов");
        Response deleteResponseBoby = apiClient.deleteBooking(createdBookingBoby.getBookingid());
        AssertionsForClassTypes.assertThat(deleteResponseBoby.getStatusCode()).isEqualTo(200);

        step("Очистка данных после выполнения тестов");
        Response deleteResponseAlex = apiClient.deleteBooking(createdBookingAlex.getBookingid());
        AssertionsForClassTypes.assertThat(deleteResponseAlex.getStatusCode()).isEqualTo(200);
    }
}



