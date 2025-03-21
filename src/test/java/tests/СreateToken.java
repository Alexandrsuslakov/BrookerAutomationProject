package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class СreateToken {

    private APIClient apiClient;
    private ObjectMapper objectMapper; // Объявление переменной


    //Иницилизация API клиента перед каждым тестом
    @BeforeEach
    public void setup() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
        apiClient.createToken("admin", "password123");
    }
    @Test
    public void testTokenIsReceived() {
        String token = apiClient.getToken();
        assertNotNull(token, "Токен не должен быть null");
        System.out.println("Полученный токен: " + token);
    }
}
