package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.Booking;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.type.TypeReference;



import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GetBookingTest {

    private APIClient apiClient;
    private ObjectMapper objectMapper; // Объявление переменной


    //Иницилизация API клиента перед каждым тестом
    @BeforeEach
    public void setup() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
    }

    //Тест на получение bookingid()
    @Test
    public void testGetBooking() throws Exception{
        Response response = apiClient.getBooking();
        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());
        assertThat(response.getStatusCode()).isEqualTo(200);

        //Десериализуем тело ответа в список объектов
        String responseBody = response.getBody().asString();
        List<Booking> bookingList = objectMapper.readValue(responseBody, new TypeReference<List<Booking>>() {});

        //Проверяем, что тело ответа содержит объекты Booking
        assertThat(bookingList).isNotEmpty(); //проверка, что список не пуст.

        // Проверяем, что каждый объект Booking содержит валидное значение bookings
        for (Booking booking : bookingList) {
            assertThat(booking.getBookingid()).isGreaterThan(0); //проверка, что getBookingId > 0
        }

    }

}

