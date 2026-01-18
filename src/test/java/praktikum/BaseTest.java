package praktikum;


import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;


import org.junit.Before;
import org.junit.BeforeClass;


public class BaseTest {

    // Базовый URL API Stellar Burgers
    protected static final String BASE_URL = "https://stellarburgers.education-services.ru/api";


    @BeforeClass
    public static void setupAll() {
        RestAssured.baseURI = BASE_URL;
    }

    @Before
    public void setUp() {
        // Добавляем логирование и Allure отчеты
        RestAssured.filters(
                new RequestLoggingFilter(),
                new ResponseLoggingFilter(),
                new AllureRestAssured()
        );
    }

    // Метод для генерации уникального email
    protected String generateUniqueEmail() {
        return "test_" + System.currentTimeMillis() + "@example.com";
    }
}