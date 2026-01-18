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

public class LoginUserTest extends BaseTest {

    private UserStep userStep;
    private User testUser;
    private String accessToken;

    @Before
    public void setUp() {
        // Инициализация
        userStep = new UserStep();
        testUser = new User(
                generateUniqueEmail(),
                "password123",
                "Test User"
        );

        // Создаем пользователя перед тестами входа
        Response createResponse = userStep.create(testUser);
        accessToken = userStep.extractAccessToken(createResponse);
    }

    @After
    public void tearDown() {
        // Удаляем пользователя после тестов
        if (accessToken != null) {
            userStep.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Вход под существующим пользователем")
    @Description("Успешный вход с валидными учетными данными")
    public void loginWithExistingUserSuccess() {
        Response response = userStep.login(testUser);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(testUser.getEmail()))
                .body("user.name", equalTo(testUser.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Вход с неверным паролем")
    @Description("Попытка входа с неправильным паролем")
    public void loginWithWrongPasswordError() {
        // Создаем пользователя с неправильным паролем
        User wrongPasswordUser = new User(
                testUser.getEmail(),     // Правильный email
                "wrongpassword",         // Неправильный пароль
                testUser.getName()       // Правильное имя
        );

        Response response = userStep.login(wrongPasswordUser);

        response.then()
                .statusCode(SC_UNAUTHORIZED)  // Код 401
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Вход с неверным email")
    @Description("Попытка входа с несуществующим email")
    public void loginWithWrongEmailError() {
        Response response = userStep.login("wrong@example.com", testUser.getPassword());

        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Вход без пароля")
    @Description("Попытка входа без указания пароля")
    public void loginWithoutPasswordError() {
        Response response = userStep.login(testUser.getEmail(), "");

        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}