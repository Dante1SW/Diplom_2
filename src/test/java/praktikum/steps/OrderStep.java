package praktikum.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import praktikum.constants.Endpoints;
import praktikum.models.Order;

import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderStep {

    @Step("Создание заказа с авторизацией")
    public Response createWithAuth(String accessToken, Order order) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(order) // Используем сериализацию GSON
                .when()
                .post(Endpoints.ORDERS);
    }

    @Step("Создание заказа без авторизации")
    public Response createWithoutAuth(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order) // Используем сериализацию GSON
                .when()
                .post(Endpoints.ORDERS);
    }

    @Step("Получение списка ингредиентов")
    public Response getIngredients() {
        return given()
                .when()
                .get(Endpoints.INGREDIENTS);
    }

    @Step("Извлечение ID ингредиентов из ответа")
    public String[] extractIngredientIds(Response response) {
        List<String> ingredientList = response.then().extract().path("data._id");

        if (ingredientList == null || ingredientList.isEmpty()) {
            return new String[0];
        }

        return ingredientList.toArray(new String[0]);
    }

    @Step("Создание заказа с массивом ингредиентов")
    public Response createWithIngredients(String accessToken, String[] ingredients) {
        Order order = new Order(ingredients);
        return createWithAuth(accessToken, order);
    }


}