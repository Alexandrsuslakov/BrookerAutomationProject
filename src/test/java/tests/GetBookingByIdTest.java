package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.BookingById;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GetBookingByIdTest {
    private static final Logger logger = LoggerFactory.getLogger(GetBookingByIdTest.class); // Логгер
    private APIClient apiClient;
    private ObjectMapper objectMapper; // Объявление переменной


    //Иницилизация API клиента перед каждым тестом
    @BeforeEach
    public void setup() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
    }

    //Тест на получение bookingid()
    @ParameterizedTest
    @CsvSource({
            "3262"
    })
    public void testGetBookingById(int bookingId) throws Exception {
        logger.debug("Запуск теста для bookingId: {}", bookingId); // Логирование начала теста

        Response response = apiClient.getBookingById(bookingId);
        logger.info("Получен ответ: {}", response.asString()); // Логирование ответа

        //Десериализуем тело ответа в список объектов
        BookingById bookingById = objectMapper.readValue(response.asString(), BookingById.class);
        logger.debug("Статус-код ответа: {}", response.getStatusCode()); // Логирование статус-кода

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
        assertEquals("Mark", bookingById.getFirstname(), "Имя пользователя не совпадает");
        assertEquals("Ericsson", bookingById.getLastname(), "Неверная страница");
        assertEquals(381, bookingById.getTotalprice(), "Неверная сумма");
        assertEquals(false, bookingById.isDepositpadid(), "Неверное общее кол-во страниц");
        assertEquals("Breakfast", bookingById.getAdditionalneeds(), "Неверное значение");
        assertEquals("2019-01-08", bookingById.getBookingdates().getCheckin(), "Неверная дата последнего посещения");
        assertEquals("2020-04-27", bookingById.getBookingdates().getCheckout(), "Неверная дата окончания последней сессии");

    }
}
