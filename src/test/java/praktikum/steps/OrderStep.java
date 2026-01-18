package praktikum.steps;

import io.restassured.response.Response;
import praktikum.models.Order;

import java.util.List;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;

public class OrderStep {

    // Создание заказа с авторизацией
    public Response createWithAuth(String accessToken, Order order) {
        String requestBody = "{\"ingredients\": " + order.ingredientsToJson() + "}";

        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(requestBody)
                .when()
                .post("/orders");
    }

    // Создание заказа без авторизации
    public Response createWithoutAuth(Order order) {
        String requestBody = "{\"ingredients\": " + order.ingredientsToJson() + "}";

        return given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post("/orders");
    }

    // Получение списка ингредиентов
    public Response getIngredients() {
        return given()
                .when()
                .get("/ingredients");
    }

    // Извлечение ID ингредиентов из ответа - ИСПРАВЛЕНО!
    public String[] extractIngredientIds(Response response) {
        // REST Assured возвращает List<String>, нужно преобразовать в массив
        List<String> ingredientList = response.then().extract().path("data._id");

        if (ingredientList == null || ingredientList.isEmpty()) {
            return new String[0]; // Возвращаем пустой массив
        }

        // Преобразуем List в массив
        return ingredientList.toArray(new String[0]);
    }

    // Создание заказа с массивом ингредиентов
    public Response createWithIngredients(String accessToken, String[] ingredients) {
        Order order = new Order(ingredients);
        return createWithAuth(accessToken, order);
    }

    // Создание пустого заказа
    public Response createEmptyOrder(String accessToken) {
        Order emptyOrder = new Order(new String[0]);
        return createWithAuth(accessToken, emptyOrder);
    }

    // Альтернативный вариант: вернуть List<String>
    public List<String> extractIngredientIdsAsList(Response response) {
        return response.then().extract().path("data._id");
    }
}