package praktikum.models;

public class Order {
    private String[] ingredients;

    // Конструктор с ингредиентами
    public Order(String[] ingredients) {
        this.ingredients = ingredients;
    }

    // Пустой конструктор
    public Order() {
        this.ingredients = new String[0];
    }

    // Геттер для ингредиентов
    public String[] getIngredients() {
        return ingredients;
    }

    // Сеттер для ингредиентов
    public void setIngredients(String[] ingredients) {
        this.ingredients = ingredients;
    }

    // Метод для преобразования массива ингредиентов в JSON строку
    public String ingredientsToJson() {
        if (ingredients == null || ingredients.length == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < ingredients.length; i++) {
            sb.append("\"").append(ingredients[i]).append("\"");
            if (i < ingredients.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}