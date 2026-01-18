package praktikum.tests;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.BaseTest;
import praktikum.models.User;
import praktikum.steps.UserStep;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class CreateUserTest extends BaseTest {

    private UserStep userStep;
    private User testUser;
    private String accessToken;

    @Before
    public void setUp() {
        // Инициализируем шаги и тестовые данные
        userStep = new UserStep();
        testUser = new User(
                generateUniqueEmail(),  // Уникальный email
                "password123",          // Пароль
                "Test User"             // Имя
        );
    }

    @After
    public void tearDown() {
        // Очистка после теста: удаляем пользователя, если он был создан
        if (accessToken != null) {
            userStep.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка успешного создания пользователя с валидными данными")
    public void createUniqueUserSuccess() {
        // Шаг 1: Создаем пользователя
        Response response = userStep.create(testUser);

        // Шаг 2: Проверяем успешный ответ
        response.then()
                .statusCode(SC_OK)                           // Проверяем код 200
                .body("success", equalTo(true))             // Проверяем поле success
                .body("user.email", equalTo(testUser.getEmail()))  // Проверяем email
                .body("user.name", equalTo(testUser.getName()))    // Проверяем имя
                .body("accessToken", notNullValue())        // Проверяем наличие токена
                .body("refreshToken", notNullValue());      // Проверяем refresh токен

        // Сохраняем токен для очистки
        accessToken = userStep.extractAccessToken(response);
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Description("Попытка создания пользователя с уже существующим email")
    public void createDuplicateUserError() {
        // Шаг 1: Создаем первого пользователя
        Response firstResponse = userStep.create(testUser);
        accessToken = userStep.extractAccessToken(firstResponse);

        // Шаг 2: Пытаемся создать такого же пользователя
        Response duplicateResponse = userStep.create(testUser);

        // Шаг 3: Проверяем ошибку
        duplicateResponse.then()
                .statusCode(SC_FORBIDDEN)                    // Код 403
                .body("success", equalTo(false))            // success = false
                .body("message", equalTo("User already exists"));  // Сообщение об ошибке
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Попытка создания пользователя без обязательного поля password")
    public void createUserWithoutPasswordError() {
        // Создаем пользователя без пароля
        User userWithoutPassword = new User(
                generateUniqueEmail(),
                "",                    // Пустой пароль
                "Test User"
        );

        Response response = userStep.create(userWithoutPassword);

        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Попытка создания пользователя без обязательного поля email")
    public void createUserWithoutEmailError() {
        // Создаем пользователя без email
        User userWithoutEmail = new User(
                "",                    // Пустой email
                "password123",
                "Test User"
        );

        Response response = userStep.create(userWithoutEmail);

        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Попытка создания пользователя без обязательного поля name")
    public void createUserWithoutNameError() {
        // Создаем пользователя без имени
        User userWithoutName = new User(
                generateUniqueEmail(),
                "password123",
                ""                     // Пустое имя
        );

        Response response = userStep.create(userWithoutName);

        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}