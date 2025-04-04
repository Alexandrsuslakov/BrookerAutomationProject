package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.CreatedBooking;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException; // Добавьте этот импорт
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        Response response = apiClient.createBooking("Jim", "Brown", 111, true, "2018-01-01", "2019-01-01", "Breakfast");
        assertThat(response.getStatusCode()).isEqualTo(200);
        String responseBody = response.asString();

       ObjectMapper objectMapper = new ObjectMapper();
        createdBooking = objectMapper.readValue(responseBody, CreatedBooking.class);

        assertThat(createdBooking).isNotNull();
        assertEquals("Jim",createdBooking.getBooking().getFirstname());
        assertEquals("Brown",createdBooking.getBooking().getLastname());
        assertEquals(111,createdBooking.getBooking().getTotalprice());
        assertEquals(true,createdBooking.getBooking().isDepositpaid());
        assertEquals("2018-01-01",createdBooking.getBooking().getBookingdates().getCheckin());
        assertEquals("2019-01-01",createdBooking.getBooking().getBookingdates().getCheckout());
    }
    @AfterEach
            public void deleteObject() {
        apiClient.createToken("admin", "password123");
        Response deleteResponse = apiClient.deleteBooking(createdBooking.getBookingid());
        assertThat(apiClient.getBookingById(createdBooking.getBookingid()).getStatusCode()).isEqualTo(404);

    }

}
