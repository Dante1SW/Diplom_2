package praktikum.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import praktikum.constants.Endpoints;
import praktikum.models.User;

import static io.restassured.RestAssured.given;

public class UserStep {

    @Step("Создание нового пользователя")
    public Response create(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(Endpoints.REGISTER);
    }

    @Step("Вход пользователя (с объектом User)")
    public Response login(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(Endpoints.LOGIN);
    }

    @Step("Вход пользователя (с email и паролем)")
    public Response login(String email, String password) {
        String requestBody = String.format(
                "{\"email\": \"%s\", \"password\": \"%s\"}",
                email, password
        );

        return given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post(Endpoints.LOGIN);
    }

    @Step("Удаление пользователя")
    public Response delete(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .delete(Endpoints.USER);
    }

    @Step("Извлечение access token из ответа")
    public String extractAccessToken(Response response) {
        return response.then().extract().path("accessToken");
    }

    @Step("Извлечение сообщения об ошибке")
    public String extractErrorMessage(Response response) {
        return response.then().extract().path("message");
    }
}