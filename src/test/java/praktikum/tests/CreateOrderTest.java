package praktikum.tests;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.BaseTest;
import praktikum.models.Order;
import praktikum.models.User;
import praktikum.steps.OrderStep;
import praktikum.steps.UserStep;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class CreateOrderTest extends BaseTest {

    private UserStep userStep;
    private OrderStep orderStep;
    private User testUser;
    private String accessToken;
    private String[] ingredientIds;

    @Before
    public void setUp() {
        // Инициализация шагов
        userStep = new UserStep();
        orderStep = new OrderStep();

        // Создание тестового пользователя
        testUser = new User(
                generateUniqueEmail(),
                "password123",
                "Test User"
        );

        // Регистрация пользователя
        Response createResponse = userStep.create(testUser);
        accessToken = userStep.extractAccessToken(createResponse);

        // Получение списка ингредиентов
        Response ingredientsResponse = orderStep.getIngredients();
        ingredientIds = orderStep.extractIngredientIds(ingredientsResponse);

        // Проверка наличия ингредиентов
        if (ingredientIds == null || ingredientIds.length == 0) {
            throw new RuntimeException("Нет доступных ингредиентов для тестирования");
        }
    }

    @After
    public void tearDown() {
        // Удаление пользователя после тестов
        if (accessToken != null) {
            userStep.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверка создания заказа с передачей токена авторизации")
    public void createOrderWithAuthSuccess() {
        // Берем первые два ингредиента
        String[] orderIngredients = {ingredientIds[0], ingredientIds[1]};
        Order order = new Order(orderIngredients);

        Response response = orderStep.createWithAuth(accessToken, order);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue())
                .body("name", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка создания заказа без передачи токена авторизации")
    public void createOrderWithoutAuthSuccess() {
        String[] orderIngredients = {ingredientIds[0], ingredientIds[1]};
        Order order = new Order(orderIngredients);

        Response response = orderStep.createWithoutAuth(order);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue())
                .body("name", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами")
    @Description("Проверка создания заказа с передачей нескольких ингредиентов")
    public void createOrderWithIngredientsSuccess() {
        // Берем три ингредиента (сколько есть)
        int count = Math.min(3, ingredientIds.length);
        String[] orderIngredients = new String[count];

        for (int i = 0; i < count; i++) {
            orderIngredients[i] = ingredientIds[i];
        }

        Order order = new Order(orderIngredients);
        Response response = orderStep.createWithAuth(accessToken, order);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("order.ingredients", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Попытка создания заказа без указания ингредиентов")
    public void createOrderWithoutIngredientsError() {
        String[] emptyIngredients = {};
        Order order = new Order(emptyIngredients);

        Response response = orderStep.createWithAuth(accessToken, order);

        response.then()
                .statusCode(SC_BAD_REQUEST)  // Код 400
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Попытка создания заказа с невалидными хешами ингредиентов")
    public void createOrderWithInvalidIngredientHashError() {
        String[] invalidIngredients = {"invalid_hash_123", "wrong_hash_456"};
        Order order = new Order(invalidIngredients);

        Response response = orderStep.createWithAuth(accessToken, order);

        response.then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);  // Код 500
    }
}