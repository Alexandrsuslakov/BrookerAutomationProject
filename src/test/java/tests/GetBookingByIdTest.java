package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.BookingById;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class GetBookingByIdTest {
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
    public void testGetBookingById() throws Exception{
        Response response = apiClient.getBookingById();

        //Десериализуем тело ответа в список объектов
        BookingById bookingById = objectMapper.readValue(response.asString(),BookingById.class);

        //Логирование ответа и статус кода
        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());

        //Проверка статус-кода ответа
        assertThat(response.getStatusCode()).isEqualTo(200);

        //Проверка на наличие полей в ответе
        assertNotNull(bookingById.getLastname(), "Поле Lastname не должен быть null");
        assertNotNull(bookingById.getTotalprice(), "Поле Totalprice не должен быть null");
        assertNotNull(bookingById.isDepositpadid(), "Поле Depositpadid не должен быть null");
        assertNotNull(bookingById.getAdditionalneeds(), "Поле Additionalneeds не должен быть null");
        assertNotNull(bookingById.getBookingdates().getCheckin(), "Поле Checkin не должен быть null");
        assertNotNull(bookingById.getBookingdates().getCheckout(), "Поле Checkout не должен быть null");

        // Проверка значения полей
        assertEquals("Mark",bookingById.getFirstname(),"Имя пользователя не совпадает");
        assertEquals("Ericsson",bookingById.getLastname(),"Неверная страница");
        assertEquals(381,bookingById.getTotalprice(),"Неверная сумма");
        assertEquals(false,bookingById.isDepositpadid(),"Неверное общее кол-во страниц");
        assertEquals("Breakfast",bookingById.getAdditionalneeds(),"Неверное значение");
        assertEquals("2019-01-08",bookingById.getBookingdates().getCheckin(),"Неверная дата последнего посещения");
        assertEquals("2020-04-27",bookingById.getBookingdates().getCheckout(),"Неверная дата окончания последней сессии");

    }
}
