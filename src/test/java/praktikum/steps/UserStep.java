package praktikum.steps;

import io.restassured.response.Response;
import praktikum.models.User;

import static io.restassured.RestAssured.given;

public class UserStep {

    // Создание нового пользователя
    public Response create(User user) {
        return given()
                .header("Content-type", "application/json")  // Указываем тип контента
                .body(user)                                  // Передаем объект User (автоматически конвертируется в JSON)
                .when()
                .post("/auth/register");                     // Отправляем POST запрос
    }

    // Вход пользователя (с объектом User)
    public Response login(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/auth/login");
    }

    // Вход пользователя (с email и паролем)
    public Response login(String email, String password) {
        String requestBody = String.format(
                "{\"email\": \"%s\", \"password\": \"%s\"}",
                email, password
        );

        return given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post("/auth/login");
    }

    // Удаление пользователя
    public Response delete(String accessToken) {
        return given()
                .header("Authorization", accessToken)  // Передаем токен в заголовке
                .when()
                .delete("/auth/user");
    }

    // Извлечение access token из ответа
    public String extractAccessToken(Response response) {
        return response.then().extract().path("accessToken");
    }

    // Извлечение сообщения об ошибке
    public String extractErrorMessage(Response response) {
        return response.then().extract().path("message");
    }
}